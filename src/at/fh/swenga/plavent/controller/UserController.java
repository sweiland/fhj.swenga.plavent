package at.fh.swenga.plavent.controller;

import java.util.ArrayList;
//import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

	@Autowired
	private MailSender mailSender;
	@Autowired
	private SimpleMailMessage templateMessage;

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
			model.addAttribute("warningMessage", "Not authorized to create new Users.");
			return showUserManagement(model, authentication);
		}
	}

	@Secured({ "ROLE_ADMIN" })
	@PostMapping("/createUser")
	public String createNewUser(@RequestParam(name = "role", required = false) String role, @Valid User newUser,
			BindingResult bindingResult, Model model, Authentication authentication) {

		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			return showUserManagement(model, authentication);
		}

		if (userRepo.findFirstByUsername(newUser.getUsername()) != null) {
			model.addAttribute("errorMessage", "User already exists!");
		} else {

			// Set role Guest in any way.
			List<UserRole> roles = new ArrayList<UserRole>();
			UserRole userRoleGuest = userRoleRepo.findFirstByRoleName("ROLE_GUEST");
			roles.add(userRoleGuest);

			if ("host".equals(role)) {
				roles.add(userRoleRepo.findFirstByRoleName("ROLE_HOST"));
			}

			if ("admin".equals(role)) {
				roles.add(userRoleRepo.findFirstByRoleName("ROLE_HOST"));
				roles.add(userRoleRepo.findFirstByRoleName("ROLE_ADMIN"));
			}

			newUser.encryptPassword();
			newUser.setRoleList(roles);
			newUser.setEnabled(true);
			newUser.setToken(UUID.randomUUID().toString());

			userRepo.save(newUser);
			model.addAttribute("message", "New user " + newUser.getUsername() + "added.");

		}
		return showUserManagement(model, authentication);
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
				newUser.setToken(UUID.randomUUID().toString());

				model.addAttribute("message", "Successfully registered User: " + newUser.getUsername());
			}
			userRepo.save(newUser);

		}
		return showRegisterIssues(model, authentication);
	}

	// ******************** R:READ ****************************************

	@Secured({ "ROLE_GUEST" })
	@RequestMapping(value = { "showProfile" })
	public String showProfile(Model model, Authentication authentication) {

		User user = userRepo.findFirstByUsername(authentication.getName());

		if (user != null && user.isEnabled()) {

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
		} else {
			model.addAttribute("errorMessage", "Something went wrong!");
			return "login";
		}
		return "showProfile";
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping(value = { "showUserManagement" })
	public String showUserManagement(Model model, Authentication authentication) {

		List<User> users = userRepo.findAll();
		model.addAttribute("users", users);
		model.addAttribute("information",
				"Currently there are <strong>" + userRepo.findByEnabledTrue().size() + "</strong> active Users and <strong>"
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
	public String editUser(@RequestParam String username, Model model, Authentication authentication) {

		User user = userRepo.findFirstByUsername(username);

		if (user != null && user.isEnabled()) {
			if (user.getUsername().equalsIgnoreCase(authentication.getName())
					|| authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {

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

				model.addAttribute("message", "User data successfully retrieved. You can now edit the User");
				return "createModifyUser";
			} else {
				if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
					model.addAttribute("errorMessage",
							"Error while reading User data! Hint: Either dissabled or does not exist");
					return showUserManagement(model, authentication);
				} else {
					model.addAttribute("errorMessage", "Error while reading User data!");
					return showProfile(model, authentication);
				}
			}
		}
		return "createModifyUser";
	}

	@Secured({ "ROLE_GUEST" })
	@PostMapping(value = "/editUser")
	public String editUser(@Valid User editUserModel, BindingResult bindingResult, Model model,
			Authentication authentication) {

		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
				return showUserManagement(model, authentication);
			else
				return showProfile(model, authentication);
		}

		User user = userRepo.findFirstByUsername(editUserModel.getUsername());

		if (user != null && user.isEnabled()) {
			if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
					|| user.getUsername().equalsIgnoreCase(authentication.getName())) {

				user.setFirstname(editUserModel.getFirstname());
				user.setLastname(editUserModel.getLastname());
				user.seteMail(editUserModel.geteMail());
				user.setTelNumber(editUserModel.getTelNumber());

				userRepo.save(user);

			} else {
				model.addAttribute("warningMessage",
						"Not allowed to edit User with username " + editUserModel.getUsername() + "!");
			}

			if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
				model.addAttribute("message", "Successfully updated User data of User: " + user.getUsername() + " !");
				return showUserManagement(model, authentication);
			} else {
				model.addAttribute("message", "Successfully updated User data of User: " + user.getUsername() + " !");
				return showProfile(model, authentication);
			}

		} else {
			if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
				model.addAttribute("errorMessage",
						"Error while reading User data! Hint: Either dissabled or does not exist");
				return showUserManagement(model, authentication);
			} else {
				model.addAttribute("errorMessage", "Error while reading User data!");
				return showProfile(model, authentication);
			}
		}
	}

	@Secured({ "ROLE_ADMIN" })
	@GetMapping(value = "/changeUserRole")
	public String changeUserRole(@RequestParam("username") String username, Model model,
			Authentication authentication) {

		User user = userRepo.findFirstByUsername(username);

		if (user != null && user.isEnabled()) {

			if (user.getUsername().equalsIgnoreCase(authentication.getName())) {
				model.addAttribute("warningMessage", "You cannot change your own user role!");
				return showUserManagement(model, authentication);
			}

			model.addAttribute("user", user);
			return "changeUserRole";
		} else {
			model.addAttribute("errorMessage", "Error while reading User data! Either dissabled or does not exist");
			return showUserManagement(model, authentication);
		}
	}

	@Secured({ "ROLE_ADMIN" })
	@PostMapping(value = "/changeUserRole")
	public String changeUserRole(@RequestParam(name = "role", required = false) String role, @Valid User editUserModel,
			BindingResult bindingResult, Model model, Authentication authentication) {

		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			return showUserManagement(model, authentication);
		}

		List<UserRole> roles = new ArrayList<UserRole>();
		User user = userRepo.findFirstByUsername(editUserModel.getUsername());

		if (user != null && user.isEnabled()) {

			if (role.equals("admin")) {
				UserRole userRoleAdmin = userRoleRepo.findFirstByRoleName("ROLE_ADMIN");
				UserRole userRoleHost = userRoleRepo.findFirstByRoleName("ROLE_HOST");
				UserRole userRoleGuest = userRoleRepo.findFirstByRoleName("ROLE_GUEST");
				if (userRoleAdmin != null && userRoleHost != null && userRoleGuest != null) {
					roles.add(userRoleGuest);
					roles.add(userRoleHost);
					roles.add(userRoleAdmin);
				} else {
					model.addAttribute("warningMessage", "Error while assigning user Role!");
					return showUserManagement(model, authentication);
				}
			}
			if (role.equals("host")) {
				UserRole userRoleHost = userRoleRepo.findFirstByRoleName("ROLE_HOST");
				UserRole userRoleGuest = userRoleRepo.findFirstByRoleName("ROLE_GUEST");
				if (userRoleHost != null && userRoleGuest != null) {
					roles.add(userRoleGuest);
					roles.add(userRoleHost);
				} else {
					model.addAttribute("warningMessage", "Error while assigning user Role!");
					return showUserManagement(model, authentication);
				}
			}
			if (role.equals("guest")) {
				UserRole userRoleGuest = userRoleRepo.findFirstByRoleName("ROLE_GUEST");
				if (userRoleGuest != null) {
					roles.add(userRoleGuest);
				} else {
					model.addAttribute("warningMessage", "Error while assigning user Role!");
					return showUserManagement(model, authentication);
				}
			}

			user.setRoleList(roles);
			userRepo.save(user);
			model.addAttribute("message", "User Data successfully updated!");
			return showUserManagement(model, authentication);
		} else {
			model.addAttribute("errorMessage",
					"Error while reading User data! Hint: Either dissabled or does not exist.");
			return showUserManagement(model, authentication);
		}

	}

	@Secured({ "ROLE_GUEST" })
	@GetMapping("/uploadProfilePicture")
	public String uploadProfilePicture(Model model, @RequestParam("username") String username) {
		model.addAttribute("username", username);
		User user = userRepo.findFirstByUsername(username);
		if (user != null && user.isEnabled()) {
			model.addAttribute("information", "You can now upload your profile Picture in JPG format.");
			return "uploadProfilePicture";
		} else {
			model.addAttribute("errorMessage",
					"Error while reading User Data!. Please login in again and retry to upload your profile Picture.");
			return "login";
		}
	}

	@Secured({ "ROLE_GUEST" })
	@PostMapping("/uploadProfilePicture")
	public String uploadProfilePicture(Model model, Authentication authentication,
			@RequestParam("username") String username, @RequestParam("imageFile") MultipartFile imageFile) {

		try {

			Optional<User> userOpt = userRepo.findByUsername(username);

			if (!userOpt.isPresent()) {
				model.addAttribute("errorMessage", "Error while reading Data!");
				return "showProfile";
			}

			User user = userOpt.get();

			//Load lazy ProfilePicutre and check if there is one already!
			ProfilePicture currPic = profilePictureRepo.findByAssignedUserUsername(user.getUsername());		
			// Already a Profile Picture available -> delete it
			if (currPic != null) {
				profilePictureRepo.delete(currPic);
				// Don't forget to remove the relationship too
				user.setProfilePicture(null);
				userRepo.save(user);
			}
			// Create a new document and set all available infos

			ProfilePicture pp = new ProfilePicture();
			pp.setName(user.getUsername() + "-profile-Picture");
			pp.setType(imageFile.getContentType());
			pp.setCreated(new Date());
			pp.setPic(imageFile.getBytes());
			
			pp.setAssignedUser(user);
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
			return showUserManagement(model, authentication);
		} else {

			if (user.getUsername().equalsIgnoreCase(authentication.getName())
					|| authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
				model.addAttribute("user", user);
				return "changeResetPassword";
			} else {
				model.addAttribute("warningMessage", "Not allowed to change password for " + user.getUsername() + "!");
				return showUserManagement(model, authentication);
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
			if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
				return showUserManagement(model, authentication);
			else
				return showProfile(model, authentication);
		}

		User user = userRepo.findFirstByUsername(editUserModel.getUsername());

		if (user != null && user.isEnabled()) {
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
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
			return showUserManagement(model, authentication);
		else
			return showProfile(model, authentication);
	}

	@GetMapping("/sendResetPassword")
	public String sendResetPassword(@RequestParam("username") String username, Model model,
			Authentication authentication) {

		User user = userRepo.findFirstByUsername(username);
		if (user != null && user.isEnabled() && user.geteMail() != null && user.getToken() != null) {
			sendPasswordResetMail(user);
			model.addAttribute("message", "Reset Passwort Email for User: " + user.getUsername() + " has been sent.");
			return "login";
		} else
			model.addAttribute("errorMessage", "Error while reading user data!");
		return "login";
	}

	private void sendPasswordResetMail(User user) {


		String content = "Copy and paste the following link in your browser to reset your password: ";
		String resetPasswordUrl = "http://localhost:8080/fhj.swenga2017.plavent/resetPassword?token="
				+ user.getToken();
		
		// Create a thread safe "copy" of the template message and customize it
		SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);

		// You can override default settings from dispatcher-servlet.xml:
		msg.setTo(user.geteMail());
		msg.setSubject("Password Reset");
		msg.setText(String.format(msg.getText(), user.getFirstname() + ' ' + user.getLastname(), content, resetPasswordUrl));
		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {
			ex.printStackTrace();
		}

	}

	@GetMapping("/resetPassword")
	public String resetPassword(@RequestParam("token") String userToken, Model model, Authentication authentication) {

		User user = userRepo.findFirstByToken(userToken);
		if (user != null && user.isEnabled()) {
			model.addAttribute("message", "You can now reset the password.");
			model.addAttribute("userB", user);
			return "changeResetPassword";
		} else
			model.addAttribute("errorMessage", "Error while reading user data!");
		return "login";
	}

	@PostMapping("/resetPassword")
	public String resetPassword(@Valid User resetPasswordUser, BindingResult bindingResult, Model model,
			Authentication authentication) {

		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			return "login";
		}

		User user = userRepo.findFirstByUsername(resetPasswordUser.getUsername());
		if (user != null && user.isEnabled()) {
			user.setPassword(resetPasswordUser.getPassword());
			user.encryptPassword();
			userRepo.save(user);
			model.addAttribute("message", "Password successfully reset");
			return "login";
		} else
			model.addAttribute("errorMessage", "Error while reading user data!");
		return "login";
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
			return showUserManagement(model, authentication);
		}

		User user = userRepo.findFirstByUsername(editUserModel.getUsername());
		if (user.isEnabled() == false) {
			model.addAttribute("errorMessage", "User does not exist! <" + editUserModel.getUsername() + ">");
			return showUserManagement(model, authentication);
		}
		if (user.getUsername().equalsIgnoreCase(authentication.getName())) {
			model.addAttribute("errorMessage", "You cannot delete yourself! <" + editUserModel.getUsername() + ">");
		} else {
			user.setEnabled(false);
			userRepo.save(user);
			model.addAttribute("message", "User " + editUserModel.getUsername() + " sucessfully deleted");
		}
		return showUserManagement(model, authentication);
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
			return showUserManagement(model, authentication);
		}

		User user = userRepo.findFirstByUsername(editUserModel.getUsername());
		if (user == null || user.isEnabled()) {
			model.addAttribute("errorMessage",
					"User does not exist or is already reactivated! <" + editUserModel.getUsername() + ">");
			return showUserManagement(model, authentication);
		} else {
			user.setEnabled(true);
			userRepo.save(user);
			model.addAttribute("message", "User " + editUserModel.getUsername() + "sucessfully reactivated");
		}
		return showUserManagement(model, authentication);
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
