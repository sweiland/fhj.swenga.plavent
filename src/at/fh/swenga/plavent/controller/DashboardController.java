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

	@RequestMapping(value = { "preparePlavent" })
	public String preparePlavent(Model model) throws Exception {

		// Create useroles if required
		UserRole roleAdmin = userRoleDao.getUserRole("ADMIN");
		if (roleAdmin == null)
			roleAdmin = new UserRole("ADMIN", "The role to manage the system",true,true,true);
		UserRole roleHost = userRoleDao.getUserRole("HOST");
		if (roleHost == null)
			roleHost = new UserRole("HOST", "The role to create happening and manage them",false,true,false);
		UserRole roleGuest = userRoleDao.getUserRole("GUEST");
		if (roleGuest == null)
			roleGuest = new UserRole("GUEST", "The role to be a guest at happenings",false,false,false);

		// Create Happening status values
		HappeningStatus hsActive = happeningStatusDao.getHappeningStatus("ACTIVE");
		if (hsActive == null)
			hsActive = new HappeningStatus("ACTIVE", "The happening will happen as planned!");

		HappeningStatus hsDeleted = happeningStatusDao.getHappeningStatus("DELETED");
		if (hsDeleted == null)
			hsDeleted = new HappeningStatus("DELETED", "The happening is cancelled!");

		// Create a default happening cateogry
		HappeningCategory catUnAssigned = happeningCategoryDao.getCategory("Unassigned");
		if (catUnAssigned == null)
			catUnAssigned = new HappeningCategory("Unassigned", "Not specified ");

		// Create overall admin if required
		MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");
		if (userDao.getUser("admin") == null) {
			User administrator = new User("admin", new String(md5.digest("admin".getBytes())), "Administrator",
					"Administrator", roleAdmin);
			userDao.persist(administrator);
		}

		// Create a host user if required
		if (userDao.getUser("host") == null) {
			User host = new User("host", new String(md5.digest("host".getBytes())), "Host", "Host", roleHost);
			userDao.persist(host);
		}

		// Create a simple guest user if required
		if (userDao.getUser("guest") == null) {
			User host = new User("guest", new String(md5.digest("guest".getBytes())), "Host", "Host", roleGuest);
			userDao.persist(host);
		}

		model.addAttribute("warningMessage", "Environment created - Start planning!");
		return "login";
	}


}
