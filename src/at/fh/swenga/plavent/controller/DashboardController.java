package at.fh.swenga.plavent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import at.fh.swenga.plavent.dao.HappeningCategoryDao;
import at.fh.swenga.plavent.dao.HappeningStatusDao;
import at.fh.swenga.plavent.dao.UserDao;
import at.fh.swenga.plavent.dao.UserRoleDao;

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
	
	@RequestMapping(value = { "/", "/login" }, method = RequestMethod.GET)
	public String handleLogin(Authentication authentication) {

		// If user already set(logged in, show dashboard page otherwise show login page
		if (authentication != null)
			return "dashboard";
		else
			return "login";
	}
	
	

	@Secured({ "ROLE_USER"})
	@RequestMapping(value = { "dashboard" })
	public String showDashboard(Model model) {
		
		//TODO: Show stuff for logged in user
		// Show main screen (dashboard)
		return "dashboard";
	}
}

