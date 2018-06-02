package at.fh.swenga.plavent.controller;

import java.util.ArrayList;
//import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import javax.validation.Valid;

import org.apache.commons.codec.binary.StringUtils;
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
import org.springframework.web.multipart.MultipartFile;

import at.fh.swenga.plavent.model.ProfilePicture;
import at.fh.swenga.plavent.model.User;
import at.fh.swenga.plavent.model.UserRole;
import at.fh.swenga.plavent.repo.ProfilePictureRepository;
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

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserRoleRepository userRoleRepo;

	@Autowired
	private ProfilePictureRepository profilePictureRepo;

	public UserController() {
	}

	/**********************************************
	 * CRUD FUNCTIONALITIES
	 ********************************************** 
	 * C: createUser, registerUser R: showProfile, showUsermanagement U: editUser,
	 * changePassword, uploadProfilePicture D: deleteUser, reactivateUser
	 * 
	 **********************************************
	 * HELPER METHODS
	 **********************************************
	 *
	 * errorsDetected ExceptionHandler
	 * 
	 */

	// ******************** C:CREATE **************************************

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

	@GetMapping("/registerUser")
	public String registerUser(Model model, Authentication authentication) {

		return "registerUser";

	}

	@PostMapping("/registerUser")
	public String registerUser(@RequestParam(value = "file", required = false) MultipartFile file, @Valid User newUser,
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
			model.addAttribute("warningMessage", "User could not be registered!");
		} else {

			UserRole role = userRoleRepo.findFirstByRoleName("ROLE_GUEST");
			if (role != null) {

				List<UserRole> roles = new ArrayList<UserRole>();
				roles.add(role);
				newUser.setRoleList(roles);

				newUser.encryptPassword();
				newUser.setEnabled(true);
				model.addAttribute("message",
						"Registered User " + newUser.getUsername() + " without a Profile Picture.");
			}
			userRepo.save(newUser);

		}
		return showRegisterIssues(model, authentication);
	}

	// ******************** R:READ ****************************************

	@Secured({ "ROLE_GUEST" })
	@RequestMapping(value = { "showProfile" })
	public String showProfile(Model model, Authentication authentication) {

		/*
		 * final String IMAGES = "images"; final String TOMCAT_HOME_PROPERTY =
		 * "catalina.home"; final String TOMCAT_HOME_PATH =
		 * System.getProperty(TOMCAT_HOME_PROPERTY); final String IMAGES_PATH =
		 * TOMCAT_HOME_PATH + File.separator + IMAGES;
		 * 
		 * final File IMAGES_DIR = new File(IMAGES_PATH); final String
		 * IMAGES_DIR_ABSOLUTE_PATH = IMAGES_DIR.getAbsolutePath() + File.separator;
		 * 
		 * private void createImagesDirIfNeeded() { if (!IMAGES_DIR.exists()) {
		 * IMAGES_DIR.mkdirs(); } }
		 */
		User user = userRepo.findFirstByUsername(authentication.getName());
		model.addAttribute("user", user);
		if (user.getProfilePicture() != null) {

			Optional<ProfilePicture> ppOpt = profilePictureRepo.findById(user.getProfilePicture().getId());
			ProfilePicture pp = ppOpt.get();
			byte[] profilePicture = pp.getPic();
			
			StringBuilder sb = new StringBuilder();
			sb.append("data:image/jpeg;base64,");
			sb.append(Base64.encodeBase64String(profilePicture));
			String image = sb.toString();
			
			model.addAttribute("image", image);
		}
		return "viewProfile";
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping(value = { "showUserManagement" })
	public String showAllUsers(Model model, Authentication authentication) {

		model.addAttribute("users", userRepo.findAll());
		model.addAttribute("message",
				"Currently there are <strong>" + userRepo.findAll().size() + "</strong> active Users and <strong>"
						+ userRepo.findByEnabledFalse().size() + " </strong> inactive users");
		return "userManagement";
	}

	@RequestMapping(value = { "showRegisterIssues" })
	public String showRegisterIssues(Model model, Authentication authentication) {

		return "login";
	}

	// ******************** UPDATE ****************************************

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
				if (user.getProfilePicture() != null) {

					Optional<ProfilePicture> ppOpt = profilePictureRepo.findById(user.getProfilePicture().getId());
					ProfilePicture pp = ppOpt.get();
					byte[] profilePicture = pp.getPic();
					
					StringBuilder sb = new StringBuilder();
					sb.append("data:image/jpeg;base64,");
					sb.append(Base64.encodeBase64String(profilePicture));
					String image = sb.toString();
					
					model.addAttribute("image", image);
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

	@Secured({ "ROLE_GUEST" })
	@GetMapping("/uploadProfilePicture")
	public String uploadProfilePicture(Model model, @RequestParam("username") String username) {
		model.addAttribute("username", username);
		return "uploadProfilePicture";
	}

	@Secured({ "ROLE_GUEST" })
	@PostMapping("/uploadProfilePicture")
	public String uploadProfilePicture(Model model, Authentication authentication,
			@RequestParam("username") String username, @RequestParam("imageFile") MultipartFile imageFile) {

		try {

			Optional<User> userOpt = userRepo.findByUsername(username);

			if (!userOpt.isPresent()) {
				model.addAttribute("errorMessage", "Error while reading Data!");
				return "viewProfile";
			}

			User user = userOpt.get();

			// Already a Profile Picture available -> delete it
			if (user.getProfilePicture() != null) {
				profilePictureRepo.delete(user.getProfilePicture());
				// Don't forget to remove the relationship too
				user.setProfilePicture(null);
			}
			// Create a new document and set all available infos

			ProfilePicture pp = new ProfilePicture();
			pp.setName(user.getUsername() + "-profile-Picture");
			pp.setType(imageFile.getContentType());
			pp.setCreated(new Date());
			pp.setPic(imageFile.getBytes());
			user.setProfilePicture(pp);
			profilePictureRepo.save(pp);
			userRepo.save(user);
			model.addAttribute("message", "Profile Picture successfully uploaded.");

		} catch (Exception e) {

			model.addAttribute("errorMessage", "Error:" + e.getMessage());
		}

		return showProfile(model, authentication);
	}

	@Secured({ "ROLE_GUEST" })
	@GetMapping("/changePassword")
	public String changePassword(@RequestParam(name = "username") String username, Model model,
			Authentication authentication) {

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

	// ******************** D:DELETE ****************************************

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

	// ******************** HELPER METHODS ****************************************

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

	/*
	 * @ExceptionHandler()
	 * 
	 * @ResponseStatus(code = HttpStatus.FORBIDDEN) public String
	 * handle403(Exception ex) { ex.printStackTrace(); return "login"; }
	 */
}
