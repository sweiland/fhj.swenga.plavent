package at.fh.swenga.plavent.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import at.fh.swenga.plavent.dao.UserDao;
import at.fh.swenga.plavent.model.User;

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class UserManagementController {

	/**
	 * CURRENTLY WORKED ON BY FERNBACH16
	 */

	@Autowired
	private UserDao userDao;

	// Variables
	private static User currLoggedInUser;

	public UserManagementController() {
	}

	private List<User> users = new ArrayList<User>();

	/**
	 * 
	 * USER MANAGEMENT BASIC FUNCTIONALITIES verifyLogin showUserManagement
	 * 
	 */

	@RequestMapping(value =  "/login", method = RequestMethod.GET)
	public String handleLogin() {
		return "login";
	}

	// @Secured({ "ROLE_USER", "ROLE_ADMIN" })
	@RequestMapping(value = { "/", "showUserManagement" })
	public String showAllUsers(Model model) {
		/**if (!isLoggedIn(model)) {
			return "login";
		} **/
		model.addAttribute("users", userDao.findAllBy());
		return "userManagement";
	}

	/**
	 * MAIN FUNCTIONALITIES editUser deleteExistingUser showChangePasswordForm
	 */
	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	@GetMapping("/editUser")
	public String editUser(Model model, @RequestParam String username) {

		/**
		if (!isLoggedIn(model)) {
			return "login";
		}**/

		User user = userDao.findByUserName(username);

		if (user != null) {
			model.addAttribute("user", user);
			return "createModifyUser";
		} else {
			model.addAttribute("errorMessage", "Couldn't find user" + username);
			return showAllUsers(model);
		}
	}

	@Secured({ "ROLE_ADMIN" })
	@GetMapping("/deleteUser")
	public String deleteUser(Model model, @RequestParam String username) {
		User user = userDao.findByUserName(username);
		if (user == null) {
			model.addAttribute("errorMessage", "User does not exist! <" + username + ">");
		} else {
			userDao.deleteByUserName(username);
			model.addAttribute("warningMessage", "User " + username + "sucessfully deleted");
		}
		return showAllUsers(model);
	}

	/**
	@Secured({ "ROLE_USER", "ROLE_ADMIN" })
	@GetMapping("/showChangePasswordForm")
	public String showChangePasswortForm(Model model, @RequestParam String username) {
		if (!isLoggedIn(model)) {
			return "login";
		}

		User user = userDao.findByUserName(username);
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

**/
	/**
	 * HELPER FUNCTIONALITIES isLoggedIn getCurrentLoggedInUser errorsDetected
	 * 
	 **/
	/**
	public static boolean isLoggedIn(Model model) {
		if (currLoggedInUser != null) {
			model.addAttribute("currLoggedInUser", currLoggedInUser);
			return true;
		} else {
			model.addAttribute("warningMessage", "Currently not logged in!");
			return false;
		}
	}
	**/

	public static User getCurrentLoggedInUser() {
		return currLoggedInUser;
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


	/**
	 * INITAL FRAMEWORK BY HOEDLALE16
	 */
	/**
	 * 
	 * @RequestMapping(value = { "verifyLogin" }) public String
	 *                       verifyLoginData(Model model, @RequestParam String
	 *                       username, @RequestParam String password) throws
	 *                       Exception { // TODO: Check if enters user/pw matches to
	 *                       criterias and so on...
	 * 
	 *                       User user = userDao.verifyLogin(username, password); if
	 *                       (user == null) { model.addAttribute("errorMessage",
	 *                       "Username or password incorrect!"); return "login"; }
	 *                       else { currLoggedInUser = user;
	 *                       model.addAttribute("currLoggedInUser",
	 *                       currLoggedInUser); model.addAttribute("message",
	 *                       "Welcome " + currLoggedInUser.getFirstname() + "!");
	 *                       return "dashboard"; } }
	 * 
	 * @RequestMapping(value = { "logout" }) public String verifyLoginData(Model
	 *                       model) { currLoggedInUser = null; return "login"; }
	 * 
	 * 
	 *                       // Create new User @GetMapping("/showCreateUserForm")
	 *                       public String showNewUserForm(Model model) { if
	 *                       (!isLoggedIn(model)) { return "login"; }
	 * 
	 *                       return "createModifyUser"; }
	 * 
	 *                       // Edit User @GetMapping("/editUser") public String
	 *                       editUser(Model model, @RequestParam String username) {
	 * 
	 *                       if (!isLoggedIn(model)) { return "login"; }
	 * 
	 *                       User user = userDao.findByUserName(username);
	 * 
	 *                       if (user != null) { model.addAttribute("user", user);
	 *                       return "createModifyUser"; } else {
	 *                       model.addAttribute("errorMessage", "Couldn't find user"
	 *                       + username); return showAllUsers(model); } }
	 * 
	 * 
	 *                       // Change
	 *                       password @GetMapping("/showChangePasswordForm") public
	 *                       String showChangePasswortForm(Model
	 *                       model, @RequestParam String username) { if
	 *                       (!isLoggedIn(model)) { return "login"; }
	 * 
	 *                       User user = userDao.getUser(username); if (user !=
	 *                       null) { model.addAttribute("user", user); // TODO:
	 *                       Implement // return "changePassword" return "todo"; }
	 *                       else { model.addAttribute("errorMessage", "Couldn't
	 *                       find user" + username); return showAllUsers(model); }
	 * 
	 *                       }
	 * 
	 *                       // RequestMethod.POST => Create new
	 *                       User @PostMapping("/createNewUser") public String
	 *                       createNewUser(@Valid User newUserModel, BindingResult
	 *                       bindingResult, Model model) {
	 * 
	 *                       // Any errors? -> Create a String out of all errors and
	 *                       return to the page if (errorsDetected(model,
	 *                       bindingResult)) return showAllUsers(model);
	 * 
	 *                       // Look for illness in the List. One available -> Error
	 *                       if (userDao.getUser(newUserModel.getUsername()) !=
	 *                       null) { model.addAttribute("errorMessage", "User
	 *                       already exists!"); } else {
	 *                       userDao.persist(newUserModel); //
	 *                       userManager.addUser(newUserModel);
	 *                       model.addAttribute("message", "New user " +
	 *                       newUserModel.getUsername() + " added."); }
	 * 
	 *                       return showAllUsers(model); }
	 * 
	 *                       // RequestMethod.POST => Update existing
	 *                       User @PostMapping("/modifyExistingUser") public String
	 *                       modifyUser(@Valid User changedUserModel, BindingResult
	 *                       bindingResult, Model model) {
	 * 
	 *                       // Check for errors if (errorsDetected(model,
	 *                       bindingResult)) return showAllUsers(model);
	 * 
	 *                       // Get the illness the user wants to change User user =
	 *                       userDao.getUser(changedUserModel.getUsername()); if
	 *                       (user == null) { model.addAttribute("errorMessage",
	 *                       "User does not exist! <" +
	 *                       changedUserModel.getUsername() + ">"); } else {
	 *                       userDao.merge(changedUserModel);
	 *                       model.addAttribute("message", "Changed user " +
	 *                       user.getUsername()); }
	 * 
	 *                       return showAllUsers(model); }
	 * 
	 * 
	 *                       // RequestMethod.POST => Update existing
	 *                       User @PostMapping("/changePasswordExistingUser") public
	 *                       String changePasswordFromUser(@Valid User
	 *                       changedUserModel, BindingResult bindingResult, Model
	 *                       model) { // Check for errors if (errorsDetected(model,
	 *                       bindingResult)) return showAllUsers(model);
	 * 
	 *                       // Get the illness the user wants to change User user =
	 *                       userDao.getUser(changedUserModel.getUsername()); if
	 *                       (user == null) { model.addAttribute("errorMessage",
	 *                       "User does not exist! <" +
	 *                       changedUserModel.getUsername() + ">"); } else { // TODO
	 *                       Change password which is stored in changedUserModel }
	 *                       return showAllUsers(model); }
	 * 
	 *                       // Filter userManagement @PostMapping("/filterUsers")
	 *                       public String filterUsers(Model model, @RequestParam
	 *                       String searchString) { if (!isLoggedIn(model)) { return
	 *                       "login"; }
	 * 
	 *                       model.addAttribute("users",
	 *                       userDao.getFilteredUsers(searchString)); return
	 *                       "userManagement"; }
	 * 
	 * @ExceptionHandler(Exception.class) public String handleAllException(Exception
	 *                                    ex) { return "error"; }
	 **/
}
