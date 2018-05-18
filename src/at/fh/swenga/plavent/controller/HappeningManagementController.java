package at.fh.swenga.plavent.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.fh.swenga.plavent.dao.HappeningCategoryRepository;
import at.fh.swenga.plavent.dao.HappeningRepository;
import at.fh.swenga.plavent.dao.HappeningStatusRepository;
import at.fh.swenga.plavent.dao.HappeningTaskRepository;
import at.fh.swenga.plavent.dao.UserRepository;
import at.fh.swenga.plavent.model.Happening;
import at.fh.swenga.plavent.model.HappeningTask;
import at.fh.swenga.plavent.model.User;

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class HappeningManagementController {

	@Autowired
	HappeningRepository happeningDao;

	@Autowired
	HappeningTaskRepository happeningTaskDao;

	@Autowired
	HappeningCategoryRepository happeningCategoryDao;

	@Autowired
	HappeningStatusRepository happeningStatusDao;

	@Autowired
	UserRepository userDao;

	public HappeningManagementController() {
		// TODO Auto-generated constructor stub
	}

	private boolean errorsDetected(Model model, BindingResult bindingResult) {
		// Any errors? -> Create a String out of all errors and return to the page
		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid<br>";
			}
			model.addAttribute("errorMessage", errorMessage);
			return true;
		}
		return false;
	}

	@Secured({ "ROLE_HOST"})
	@RequestMapping(value = { "showHappeningManagement" })
	public String showHappenings(Model model) {

		// TODO: JUST ADMINS are allowed to see all. The others just happenings which belongs to them
		//if (UserManagementController.getCurrentLoggedInUser().getRole().getRoleName().equalsIgnoreCase("ADMIN")) {
		// Admin is allowed to see all happenings!
			model.addAttribute("happenings", happeningDao.findAll());
		//} else {
			// Show just happening for given user.
			// TODO: take logged in user from session flag
			//model.addAttribute("happenings", happeningDao
			//		.findByHappeningHostUsername(UserManagementController.getCurrentLoggedInUser().getUsername()));
		//}

		return "happeningManagement";
	}


	@Secured({ "ROLE_HOST"})
	@RequestMapping(value = { "showCreateHappeningForm" })
	public String showCreateHappeningForm(Model model) {

		// Set required attributes
		model.addAttribute("happeningCategories", happeningCategoryDao.findAll());

		// TODO:Add possible other Possible other HOSTS to list if current logged in
		// user is a ADMIN
		//if (UserManagementController.getCurrentLoggedInUser().getRole().isAdminRole()) {
			// TODO: Required from UserDAO: List of all Users which are in Role Hosts except
			// given one.
			// model.addAttribute("happeningHosts", )
		//}

		return "createModifyHappening";
	}

	@Secured({ "ROLE_HOST"})
	@RequestMapping(value = { "showModifyExistingHappingForm" })
	public String showModifyExistingHappingForm(Model model, @RequestParam(value = "happeningId") Happening happening) {

		if (happening != null) {
			model.addAttribute("happening", happening);
			model.addAttribute("happeningCategories", happeningCategoryDao.findAll());

			// TODO:Add possible other Possible other HOSTS to list if current logged in
			// user is a ADMIN
			//if (UserManagementController.getCurrentLoggedInUser().getRole().isAdminRole()) {
				// TODO: Required from UserDAO: List of all Users which are in Role Hosts except
				// given one.
				// model.addAttribute("happeningHosts", )
			//}

			return "createModifyHappening";
		}

		// TODO: Implement
		model.addAttribute("warningMessage", "Not implemented <" + "showModifyExistingHappingForm" + ">");
		// return "createModifyHappening";
		return "happeningManagement";
	}

	@Secured({ "ROLE_HOST"})
	@PostMapping("/createNewHappening")
	// public String createNewHappening(@Valid Happening newHappening, BindingResult
	// bindingResult, Model model) {
	public String createNewHappening(Happening newHappening, @RequestParam(value = "host") String hostUsername,
			@RequestParam(value = "startDate") String startAsString,
			@RequestParam(value = "endDate") String endAsString, @RequestParam(value = "categoryID") int categoryID,
			Model model) throws ParseException {

		// Set correct connection objects
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyy hh:mm");
		newHappening.setHappeningStatus(happeningStatusDao.findFirstByStatusName("ACTIVE"));
		newHappening.setHappeningHost(userDao.findFirstByUsername(hostUsername));
		newHappening.setStart(format.parse(startAsString));
		newHappening.setEnd(format.parse(endAsString));
		newHappening.setCategory(happeningCategoryDao.findFirstByCategoryID(categoryID));
		happeningDao.save(newHappening);

		return showHappenings(model);
	}

	@Secured({ "ROLE_HOST"})
	@PostMapping("/modifyExistingHappening")
	public String modifyExistingHappening(Happening newHappening, @RequestParam(value = "host") String hostUsername,
			@RequestParam(value = "startDate") String startAsString,
			@RequestParam(value = "endDate") String endAsString, @RequestParam(value = "categoryID") int categoryID,
			@RequestParam(value = "statusString") String happeningStatus, Model model) throws ParseException {

		// Set correct connection objects
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyy hh:mm");
		newHappening.setHappeningStatus(happeningStatusDao.findFirstByStatusName(happeningStatus));
		newHappening.setHappeningHost(userDao.findFirstByUsername(hostUsername));
		newHappening.setStart(format.parse(startAsString));
		newHappening.setEnd(format.parse(endAsString));
		newHappening.setCategory(happeningCategoryDao.findFirstByCategoryID(categoryID));
		happeningDao.saveAndFlush(newHappening);

		return showHappenings(model);
	}

	@Secured({ "ROLE_HOST"})
	@GetMapping("/deleteExistingHappening")
	public String deleteHappening(Model model, @RequestParam(value = "happeningId") Happening happening) {
		happening.setHappeningStatus(happeningStatusDao.findFirstByStatusName("DELETED"));
		happeningDao.save(happening);
		return showHappenings(model);
	}

	@Secured({ "ROLE_HOST"})
	@GetMapping("/reactivateExistingHappening")
	public String reactivateHappening(Model model, @RequestParam(value = "happeningId") Happening happening) {

		happening.setHappeningStatus(happeningStatusDao.findFirstByStatusName("ACTIVE"));
		happeningDao.save(happening);
		return showHappenings(model);
	}

	@Secured({ "ROLE_HOST"})
	@PostMapping("/filterHappenings")
	public String filterHappenings(Model model, @RequestParam String searchString) {
		model.addAttribute("happenings", happeningDao.findByHappeningName(searchString));
		return "happeningManagement";
	}

	// -----------------------------------------------------------------------------------------
	// --- GUESTLIST MANAGEMENT ---
	// -----------------------------------------------------------------------------------------

	@Secured({ "ROLE_HOST"})
	@RequestMapping(value = { "showGuestListManagement" })
	public String showGuestListManagement(Model model, @RequestParam(value = "happeningID") Happening happening) {

		if (happening != null) {
			model.addAttribute("happening", happening);
			model.addAttribute("happeningGuests", happening.getGuestList()); // Required in a separate attribute for
																				// filtering
			model.addAttribute("potentialGuests", userDao.getPotentialGuestsForHappening(happening.getHappeningId()));
			return "happeningGuestManagement";
		}

		// Fallback - return to happening overview
		model.addAttribute("warningMessage", "Happening not found");
		return showHappenings(model);
	}

	// Add guest to guestlist
	@Secured({ "ROLE_HOST"})
	@PostMapping("/assignNewGuestToHappening")
	public String assignNewGuestToHappening(Model model, @RequestParam(value = "happeningID") Happening happening,
			@RequestParam(value = "newGuestUsername") String username) {

		User newGuest = userDao.findFirstByUsername(username);
		if (happening != null && newGuest != null) {
			// Assign new Guest to happening
			happening.addGuestToList(newGuest);
			happeningDao.save(happening);
			model.addAttribute("message", "User " + newGuest.getUsername() + " added from guestlist!");
		} else {
			model.addAttribute("warningMessage", "User or Happening not fonud!");
		}
		return showGuestListManagement(model, happening);
	}

	// Remove Guest from guestlist
	@Secured({ "ROLE_HOST"})
	@GetMapping("/unassignExistingGuest")
	public String unassignGuest(Model model, @RequestParam(value = "happeningID") Happening happening,
			@RequestParam(value = "username") String username) {

		User guestToRemove = userDao.findFirstByUsername(username);
		if (happening != null && guestToRemove != null) {
			happening.removeFromList(guestToRemove);
			happeningDao.save(happening);
			model.addAttribute("message", "User " + guestToRemove.getUsername() + " removed from guestlist!");

		} else {
			model.addAttribute("warningMessage", "User or Happening not fonud!");
		}

		return showGuestListManagement(model, happening);
	}

	// Filter GuestListManagement page
	@Secured({ "ROLE_HOST"})
	@PostMapping("/filterHappeningGuestList")
	public String filterGuestList(Model model, @RequestParam(value = "happeningID") Happening happening,
			@RequestParam String searchString) {

		if (happening != null) {
			model.addAttribute("happening", happening);
			model.addAttribute("potentialGuests", userDao.getPotentialGuestsForHappening(happening.getHappeningId()));
			model.addAttribute("warningMessage", "Not implemented <" + "showGuestListManagement" + ">");
			return "happeningGuestManagement";
		}

		model.addAttribute("warningMessage", "Happening not found");
		return showHappenings(model);
	}

	// Show form to assign Task to a guest
	@Secured({ "ROLE_HOST"})
	@GetMapping("/showAssignTaskToGuestForm")
	public String showAssignTaskToGuestForm(Model model, @RequestParam(value = "happeningID") Happening happening,
			@RequestParam String username) {

		// User von der Gaesteliste laden (Nicht zugeordnete leute durefen da nicht
		// vorkommen!
		User guest = null;
		if (happening != null) {
			if (happening.getHappeningHost().getUsername().equals(username))
				guest = happening.getHappeningHost();
			else {
				for (User g : happening.getGuestList()) {
					if (g.getUsername().equals(username)) {
						guest = g;
						break;
					}
				}
			}

			if (guest != null) {
				// TODO: Alle relevanten Attribute Setzen
				model.addAttribute("happeningTasks", happeningTaskDao.getUnassignedTasks(happening.getHappeningId()));
				model.addAttribute("happening", happening);
				model.addAttribute("happeningGuest", guest);

				model.addAttribute("warningMessage", "Not implemented <" + "showGuestListManagement" + ">");
				return showGuestListManagement(model, happening);
				// return "assignTaskToGuest";
			}

		}

		// Fallback
		model.addAttribute("errorMessage", "Couldn't find user" + username);
		return this.showGuestListManagement(model, happening);
	}

	// Assign given Task to Guest
	@Secured({ "ROLE_HOST"})
	@GetMapping("/assignTaskToGuest")
	public String assignTaskToGuest(Model model, @RequestParam(value = "happeningID") Happening happening,
			@RequestParam String username, @RequestParam(value = "taskID") HappeningTask happeningTask) {


		// User von der Gaesteliste laden (Nicht zugeordnete leute durefen da nicht
		// vorkommen!
		User guest = null;
		HappeningTask task = happeningTaskDao.findByTaskIdAndHappeningHappeningId(happeningTask.getTaskId(),
				happening.getHappeningId());
		if (happening != null) {
			if (happening.getHappeningHost().getUsername().equals(username))
				guest = happening.getHappeningHost();
			else {
				for (User g : happening.getGuestList()) {
					if (g.getUsername().equals(username)) {
						guest = g;
						break;
					}
				}
			}

			if (guest != null && task != null) {
				// TODO: Assign given user to task and show page
				task.setResponsibleUser(guest);
				model.addAttribute("warningMessage", "Not implemented <" + "assignTaskToGuest" + ">");
				return showGuestListManagement(model, happening);
			}
		}

		// Fallback
		model.addAttribute("errorMessage", "Couldn't find user" + username);
		return this.showGuestListManagement(model, happening);

	}

	@Secured({ "ROLE_HOST"})
	@GetMapping("/generateGuestListPDF")
	public String generateGuestListPDF(Model model, @RequestParam(value = "happeningID") Happening happening) {
		// TODO PDF generieren
		model.addAttribute("warningMessage", "Not implemented <" + "generateGuestListPDF" + ">");
		return showGuestListManagement(model, happening);
	}

	// -----------------------------------------------------------------------------------------
	// --- TASKLIST MANAGEMENT ---
	// -----------------------------------------------------------------------------------------
	@Secured({ "ROLE_HOST"})
	@RequestMapping(value = { "showTaskListManagement" })
	public String showTaskListManagement(Model model, @RequestParam(value = "happeningID") Happening happening) {
		model.addAttribute("happening", happening); // Enthaelt die GaesteList & Tasks
		model.addAttribute("happeningTasks", happening.getTaskList()); // Extern benoetigt fuer filterfunction
		return "happeningTaskManagement";
	}

	@Secured({ "ROLE_HOST"})
	@RequestMapping(value = { "showCreateHappeningTaskForm" })
	public String showCreateHappeningTaskForm(Model model, @RequestParam(value = "happeningID") Happening happening) {
		model.addAttribute("happening", happening);
		return "createModifyHappeningTask";
	}

	@Secured({ "ROLE_HOST"})
	@PostMapping("/createNewHappeningTask")
	public String createNewHappeningTask(Model model, @RequestParam(value = "happeningID") Happening happening,
			@RequestParam(value = "responsibleUsername") String username, @Valid HappeningTask newTask,
			BindingResult bindingResult) {
		// Any errors? -> Create a String out of all errors and return to the page
		if (errorsDetected(model, bindingResult))
			return showTaskListManagement(model, happening);

		// Link Task with user and happening
		// newTask.setResponsibleUser(userDao.findFirstByUsername(username));
		newTask.setHappening(happening);
		happening.addHappeningTask(newTask);

		happeningTaskDao.save(newTask);
		return showTaskListManagement(model, happening);
	}

	// Filter GuestListManagement page
	@Secured({ "ROLE_HOST"})
	@PostMapping("/filterHappeningTaskList")
	public String filterHappeingTaskList(Model model, @RequestParam(value = "happeningID") Happening happening,
			@RequestParam String searchString) {
		model.addAttribute("happening", happening);
		
		// Extern benoetigt fuer filterfunction
		model.addAttribute("happeningTasks",
				happeningTaskDao.getFilteredTasks(happening.getHappeningId(), searchString)); 
		return "happeningTaskManagement";
	}

	@Secured({ "ROLE_HOST"})
	@RequestMapping(value = { "showModifyHappeningTaskForm" })
	public String showModifyHappeningTaskForm(Model model, @RequestParam(value = "taskId") HappeningTask task) {
		model.addAttribute("happening", task.getHappening());
		model.addAttribute("happeningTask", task);
		return "createModifyHappeningTask";
	}
	
	@Secured({ "ROLE_HOST"})
	@PostMapping("/modifyExistingHappeningTask")
	public String modifyExistingHappeningTask(Model model, @RequestParam(value = "happeningID") Happening happening) {
		// TODO: Parameter newTask
		// Auslesen des Happening
		// Hinzufügen des Tasks
		model.addAttribute("warningMessage", "Not implemented <" + "modifyExistingHappeningTask" + ">");
		return showTaskListManagement(model, happening);
	}

	@Secured({ "ROLE_HOST"})
	@GetMapping("/deleteExistingTask")
	public String deleteExistingTask(Model model, @RequestParam(value = "taskId") HappeningTask task) {

		task.getHappening().removeHappeningTaskFromList(task);
		happeningTaskDao.delete(task);
		return showTaskListManagement(model, task.getHappening());
	}
	// -----------------------------------------------------------------------------------------

	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		ex.printStackTrace();
		return "error";
	}
}
