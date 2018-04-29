package at.fh.swenga.plavent.controller;

import java.security.MessageDigest;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.fh.swenga.plavent.dao.HappeningCategoryDao;
import at.fh.swenga.plavent.dao.HappeningStatusDao;
import at.fh.swenga.plavent.dao.UserDao;
import at.fh.swenga.plavent.dao.UserRoleDao;
import at.fh.swenga.plavent.model.HappeningCategory;
import at.fh.swenga.plavent.model.HappeningStatus;
import at.fh.swenga.plavent.model.User;
import at.fh.swenga.plavent.model.UserRole;

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class UserManagementController {

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private UserRoleDao userRoleDao;

	@Autowired
	private HappeningStatusDao happeningStatusDao;
	
	@Autowired
	private HappeningCategoryDao happeningCategoryDao;
	
	private User currLoggedInUser; // Instance to hold User which is currently logged in this session

	public UserManagementController() {
	}
	
	
	@RequestMapping(value = { "preparePlavent" })
	public String preparePlavent(Model model) throws Exception {
		
		//Create useroles if required
		UserRole roleAdmin = userRoleDao.getUserRole("ADMIN");
		if(roleAdmin == null)
			roleAdmin = new UserRole("ADMIN","The role to manage the system");
		UserRole roleHost = userRoleDao.getUserRole("HOST");
		if(roleHost == null)
			roleHost = new UserRole("HOST","The role to create happening and manage them");
		UserRole roleGuest = userRoleDao.getUserRole("GUEST");
		if(roleGuest == null)
			roleGuest = new UserRole("GUEST","The role to be a guest at happenings");
		
		
		//Create Happing status values 
		HappeningStatus hsActive = happeningStatusDao.getHappeningStatus("ACTIVE");
		if (hsActive == null)
			hsActive = new HappeningStatus("ACTIVE","The happening will happen as planned!");
		
		HappeningStatus hsDeleted= happeningStatusDao.getHappeningStatus("DELETED");
		if (hsDeleted == null)
			hsDeleted = new HappeningStatus("DELETED","The happening is cancelled!");
		
		//Create a default happening cateogry
		HappeningCategory catUnAssigned  = happeningCategoryDao.getCategory("Unassigned");
		if (catUnAssigned == null)
			catUnAssigned = new HappeningCategory("Unassigned","Not specified ");
		
		
		//Create overall admin if required
		if(userDao.getUser("admin") == null) {
			MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");
			String passwordHash = new String(md5.digest("admin".getBytes()));
			User administrator = new User("admin",passwordHash,"Administrator","Administrator",roleAdmin);

			userDao.persist(administrator);
		}
		
		model.addAttribute("warningMessage","Environment created - Start planning!");
		return "login";
	}


	/**
	 * General function to verify if user is logged in
	 * 
	 * @param model
	 * @return
	 */
	private boolean isLoggedIn(Model model) {
		if (currLoggedInUser != null) {
			return true;
		} else {
			model.addAttribute("warningMessage", "Currently not logged in!");
			return false;
		}
	}

	private boolean errorsDetected(Model model, BindingResult bindingResult) {
		// Any errors? -> Create a String out of all errors and return to the page
		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid";
			}
			model.addAttribute("errorMessage", errorMessage);
			return true;
		}
		return false;
	}

	@RequestMapping(value = { "verifyLogin" })
	public String verifyLoginData(Model model, @RequestParam String username, @RequestParam String password) throws Exception {
		// TODO: Check if enters user/pw matches to criterias and so on...

		User user = userDao.verifyLogin(username, password);
		if (user == null) {
			model.addAttribute("errorMessage", "Username or password incorrect!");
			return "login";
		} else {
			currLoggedInUser = user;
			model.addAttribute("message", "Welcome " + currLoggedInUser.getFirstname() + "!");
			return "dashboard";
		}
	}

	@RequestMapping(value = { "logout" })
	public String verifyLoginData(Model model) {
		currLoggedInUser = null;
		return "login";
	}

	@RequestMapping(value = { "showUserManagement" })
	public String showAllUsers(Model model) {
		// hoedlale16: Verify that user is logged in
		if (!isLoggedIn(model)) {
			return "login";
		}

		model.addAttribute("currLoggedInuser", currLoggedInUser);
		model.addAttribute("users", userDao.getUsers());
		return "userManagement";
	}

	// Create new User
	@GetMapping("/showCreateUserForm")
	public String showNewUserForm(Model model) {
		if (!isLoggedIn(model)) {
			return "login";
		}

		return "createModifyUser";
	}

	// Modify User
	@GetMapping("/showModifyUserForm")
	public String showEditUserForm(Model model, @RequestParam String username) {

		if (!isLoggedIn(model)) {
			return "login";
		}

		User user = userDao.getUser(username);

		if (user != null) {
			model.addAttribute("user", user);
			return "createModifyUser";
		} else {
			model.addAttribute("errorMessage", "Couldn't find user" + username);
			return showAllUsers(model);
		}
	}

	// Change password
	@GetMapping("/showChangePasswordForm")
	public String showChangePasswortForm(Model model, @RequestParam String username) {
		if (!isLoggedIn(model)) {
			return "login";
		}

		
		User user = userDao.getUser(username);
		if (user != null) {
			model.addAttribute("user", user);
			// TODO: Implement
			// return "changePassword"
			return "todo";
		} else {
			model.addAttribute("errorMessage", "Couldn't find user" + username);
			return showAllUsers(model);
		}
		
	}

	// RequestMethod.POST => Create new User
	@PostMapping("/createNewUser")
	public String createNewUser(@Valid User newUserModel, BindingResult bindingResult, Model model) {

		// Any errors? -> Create a String out of all errors and return to the page
		if (errorsDetected(model, bindingResult))
			return showAllUsers(model);

		// Look for illness in the List. One available -> Error
		if (userDao.getUser(newUserModel.getUsername()) != null) {
			model.addAttribute("errorMessage", "User already exists!");
		} else {
			userDao.persist(newUserModel);
			//userManager.addUser(newUserModel);
			model.addAttribute("message", "New user " + newUserModel.getUsername() + " added.");
		}

		return showAllUsers(model);
	}

	// RequestMethod.POST => Update existing User
	@PostMapping("/modifyExistingUser")
	public String modifyUser(@Valid User changedUserModel, BindingResult bindingResult, Model model) {

		// Check for errors
		if (errorsDetected(model, bindingResult))
			return showAllUsers(model);

		// Get the illness the user wants to change
		User user = userDao.getUser(changedUserModel.getUsername());
		if (user == null) {
			model.addAttribute("errorMessage", "User does not exist! <" + changedUserModel.getUsername() + ">");
		} else {
			userDao.merge(changedUserModel);
			model.addAttribute("message", "Changed user " + user.getUsername());
		}

		return showAllUsers(model);
	}

	// Delete user
	@GetMapping("/deleteExistingUser")
	public String deleteUser(Model model, @RequestParam String username) {
		User user = userDao.getUser(username);
		if (user == null) {
			model.addAttribute("errorMessage", "User does not exist! <" + username + ">");
		} else {
			userDao.delete(user);
			model.addAttribute("warningMessage", "User " + username + "sucessfully deleted");
		}
		return showAllUsers(model);
	}

	// RequestMethod.POST => Update existing User
	@PostMapping("/changePasswordExistingUser")
	public String changePasswordFromUser(@Valid User changedUserModel, BindingResult bindingResult, Model model) {
		// Check for errors
		if (errorsDetected(model, bindingResult))
			return showAllUsers(model);

		// Get the illness the user wants to change
		User user = userDao.getUser(changedUserModel.getUsername());
		if (user == null) {
			model.addAttribute("errorMessage", "User does not exist! <" + changedUserModel.getUsername() + ">");
		} else {
			// TODO Change password which is stored in changedUserModel
		}
		return showAllUsers(model);
	}

	// Filter userManagement
	@PostMapping("/filterUsers")
	public String filterUsers(Model model, @RequestParam String searchString) {
		if (!isLoggedIn(model)) {
			return "login";
		}

		model.addAttribute("users", userDao.getFilteredUsers(searchString));
		model.addAttribute("currLoggedInuser", currLoggedInUser);
		return "userManagement";
	}

	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		return "error";
	}

}
