package at.fh.swenga.plavent.controller;

import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

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
public class DashboardController {

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserRoleDao userRoleDao;

	@Autowired
	private HappeningStatusDao happeningStatusDao;

	@Autowired
	private HappeningCategoryDao happeningCategoryDao;

	public DashboardController() {
		// TODO Auto-generated constructor stub
	}

	@RequestMapping(value = { "/" })
	public String showLoginPage(Model model) {

		/*
		 * hoedlale16: If user already set(logged in, show dashboard page otherwise show
		 * login page
		 */
		if (UserManagementController.isLoggedIn(model)) {
		  return "dashboard";
		} else {
		  // Show loginpage
		  return "login";
		}
	}
	
	@RequestMapping(value = { "dashboard" })
	public String showDashboard(Model model) {
		if (!UserManagementController.isLoggedIn(model)) {
			return "login";
		}
		
		User user = UserManagementController.getCurrentLoggedInUser();
		//TODO: Set required attributes to show dashboard for logged in user
		
		// Show main screen (dashboard)
		model.addAttribute("message", "Welcome " + user.getFirstname() + "!");
		return "dashboard";
	}
}
