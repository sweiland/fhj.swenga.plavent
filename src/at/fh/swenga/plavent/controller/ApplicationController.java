/**
 * 
 */
package at.fh.swenga.plavent.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import at.fh.swenga.plavent.model.User;
import at.fh.swenga.plavent.model.UserRole;
import at.fh.swenga.plavent.repo.ApplicationPropertyRepository;
import at.fh.swenga.plavent.repo.UserRepository;
import at.fh.swenga.plavent.repo.UserRoleRepository;

/**
 * @author Gregor Fernbach Controller for handling general things like
 *         login/logout
 * 
 *
 */
@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class ApplicationController {

	@Autowired
	private ApplicationPropertyRepository appPropertyRepository;
	
	@Autowired
	private UserRepository userRepo;
	
	@Autowired
	private UserRoleRepository userRoleRepo;

	@RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
	public String handleLogin(Model model, Authentication authentication) {

		// If user already set(logged in, show dashboard page otherwise show login page
		if (authentication != null)
			return "dashboard";
		else {
			// Check if DB-Environment exists or start is the first initial startup
			if (!appPropertyRepository.existsById("PLAVENT.INSTALLED")) {
				model.addAttribute("warningMessage",
						"Welcome to Plavent! - You first need to initialize the database. Therefore please press start!");
				model.addAttribute("noPlaventEnvironment", true);
			}
		}
		return "login";
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
			return "login";
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
		return "login";
	}

	
	@ExceptionHandler()
	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	public String handle403(Exception ex) {
		return "login";
	}
}
