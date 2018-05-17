package at.fh.swenga.plavent.controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

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
		//createUsersAndRoles();

		model.addAttribute("warningMessage", "Environment created - Start planning!");
		return "login";
		}
		catch (Exception e) {
			System.out.println("Error occured: "+ e.getMessage());
			model.addAttribute("errorMessage", "Error occured: \"+ e.getMessage()!");
			return "error";
		}
	}

	private void createHappeningCategories() {

		// Create a default happening cateogry
		HappeningCategory catUnAssigned = happeningCategoryDao.getCategory("Unassigned");
		if (catUnAssigned == null)
			catUnAssigned = new HappeningCategory("Unassigned", "Not specified ");

	}

	private void createHappeningStatus() {
		// Create Happening status values
		HappeningStatus hsActive = happeningStatusDao.getHappeningStatus("ACTIVE");
		if (hsActive == null)
			hsActive = new HappeningStatus("ACTIVE", "The happening will happen as planned!");

		HappeningStatus hsDeleted = happeningStatusDao.getHappeningStatus("DELETED");
		if (hsDeleted == null)
			hsDeleted = new HappeningStatus("DELETED", "The happening is cancelled!");
	}

	/** USERS AND ROLES ARE DEPLOYED IN THE SPRING SECURITY XML FILE. FERNBACH16 **/
	private void createUsersAndRoles() {

		// Create useroles if required
		UserRole roleAdmin = userRoleDao.getUserRole("ADMIN");
		if (roleAdmin == null)
			roleAdmin = new UserRole("ROLE_ADMIN", "The role to manage the system");
		UserRole roleHost = userRoleDao.getUserRole("HOST");
		if (roleHost == null)
			roleHost = new UserRole("ROLE_HOST", "The role to create happening and manage them");
		UserRole roleGuest = userRoleDao.getUserRole("GUEST");
		if (roleGuest == null)
			roleGuest = new UserRole("ROLE_GUEST", "The role to be a guest at happenings");

		// Create overall admin if required
		List<UserRole> roles = new LinkedList<UserRole>();
		
		roles.add(roleAdmin);
		if (userDao.findByUserName("admin") == null) {
			User administrator = new User("admin", "admin", "Administrator",
					"Administrator", roles);
			userDao.save(administrator);
		}
		
		roles.clear();
		roles.add(roleHost);

		// Create a host user if required
		if (userDao.findByUserName("host") == null) {
			User host = new User("host", "host", "Host", "Host", roles);
			userDao.save(host);
		}
		
		roles.clear();
		roles.add(roleGuest);

		// Create a simple guest user if required
		if (userDao.findByUserName("guest") == null) {
			User host = new User("guest", "guest", "Host", "Host", roles);
			userDao.save(host);
		}
	}
	
	/** DATA FACTORY FOR LATER USE !!! FERNBACH16
	@RequestMapping("/fillList")
	@Transactional
	public String fillData(Model model) {

		DataFactory df = new DataFactory();

		User user = null;

		String[] pw = {"password"};
		
		for (int i = 0; i < 100; i++) {
			if (i % 10 == 0) {
				String name = df.getBusinessName();
				producer = producerRepository.findFirstByName(name);
				if (producer == null) {
					producer = new Producer(name);
				}
			}

			User user = new User(df.getRandomWord(), df.getItem(pw), df.getFirstName(), df.getLastName(), df.getEmailAddress(), df.getNumberText(15));
			pictureFrame.setProducer(producer);
			UserDao.save(user);
		}
	}
	**/

}
