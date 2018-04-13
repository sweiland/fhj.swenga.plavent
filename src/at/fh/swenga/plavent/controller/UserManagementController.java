package at.fh.swenga.plavent.controller;

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

import at.fh.swenga.plavent.model.UserManager;
import at.fh.swenga.plavent.model.UserModel;

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class UserManagementController {

	@Autowired
	private UserManager userManager;

	private UserModel currLoggedInUser; // Instance to hold User which is currently logged in this session

	public UserManagementController() {
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

	@RequestMapping(value = { "/" })
	public String showLoginPage(Model model) {

		/*
		 * hoedlale16: If user already set(logged in, show dashboard page otherwise show
		 * login page with a warning message
		 */
		if (currLoggedInUser != null) {
			return "dashboard";
		} else {
			// Show loginpage
			return "login";
		}
	}

	@RequestMapping(value = { "verifyLogin" })
	public String verifyLoginData(Model model, @RequestParam String username, @RequestParam String password) {
		// TODO: Check if enters user/pw matches to criterias and so on...

		UserModel user = userManager.verifyLogin(username, password);
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

	@RequestMapping(value = { "userManagement" })
	public String showAllUsers(Model model) {
		// hoedlale16: Verify that user is logged in
		if (!isLoggedIn(model)) {
			return "login";
		}

		model.addAttribute("currLoggedInuser", currLoggedInUser);
		model.addAttribute("users", userManager.getAllUsers());
		return "userManagement";
	}

	@PostMapping("/searchUsers")
	public String search(Model model, @RequestParam String searchString) {
		model.addAttribute("users", userManager.getFilteredUsers(searchString));
		model.addAttribute("currLoggedInuser", currLoggedInUser);
		return "userManagement";
	}

	@GetMapping("/createNewUser")
	public String showNewUserForm(Model model) {
		return "editUser";
	}

	// RequestMethod.POST)
	@PostMapping("/createNewUser")
	public String addIllness(@Valid UserModel newUserModel, BindingResult bindingResult, Model model) {

		// Any errors? -> Create a String out of all errors and return to the page
		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid";
			}
			model.addAttribute("errorMessage", errorMessage);

			return showAllUsers(model);
		}

		// Look for illness in the List. One available -> Error
		if ( userManager.getUser(newUserModel.getUsername()) != null) {
			model.addAttribute("errorMessage", "User already exists!");
		} else {
			userManager.addUser(newUserModel);
			model.addAttribute("message", "New user " + newUserModel.getUsername() + " added.");
		}

		return showAllUsers(model);
	}

	@GetMapping("/deleteUser")
	public String delete(Model model, @RequestParam String username) {
		boolean isRemoved = userManager.removeUser(username);

		if (isRemoved) {
			model.addAttribute("warningMessage", "User " + username + "sucessfully deleted");
		} else {
			model.addAttribute("errorMessage", "There is no User with username " + username);
		}

		return showAllUsers(model);
	}

	@GetMapping("/editUser")
	public String showEditUserForm(Model model, @RequestParam String username) {

		UserModel user = userManager.getUser(username);

		if (user != null) {
			model.addAttribute("user", user);
			return "editUser";
		} else {
			model.addAttribute("errorMessage", "Couldn't find user" + username);
			return showAllUsers(model);
		}
	}

	// RequestMethod.POST)
	@PostMapping("/editUser")
	public String postEditUserForm(@Valid UserModel changedUserModel, BindingResult bindingResult, Model model) {

		// Any errors? -> Create a String out of all errors and return to the page
		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid";
			}
			model.addAttribute("errorMessage", errorMessage);
			return showAllUsers(model);
		}

		// Get the illness the user wants to change
		UserModel user = userManager.getUser(changedUserModel.getUsername());
		if (user == null) {
			model.addAttribute("errorMessage", "User does not exist! <" + changedUserModel.getUsername() + ">");
		} else {
			// Change the attributes
			user.updateModel(changedUserModel);
			model.addAttribute("message", "Changed user " + user.getUsername());
		}

		return showAllUsers(model);
	}

	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {

		return "error";

	}

}
