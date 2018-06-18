package at.fh.swenga.plavent.controller;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import at.fh.swenga.plavent.model.ApplicationProperty;
import at.fh.swenga.plavent.model.Happening;
import at.fh.swenga.plavent.model.HappeningCategory;
import at.fh.swenga.plavent.model.HappeningStatus;
import at.fh.swenga.plavent.model.HappeningTask;
import at.fh.swenga.plavent.model.User;
import at.fh.swenga.plavent.model.UserRole;
import at.fh.swenga.plavent.repo.ApplicationPropertyRepository;
import at.fh.swenga.plavent.repo.HappeningCategoryRepository;
import at.fh.swenga.plavent.repo.HappeningRepository;
import at.fh.swenga.plavent.repo.HappeningStatusRepository;
import at.fh.swenga.plavent.repo.HappeningTaskRepository;
import at.fh.swenga.plavent.repo.UserRepository;
import at.fh.swenga.plavent.repo.UserRoleRepository;

/**
 * @author Alexander Hoedl:
 * 
 *         initialization of application data (users, roles, happenings,
 *         categories, status)
 *
 */

@Controller
public class InitialSetupController {

	@Autowired
	private ApplicationPropertyRepository appPropertyRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private UserRoleRepository userRoleRepo;

	@Autowired
	private HappeningRepository happeningRepo;

	@Autowired
	private HappeningStatusRepository happeningStatusRepo;

	@Autowired
	private HappeningCategoryRepository happeningCategoryRepo;

	@Autowired
	private HappeningTaskRepository happeningTaskRepo;

	public InitialSetupController() {
	}

	@RequestMapping(value = { "preparePlavent" })
	public String preparePlavent(Model model) {
		try {

			//Create application properties
			this.createApplicationProperties();

			// Create Categories
			this.createHappeningCategories();

			// Create HappeningStatus
			this.createHappeningStatus();

			// Create UserRoles and Users
			createUsersAndRoles();

			// Create a tutorial happening
			// Needs to be the last call because requires all other methods before!!!
			createTutorialHappening();

			//model.addAttribute("message", "Database initialized - You can now login or register!");
			return "login";
		} catch (Exception e) {
			model.addAttribute("errorMessage", "Error occured: \"+ e.getMessage()!");
			return "error";
		}
	}

	private void createApplicationProperties()
	{
		//Property to mark environment as installed.
		ApplicationProperty installed = appPropertyRepo.findFirstByToken("PLAVENT.INSTALLED");
		if (installed == null) {
			installed = new ApplicationProperty("PLAVENT.INSTALLED", true,
					"Marks environment as installed. Handles InitialSetup in combination wiht amount of existing users. ");
			appPropertyRepo.save(installed);
		}
		
		//Property to set permission if HOSTS are allowed to modify happenings after their start.
		ApplicationProperty modAfterStart = appPropertyRepo.findFirstByToken("HAPPENING.MODIFICATION.AFTER.START");
		if (modAfterStart  == null) {
			modAfterStart  = new ApplicationProperty("HAPPENING.MODIFICATION.AFTER.START", false,
					"Flag to set permission if users with role HOST (and no ADMIN) are allowed to modify a happening after start or not.");
			appPropertyRepo.save(modAfterStart );
		}
		
	}

	private void createHappeningCategories() {

		// Create a default happening category
		HappeningCategory catUnAssigned = happeningCategoryRepo.findFirstByCategoryName("Unassigned");
		if (catUnAssigned == null) {
			catUnAssigned = new HappeningCategory("Unassigned", "Not specified", true);
			happeningCategoryRepo.save(catUnAssigned);
		}
	}

	private void createHappeningStatus() {
		// Create Happening status values
		HappeningStatus hsActive = happeningStatusRepo.findFirstByStatusName("ACTIVE");
		if (hsActive == null) {
			hsActive = new HappeningStatus("ACTIVE", "The happening will happen as planned!");
			happeningStatusRepo.save(hsActive);
		}
		HappeningStatus hsDeleted = happeningStatusRepo.findFirstByStatusName("DELETED");
		if (hsDeleted == null) {
			hsDeleted = new HappeningStatus("DELETED", "The happening is cancelled!");
			happeningStatusRepo.save(hsDeleted);
		}
	}
	

	/** USERS AND ROLES ARE DEPLOYED IN THE SPRING SECURITY XML FILE. FERNBACH16 **/
	private void createUsersAndRoles() {

		// Create useroles if required
		UserRole roleAdmin = userRoleRepo.findFirstByRoleName("ROLE_ADMIN");
		if (roleAdmin == null) {
			roleAdmin = new UserRole("ROLE_ADMIN", "The role to manage the system");
			userRoleRepo.save(roleAdmin);
		}

		UserRole roleHost = userRoleRepo.findFirstByRoleName("ROLE_HOST");
		if (roleHost == null) {
			roleHost = new UserRole("ROLE_HOST", "The role to create happening and manage them");
			userRoleRepo.save(roleHost);
		}

		UserRole roleGuest = userRoleRepo.findFirstByRoleName("ROLE_GUEST");
		if (roleGuest == null) {
			roleGuest = new UserRole("ROLE_GUEST", "The role to be a guest at happenings");
			userRoleRepo.save(roleGuest);
		}

		// Create overall admin if required
		if (userRepo.findFirstByUsername("admin") == null) {
			User administrator = new User("admin", "password", "Administrator", "Administrator", "admin@plavent.com",new ArrayList<UserRole>(),UUID.randomUUID().toString());
			administrator.encryptPassword();
			administrator.addUserRole(roleGuest);
			administrator.addUserRole(roleHost);
			administrator.addUserRole(roleAdmin);
			userRepo.save(administrator);
		}

		// Create a host user if required
		if (userRepo.findFirstByUsername("host") == null) {
			User host = new User("host", "password", "Host", "Host","host@plavent.com", new ArrayList<UserRole>(),UUID.randomUUID().toString());
			host.encryptPassword();
			host.addUserRole(roleGuest);
			host.addUserRole(roleHost);
			userRepo.save(host);
		}

		// Create a simple guest user if required
		if (userRepo.findFirstByUsername("guest") == null) {
			User guest = new User("guest", "password", "Guest", "Guest","guest@plavent.com", new ArrayList<UserRole>(),UUID.randomUUID().toString());
			guest.encryptPassword();
			guest.addUserRole(roleGuest);
			userRepo.save(guest);
		}
	}

	private void createTutorialHappening() {
		if (happeningRepo.findByHappeningName("Tutorial").isEmpty()) {
			// Generated with previous methods
			HappeningCategory catUnassinged = happeningCategoryRepo.findFirstByCategoryName("Unassigned");
			HappeningStatus statusActive = happeningStatusRepo.findFirstByStatusName("ACTIVE");
			User host = userRepo.findFirstByUsername("host");

			List<User> guests = userRepo.getUsersByRolename("GUEST");
			guests.remove(host); // Remove host itself which is the host and not the guest of the happening!

			Calendar today = Calendar.getInstance();
			today.add(Calendar.HOUR, 3); //Start in 3 hours
			
			Calendar nextYear =  Calendar.getInstance();
			nextYear.add(Calendar.YEAR, 1); //ends next year
			
			Happening happening = new Happening("Tutorial", today, nextYear,
					"Example Happening", "FH Joanneum", catUnassinged, statusActive, host, guests,
					new ArrayList<HappeningTask>());
			happeningRepo.save(happening);

			// Now add the tasks...
			// TODO: Assign task to a guest
			happening.addHappeningTask(
					createTask(happening, "Create Tasks", "Create new Tasks for this happening", null));
			happening.addHappeningTask(createTask(happening, "Create Category", "Create a new category", null));
			happening.addHappeningTask(createTask(happening, "Assign Guests", "Assign guests to happening", null));

			happeningRepo.save(happening);
		}

	}

	private HappeningTask createTask(Happening happening, String topic, String description, User responsible) {
		HappeningTask task = new HappeningTask(happening, topic, description, 1, responsible);
		happeningTaskRepo.save(task);
		return task;
	}
}
