package at.fh.swenga.plavent.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Autowired;
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
public class InitialSetupController {

	@Autowired
	private UserDao userDao;

	@Autowired
	private UserRoleDao userRoleDao;

	@Autowired
	private HappeningStatusDao happeningStatusDao;

	@Autowired
	private HappeningCategoryDao happeningCategoryDao;

	public InitialSetupController() {
		// TODO Auto-generated constructor stub
	}

	@RequestMapping(value = { "preparePlavent" })
	public String preparePlavent(Model model) {
		try {

			// Create Categories
			this.createHappeningCategories();

			// Create HappeningStatus
			this.createHappeningStatus();

			// Create UserRoles and Users
			createUsersAndRoles();

			model.addAttribute("warningMessage", "Environment created - Start planning!");
			return "login";
		} catch (Exception e) {
			System.out.println("Error occured: " + e.getMessage());
			model.addAttribute("errorMessage", "Error occured: \"+ e.getMessage()!");
			return "error";
		}
	}

	private void createHappeningCategories() {

		// Create a default happening cateogry
		HappeningCategory catUnAssigned = happeningCategoryDao.findFirstByCategoryName("Unassigned");
		if (catUnAssigned == null) {
			catUnAssigned = new HappeningCategory("Unassigned", "Not specified ");
			happeningCategoryDao.save(catUnAssigned);
		}
	}

	private void createHappeningStatus() {
		// Create Happening status values
		HappeningStatus hsActive = happeningStatusDao.findFirstByStatusName("ACTIVE");
		if (hsActive == null) {
			hsActive = new HappeningStatus("ACTIVE", "The happening will happen as planned!");
			happeningStatusDao.save(hsActive);
		}
		HappeningStatus hsDeleted = happeningStatusDao.findFirstByStatusName("DELETED");
		if (hsDeleted == null) {
			hsDeleted = new HappeningStatus("DELETED", "The happening is cancelled!");
			happeningStatusDao.save(hsDeleted);
		}
	}

	private void createUsersAndRoles() throws NoSuchAlgorithmException {

		// Create useroles if required
		UserRole roleAdmin = userRoleDao.getUserRole("ADMIN");
		if (roleAdmin == null) {
			roleAdmin = new UserRole("ADMIN", "The role to manage the system", true, true, true);
		}
		UserRole roleHost = userRoleDao.getUserRole("HOST");
		if (roleHost == null)
			roleHost = new UserRole("HOST", "The role to create happening and manage them", false, true, false);
		UserRole roleGuest = userRoleDao.getUserRole("GUEST");
		if (roleGuest == null)
			roleGuest = new UserRole("GUEST", "The role to be a guest at happenings", false, false, false);

		// Create overall admin if required
		MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");
		if (userDao.findFirstByUsername("admin") == null) {
			User administrator = new User("admin", new String(md5.digest("admin".getBytes())), "Administrator",
					"Administrator", roleAdmin);
			userDao.save(administrator);
		}

		// Create a host user if required
		if (userDao.findFirstByUsername("host") == null) {
			User host = new User("host", new String(md5.digest("host".getBytes())), "Host", "Host", roleHost);
			userDao.save(host);
		}

		// Create a simple guest user if required
		if (userDao.findFirstByUsername("guest") == null) {
			User host = new User("guest", new String(md5.digest("guest".getBytes())), "Host", "Host", roleGuest);
			userDao.save(host);
		}
	}
}
