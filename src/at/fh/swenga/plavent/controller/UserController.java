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
import org.springframework.web.bind.annotation.RequestParam;

import at.fh.swenga.plavent.model.User;
import at.fh.swenga.plavent.model.UserRole;
import at.fh.swenga.plavent.repo.UserRepository;
import at.fh.swenga.plavent.repo.UserRoleRepository;

/**
 * @author Gregor Fernbach:
 * 
 *         Controller of the user management
 *
 */

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class UserController {

	/**
	 * CURRENTLY WORKED ON BY FERNBACH16
	 */

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserRoleRepository userRoleRepo;

	public UserController() {
	}

	@Secured({ "ROLE_GUEST" })
	@RequestMapping(value = { "showUserManagement" })
	public String showAllUsers(Model model, Authentication authentication) {

		// If User is ins Role 'ADMIN' show all users
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			model.addAttribute("users", userRepo.findAll());
			model.addAttribute("message",
					"Currently there are <strong>" + userRepo.findAll().size() + "</strong> active Users and <strong>"
							+ userRepo.findByEnabledFalse().size() + " </strong> inactive users");
		} else {
			model.addAttribute("users", userRepo.findFirstByUsername(authentication.getName()));
		}

		return "userManagement";
	}

	@RequestMapping(value = { "showRegisterIssues" })
	public String showRegisterIssues(Model model, Authentication authentication) {

		return "login";
	}

	/**
	 * MAIN FUNCTIONALITIES editUser deleteExistingUser showChangePasswordForm
	 */
	@Secured({ "ROLE_GUEST" })
	@GetMapping(value = "/editUser")
	public String editUser(Model model, @RequestParam String username, Authentication authentication) {

		User user = userRepo.findFirstByUsername(username);

		if (user != null) {
			if ((user.getUsername().equalsIgnoreCase(authentication.getName())
					|| authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
					&& user.isEnabled()) {
				model.addAttribute("user", user);

				if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
					model.addAttribute("hasRoleAdmin", user.getRoleList().contains(new UserRole("ROLE_ADMIN", null)));
					model.addAttribute("hasRoleHost", user.getRoleList().contains(new UserRole("ROLE_HOST", null)));
					model.addAttribute("hasRoleGuest", user.getRoleList().contains(new UserRole("ROLE_GUEST", null)));
				}

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

	@Secured({ "ROLE_GUEST" })
	@PostMapping(value = "/editUser")
	public String editUser(@RequestParam(name = "ur_guest", required = true) boolean isGuest,
			@RequestParam(name = "ur_host", required = false) boolean isHost,
			@RequestParam(name = "ur_admin", required = false) boolean isAdmin, @Valid User editUserModel,
			BindingResult bindingResult, Model model, Authentication authentication) {

		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			return showAllUsers(model, authentication);
		}

		List<UserRole> roles = new ArrayList<UserRole>();
		User user = userRepo.findFirstByUsername(editUserModel.getUsername());

		if (user != null) {
			if ((user.getUsername().equalsIgnoreCase(authentication.getName())
					|| authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
							&& user.isEnabled())) {

				if (isGuest == true) {
					UserRole role = userRoleRepo.findFirstByRoleName("ROLE_GUEST");
					if (role != null)
						roles.add(role);
				}
				if (isGuest == true && isHost == true) {
					UserRole role = userRoleRepo.findFirstByRoleName("ROLE_HOST");
					if (role != null)
						roles.add(role);
				}
				if (isAdmin == true && isGuest == true && isAdmin == true) {
					UserRole role = userRoleRepo.findFirstByRoleName("ROLE_ADMIN");
					if (role != null)
						roles.add(role);
				}
			}

			user.setFirstname(editUserModel.getFirstname());
			user.setLastname(editUserModel.getLastname());
			user.seteMail(editUserModel.geteMail());
			user.setTelNumber(editUserModel.getTelNumber());
			if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
				user.setRoleList(roles);

			userRepo.save(user);
			model.addAttribute("message", "Changed User " + editUserModel.getUsername());
		} else {
			model.addAttribute("warningMessage",
					"Not allowed to edit User with username " + editUserModel.getUsername() + "!");
		}
		return showAllUsers(model, authentication);
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/deleteUser")
	public String deleteUser(@Valid User editUserModel, Model model, BindingResult bindingResult,
			Authentication authentication) {

		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			return showAllUsers(model, authentication);
		}

		User user = userRepo.findFirstByUsername(editUserModel.getUsername());
		if (user.isEnabled() == false) {
			model.addAttribute("errorMessage", "User does not exist! <" + editUserModel.getUsername() + ">");
			return showAllUsers(model, authentication);
		}
		if (user.getUsername().equalsIgnoreCase(authentication.getName())) {
			model.addAttribute("errorMessage", "You cannot delete yourself! <" + editUserModel.getUsername() + ">");
		} else {
			user.setEnabled(false);
			userRepo.save(user);
			model.addAttribute("message", "User " + editUserModel.getUsername() + "sucessfully deleted");
		}
		return showAllUsers(model, authentication);
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping("/reactivateUser")
	public String reactivateUser(@Valid User editUserModel, Model model, BindingResult bindingResult,
			Authentication authentication) {

		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			return showAllUsers(model, authentication);
		}

		User user = userRepo.findFirstByUsername(editUserModel.getUsername());
		if (user == null || user.isEnabled()) {
			model.addAttribute("errorMessage",
					"User does not exist or is already reactivated! <" + editUserModel.getUsername() + ">");
			return showAllUsers(model, authentication);
		} else {
			user.setEnabled(true);
			userRepo.save(user);
			model.addAttribute("message", "User " + editUserModel.getUsername() + "sucessfully reactivated");
		}
		return showAllUsers(model, authentication);
	}

	@GetMapping("/registerUser")
	public String registerUser(Model model, Authentication authentication) {

		return "registerUser";

	}

	@PostMapping("/registerUser")
	public String registerUser(@Valid User newUserModel, BindingResult bindingResult, Model model,
			Authentication authentication) {

		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			return showRegisterIssues(model, authentication);
		}

		if (userRepo.findFirstByUsername(newUserModel.getUsername()) != null) {
			model.addAttribute("warningMessage", "User could not be registered!");
		} else {

			UserRole role = userRoleRepo.findFirstByRoleName("ROLE_GUEST");
			if (role != null) {

				List<UserRole> roles = new ArrayList<UserRole>();
				roles.add(role);
				newUserModel.setRoleList(roles);

				newUserModel.encryptPassword();
				newUserModel.setEnabled(true);
			}
			userRepo.save(newUserModel);
			model.addAttribute("message", "Registered User " + newUserModel.getUsername());
		}
		return showRegisterIssues(model, authentication);
	}

	@Secured({ "ROLE_ADMIN" })
	@GetMapping("/createUser")
	public String createNewUser(Model model, Authentication authentication) {

		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
			return "createModifyUser";
		else {
			model.addAttribute("warningMessage", "Not allowed to create new Users ");
			return showAllUsers(model, authentication);
		}
	}

	@Secured({ "ROLE_ADMIN" })
	@PostMapping("/createUser")
	public String createNewUser(@RequestParam(name = "ur_guest", required = true) boolean isGuest,
			@RequestParam(name = "ur_host", required = false) boolean isHost,
			@RequestParam(name = "ur_admin", required = false) boolean isAdmin, @Valid User newUser,
			BindingResult bindingResult, Model model, Authentication authentication) {

		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			return showRegisterIssues(model, authentication);
		}

		if (userRepo.findFirstByUsername(newUser.getUsername()) != null) {
			model.addAttribute("errorMessage", "User already exists!");
		}

		else {
			List<UserRole> roles = new ArrayList<UserRole>();
			if (isGuest) {
				UserRole role = userRoleRepo.findFirstByRoleName("ROLE_GUEST");
				if (role != null)
					roles.add(role);
			}
			if (isGuest && isHost) {
				UserRole role = userRoleRepo.findFirstByRoleName("ROLE_HOST");
				if (role != null)
					roles.add(role);
			}
			if (isGuest && isHost && isAdmin) {
				UserRole role = userRoleRepo.findFirstByRoleName("ROLE_ADMIN");
				if (role != null)
					roles.add(role);
			}

			newUser.encryptPassword();
			newUser.setRoleList(roles);
			newUser.setEnabled(true);

			userRepo.save(newUser);
			model.addAttribute("message", "New user " + newUser.getUsername() + "added.");

		}
		return showAllUsers(model, authentication);
	}

	@Secured({ "ROLE_GUEST" })
	@GetMapping("/changePassword")
	public String changePassword(@RequestParam String username, Model model, Authentication authentication) {

		User user = userRepo.findFirstByUsername(username);

		if ((user == null) || (user.isEnabled() == false)) {
			model.addAttribute("errorMessage", "Error while reading User!");
			return showAllUsers(model, authentication);
		} else {

			if (user.getUsername().equalsIgnoreCase(authentication.getName())
					|| authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
				model.addAttribute("user", user);
				return "changePassword";
			} else {
				model.addAttribute("warningMessage", "Not allowed to change password for " + user.getUsername() + "!");
				return showAllUsers(model, authentication);
			}
		}
	}

	/*
	 * @Secured({ "ROLE_GUEST" })
	 * 
	 * @PostMapping("/changePassword") public String changePassword(@RequestParam
	 * String password, @Valid User cPWUser, BindingResult bindingResult, Model
	 * model, Authentication authentication) {
	 * 
	 * if (bindingResult.hasErrors()) { String errorMessage = ""; for (FieldError
	 * fieldError : bindingResult.getFieldErrors()) { errorMessage +=
	 * fieldError.getField() + " is invalid<br>"; }
	 * model.addAttribute("errorMessage", errorMessage); return
	 * showRegisterIssues(model, authentication); }
	 * 
	 * User user = userRepo.findFirstByUsername(cPWUser.getUsername());
	 * System.out.println(cPWUser.getUsername()); if (user == null) {
	 * model.addAttribute("errorMessage", "Error while reading user!"); } else {
	 * 
	 * if (user.getUsername().equalsIgnoreCase(authentication.getName()) ||
	 * authentication.getAuthorities().contains(new
	 * SimpleGrantedAuthority("ROLE_ADMIN"))) { user.setPassword(password);
	 * user.encryptPassword(); userRepo.save(user); model.addAttribute("message",
	 * "The Password for <" + user.getUsername() + "> was successfully changed!"); }
	 * else { model.addAttribute("warningMessage",
	 * "Not allowed to change password for " + user.getUsername() + "!"); return
	 * showAllUsers(model, authentication); } } return showAllUsers(model,
	 * authentication); }
	 */

	@Secured({ "ROLE_GUEST" })
	@PostMapping(value = "/changePassword")
	public String changePassword(@Valid User editUserModel, @RequestParam String password, BindingResult bindingResult,
			Model model, Authentication authentication) {

		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			return showAllUsers(model, authentication);
		}

		User user = userRepo.findFirstByUsername(editUserModel.getUsername());

		if (user != null) {
			if ((user.getUsername().equalsIgnoreCase(authentication.getName())
					|| authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))) {

				user.setPassword(password);
				user.encryptPassword();
				userRepo.save(user);

				model.addAttribute("message", "Password successfully changed for User: " + editUserModel.getUsername());
			} else {
				model.addAttribute("warningMessage", "Error while reading User data!");
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
		ex.printStackTrace();
		return "error";
	}

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
