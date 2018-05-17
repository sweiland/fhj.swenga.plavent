package at.fh.swenga.plavent.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

	public UserManagementController() {
	}

	@Secured({ "ROLE_USER" })
	@RequestMapping(value = { "showUserManagement" })
	public String showAllUsers(Model model, Authentication authentication) {

		// If User is ins Role 'ADMIN' show all users
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			model.addAttribute("users", userDao.findAll());
		} else {
			model.addAttribute("users", userDao.findFirstByUsername(authentication.getName()));
		}

		return "userManagement";
	}

	/**
	 * MAIN FUNCTIONALITIES editUser deleteExistingUser showChangePasswordForm
	 */
	@Secured({ "ROLE_USER" })
	@GetMapping("/editUser")
	public String editUser(Model model, @RequestParam String username, Authentication authentication) {

		User user = userDao.findFirstByUsername(username);

		if (user != null) {

			// Check if user and logged in user is the same or logged in user is in role
			// ADMin
			if (user.getUsername().equalsIgnoreCase(authentication.getName())
					|| authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
				model.addAttribute("user", user);
				return "createModifyUser";
			} else {
				model.addAttribute("warningMessage", "Not allowed to edit " + username + "!");
				return showAllUsers(model, authentication);
			}
		} else {
			model.addAttribute("errorMessage", "Couldn't find user" + username);
			return showAllUsers(model, authentication);
		}
	}

	@Secured({ "ROLE_ADMIN" })
	@GetMapping("/deleteUser")
	public String deleteUser(Model model, @RequestParam String username, Authentication authentication) {
		User user = userDao.findFirstByUsername(username);
		if (user == null) {
			model.addAttribute("errorMessage", "User does not exist! <" + username + ">");
		} else {
			userDao.deleteByUserName(username);
			model.addAttribute("warningMessage", "User " + username + "sucessfully deleted");
		}
		return showAllUsers(model, authentication);
	}

	/**
	 * @Secured({ "ROLE_USER", "ROLE_ADMIN"
	 * }) @GetMapping("/showChangePasswordForm") public String
	 * showChangePasswortForm(Model model, @RequestParam String username) { if
	 * (!isLoggedIn(model)) { return "login"; }
	 * 
	 * User user = userDao.findFirstByUsername(username);
	 * 
	 * if (user != null) { model.addAttribute("user", user); // TODO: Implement //
	 * return "changePassword" return "todo"; } else {
	 * model.addAttribute("errorMessage", "Couldn't find user" + username); return
	 * showAllUsers(model); } }
	 * 
	 * // RequestMethod.POST => Create new User @PostMapping("/createNewUser")
	 * public String createNewUser(@Valid User newUserModel, BindingResult
	 * bindingResult, Model model) {
	 * 
	 * // Any errors? -> Create a String out of all errors and return to the page if
	 * (errorsDetected(model, bindingResult)) return showAllUsers(model);
	 * 
	 * // Look for illness in the List. One available -> Error if
	 * (userDao.findFirstByUsername(newUserModel.getUsername()) != null) {
	 * model.addAttribute("errorMessage", "User already exists!"); } else {
	 * userDao.save(newUserModel); //userManager.addUser(newUserModel);
	 * model.addAttribute("message", "New user " + newUserModel.getUsername() + "
	 * added."); } }
	 * 
	 * // RequestMethod.POST => Update existing
	 * User @PostMapping("/modifyExistingUser") public String modifyUser(@Valid User
	 * changedUserModel, BindingResult bindingResult, Model model) {
	 * 
	 * // Check for errors if (errorsDetected(model, bindingResult)) return
	 * showAllUsers(model);
	 * 
	 * // Get the illness the user wants to change User user =
	 * userDao.findFirstByUsername(changedUserModel.getUsername()); if (user ==
	 * null) { model.addAttribute("errorMessage", "User does not exist! <" +
	 * changedUserModel.getUsername() + ">"); } else {
	 * userDao.save(changedUserModel); model.addAttribute("message", "Changed user "
	 * + user.getUsername()); }
	 * 
	 * return showAllUsers(model); }
	 * 
	 * // Delete user @GetMapping("/deleteExistingUser") public String
	 * deleteUser(Model model, @RequestParam String username) { User user =
	 * userDao.findFirstByUsername(username); if (user == null) {
	 * model.addAttribute("errorMessage", "User does not exist! <" + username +
	 * ">");
	 **/

	// RequestMethod.POST => Update existing User
	@Secured({ "ROLE_USER" })
	@PostMapping("/changePasswordExistingUser")
	public String changePasswordFromUser(@Valid User changedUserModel, BindingResult bindingResult, Model model,
			Authentication authentication) {
		// Check for errors
		if (errorsDetected(model, bindingResult))
			return showAllUsers(model, authentication);

		// Get the illness the user wants to change
		User user = userDao.findFirstByUsername(changedUserModel.getUsername());
		if (user == null) {
			model.addAttribute("errorMessage", "User does not exist! <" + changedUserModel.getUsername() + ">");
		} else {

			// Check if user and logged in user is the same or logged in user is in role
			// ADMin
			if (user.getUsername().equalsIgnoreCase(authentication.getName())
					|| authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
				// TODO Change password which is stored in changedUserModel
			} else {
				model.addAttribute("warningMessage", "Not allowed to change password for " + user.getUsername() + "!");
				return showAllUsers(model, authentication);
			}
		}
		return showAllUsers(model, authentication);
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

	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		return "error";
	}

}
