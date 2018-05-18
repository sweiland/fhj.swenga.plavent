package at.fh.swenga.plavent.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import at.fh.swenga.plavent.model.Happening;
import at.fh.swenga.plavent.model.HappeningCategory;
import at.fh.swenga.plavent.model.HappeningStatus;
import at.fh.swenga.plavent.model.HappeningTask;
import at.fh.swenga.plavent.model.User;
import at.fh.swenga.plavent.model.UserRole;
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

			// Create a tutorial happening
			// Needs to be the last call because requires all other methods before!!!
			createTutorialHappening();

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
		HappeningCategory catUnAssigned = happeningCategoryRepo.findFirstByCategoryName("Unassigned");
		if (catUnAssigned == null) {
			catUnAssigned = new HappeningCategory("Unassigned", "Not specified ");
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
			User administrator = new User("admin", "admin", "Administrator", "Administrator", new ArrayList<UserRole>());
			administrator.addUserRole(roleGuest);
			administrator.addUserRole(roleHost);
			administrator.addUserRole(roleAdmin);	
			userRepo.save(administrator);
		}

		// Create a host user if required
		if (userRepo.findFirstByUsername("host") == null) {
			User host = new User("host", "host", "Host", "Host", new ArrayList<UserRole>());
			host.addUserRole(roleGuest);
			host.addUserRole(roleHost);
			userRepo.save(host);
		}

		// Create a simple guest user if required
		if (userRepo.findFirstByUsername("guest") == null) {
			User guest = new User("guest", "guest", "Host", "Host", new ArrayList<UserRole>());
			guest.addUserRole(roleGuest);
			guest.addUserRole(roleHost);
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

			Happening happening = new Happening("Tutorial", new Date(), new Date(), "Example Happening", "FH Joanneum",
					catUnassinged, statusActive, host, guests, new ArrayList<HappeningTask>());
			happeningRepo.save(happening);
			
			//Now add the tasks...
			//TODO: Assign task to a guest
			happening.addHappeningTask(createTask(happening,"Create Tasks", "Create new Tasks for this happening", null));
			happening.addHappeningTask(createTask(happening,"Create Category", "Create a new category", null));
			happening.addHappeningTask(createTask(happening,"Assign Guests", "Assign guests to happening", null));
			
			happeningRepo.save(happening);
		}

	}
	
	private HappeningTask createTask(Happening happening, String topic, String description, User responsible) {
		HappeningTask task = new HappeningTask(happening, topic, description, 1, responsible);
		happeningTaskRepo.save(task);		
		return task;
	}
}
