/**
 * 
 */
package at.fh.swenga.plavent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import at.fh.swenga.plavent.model.User;
import at.fh.swenga.plavent.repo.HappeningStatusRepository;
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
	private UserRepository userRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private HappeningStatusRepository happeningStatusRepository;

	@RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
	public String handleLogin(Model model, Authentication authentication) {


			// If user already set(logged in, show dashboard page otherwise show login page
			if (authentication != null)
				return "dashboard";
			else {
				// Check if DB-Environment exists or start is the first initial startup
				if (happeningStatusRepository.getAmountOfHappeningStatus() <= 0
						|| userRoleRepository.getAmountOfUserRoles() <= 0 || userRepository.count() <= 0) {
					// model.addAttribute("warningMessage",
					// "Welcome to Plavent! - You first need to initialize the database. Therefor
					// please press start!");
					model.addAttribute("noPlaventEnvironment", true);
				}
				return "login";
			}
		} 
}
