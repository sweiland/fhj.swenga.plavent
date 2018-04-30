package at.fh.swenga.plavent.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.fh.swenga.plavent.dao.HappeningCategoryDao;
import at.fh.swenga.plavent.dao.HappeningDao;
import at.fh.swenga.plavent.dao.HappeningTaskDao;
import at.fh.swenga.plavent.dao.UserDao;
import at.fh.swenga.plavent.model.Happening;
import at.fh.swenga.plavent.model.HappeningTask;
import at.fh.swenga.plavent.model.User;

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class HappeningManagementController {

	@Autowired
	HappeningDao happeningDao;

	@Autowired
	HappeningTaskDao happeningTaskDao;

	@Autowired
	HappeningCategoryDao happeningCategoryDao;

	@Autowired
	UserDao userDao;

	public HappeningManagementController() {
		// TODO Auto-generated constructor stub
	}

	private boolean isLoggedInAndHasPermission(Model model) {
		// hoedlale16: Verify that user is logged in
		if (!UserManagementController.isLoggedIn(model)) {
			return false;
		} else {
			// User logged in - check if he has the permission for happening management
			User currLoggedInUser = UserManagementController.getCurrentLoggedInUser();
			return currLoggedInUser.getRole().isPermissionHappeningMgmt();
		}
	}

	private boolean errorsDetected(Model model, BindingResult bindingResult) {
		// Any errors? -> Create a String out of all errors and return to the page
		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid";
			}
			model.addAttribute("errorMessage", errorMessage);
			return true;
		}
		return false;
	}

	@RequestMapping(value = { "showHappeningManagement" })
	public String showHappenings(Model model) {

		// LoggedIn and has permission?
		if (!isLoggedInAndHasPermission(model)) {
			return "login";
		}

		model.addAttribute("happenings", happeningDao.getHappenings());
		return "happeningManagement";
	}

	@RequestMapping(value = { "showCreateHappeningForm" })
	public String showCreateHappeningForm(Model model) {
		// LoggedIn and has permission?
		if (!isLoggedInAndHasPermission(model)) {
			return "login";
		}

		// TODO: Implement
		model.addAttribute("warningMessage", "Not implemented <" + "showCreateNewHappingForm" + ">");
		return "createModifyHappening";
	}

	@RequestMapping(value = { "showModifyExistingHappingForm" })
	public String showModifyExistingHappingForm(Model model, @RequestParam(value = "happeningID") int happeningID) {
		// LoggedIn and has permission?
		if (!isLoggedInAndHasPermission(model)) {
			return "login";
		}

		Happening happening = happeningDao.getHappening(happeningID);
		model.addAttribute("happening", happening);

		// TODO: Implement
		model.addAttribute("warningMessage", "Not implemented <" + "showModifyExistingHappingForm" + ">");
		// return "createModifyHappening";
		return "happeningManagement";
	}

	@PostMapping("/createNewHappening")
	public String createNewHappening(@Valid Happening newHappening, BindingResult bindingResult, Model model) {
		// LoggedIn and has permission?
		if (!isLoggedInAndHasPermission(model)) {
			return "login";
		}

		// happeningDao.persist(newHappening);

		// TODO: Implement to store new happening
		model.addAttribute("warningMessage", "Not implemented <" + "createNewHappening" + ">");

		return showHappenings(model);
	}

	@PostMapping("/modifyExistingHappening")
	public String modifyExistingHappening(@Valid Happening newHappening, BindingResult bindingResult, Model model) {
		// LoggedIn and has permission?
		if (!isLoggedInAndHasPermission(model)) {
			return "login";
		}

		// Check for errors
		if (errorsDetected(model, bindingResult))
			return showHappenings(model);

		// TODO: Implement to store new happening
		model.addAttribute("warningMessage", "Not implemented <" + "modifyExistingHappening" + ">");

		return showHappenings(model);
	}

	@GetMapping("/deleteExistingHappening")
	public String deleteHappening(Model model, @RequestParam(value = "happeningID") int happeningID) {
		// LoggedIn and has permission?
		if (!isLoggedInAndHasPermission(model)) {
			return "login";
		}

		// TODO: implement deletion
		// happeningDao.deleteHappeing(happeningID);
		model.addAttribute("warningMessage", "Not implemented <" + "deleteExistingHappening" + ">");

		return showHappenings(model);
	}

	@PostMapping("/filterHappenings")
	public String filterHappenings(Model model, @RequestParam String searchString) {
		// LoggedIn and has permission?
		if (!isLoggedInAndHasPermission(model)) {
			return "login";
		}

		// TODO: implement deletion
		model.addAttribute("warningMessage", "Not implemented <filterHappenings>");
		// model.addAttribute("happenings",
		// userManager.getFilteredHappenings(searchString));
		return "happeningManagement";
	}

	// -----------------------------------------------------------------------------------------
	// --- GUESTLIST MANAGEMENT ---
	// -----------------------------------------------------------------------------------------

	@RequestMapping(value = { "showGuestListManagement" })
	public String showGuestListManagement(Model model, @RequestParam(value = "happeningID") int happeningID) {
		// TODO: Parameter Happening das ausgewaehlt wurde ueber ID
		Happening happening = happeningDao.getHappening(happeningID);
		if (happening != null) {
			model.addAttribute("happening", happening);
			model.addAttribute("happeningGuests", happening.getGuestList());
			// Get all Tasks for happening without assignment
			// model.addAttribute("happeningTasks",
			// happeningDao.getUnassignedTasks(happeningID));

			// Get all potential users which are not guests yet but can become one
			// potentially
			// model.addAttribute("users",
			// userDao.getPotentialGuestsForHappening(happeningID));

			// TODO: Set correct required model attributes
			model.addAttribute("warningMessage", "Not implemented <" + "showGuestListManagement" + ">");
			return "happeningGuestManagement";
		}

		model.addAttribute("warningMessage", "Happening not found");
		return showHappenings(model);
	}

	// Add guest to guestlist
	@PostMapping("/assignNewGuestToHappening")
	public String assignNewGuestToHappening(Model model, @RequestParam(value = "happeningID") int happeningID,
			@RequestParam(value = "username") String username) {

		// TODO: User uber corrected identifier(ID?) holen und zuordnen
		Happening happening = happeningDao.getHappening(happeningID);
		User user = userDao.getUser(username);
		if (happening != null && user != null) {
			model.addAttribute("warningMessage", "Not implemented <" + "assignNewGuest" + ">");
			happening.addGuestToList(user);
		} else {
			model.addAttribute("warningMessage", "User or Happening not fonud!");
		}

		return showGuestListManagement(model, happeningID);
	}

	// Remove Guest from guestlist
	@PostMapping("/unassignExistingGuest")
	public String unassignGuest(Model model, @RequestParam(value = "happeningID") int happeningID,
			@RequestParam(value = "username") String username) {

		Happening happening = happeningDao.getHappening(happeningID);
		if (happening != null) {
			model.addAttribute("warningMessage", "Not implemented <" + "assignNewGuest" + ">");
			// TODO: Mögliche Zuordnung der Tasks aufheben und noch nicht zugewiesene Tasks
			// laden
			// happeningDao.unassignUserFromTasks(happeningID, username);
			if (!happening.removeFromList(username)) {
				model.addAttribute("warningMessage", "Error while unassigning User!");
			}
		} else {
			model.addAttribute("warningMessage", "User or Happening not fonud!");
		}

		model.addAttribute("warningMessage", "Not implemented <" + "unassignExistingGuest" + ">");
		return showGuestListManagement(model, happeningID);
	}

	// Filter GuestListManagement page
	@PostMapping("/filterHappeningGuestList")
	public String filterGuestList(Model model, @RequestParam(value = "happeningID") int happeningID,
			@RequestParam String searchString) {

		Happening happening = happeningDao.getHappening(happeningID);
		if (happening != null) {
			model.addAttribute("happening", happening);
			model.addAttribute("happeningGuests", happening.getGuestList());
			// Get all Tasks for happening without assignment
			model.addAttribute("happeningTasks", happeningTaskDao.getUnassignedTasks(happeningID));

			// Get all potential users which are not guests yet but can become one
			// potentially
			// model.addAttribute("users",userDao.getPotentialGuestsForHappening(happeningID));

			// TODO: Set correct required model attributes
			model.addAttribute("warningMessage", "Not implemented <" + "showGuestListManagement" + ">");
			return "happeningGuestManagement";
		}

		model.addAttribute("warningMessage", "Happening not found");
		return showHappenings(model);
	}

	// Show form to assign Task to a guest
	@GetMapping("/showAssignTaskToGuestForm")
	public String showAssignTaskToGuestForm(Model model, @RequestParam(value = "happeningID") int happeningID,
			@RequestParam String username) {

		// LoggedIn and has permission?
		if (!isLoggedInAndHasPermission(model)) {
			return "login";
		}

		// User von der Gaesteliste laden (Nicht zugeordnete leute durefen da nicht
		// vorkommen!
		Happening happening = happeningDao.getHappening(happeningID);
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
				model.addAttribute("happeningTasks", happeningTaskDao.getUnassignedTasks(happeningID));
				model.addAttribute("happening", happening);
				model.addAttribute("happeningGuest", guest);

				model.addAttribute("warningMessage", "Not implemented <" + "showGuestListManagement" + ">");
				return showGuestListManagement(model, happeningID);
				// return "assignTaskToGuest";
			}

		}

		// Fallback
		model.addAttribute("errorMessage", "Couldn't find user" + username);
		return this.showGuestListManagement(model, happeningID);
	}

	// Assign given Task to Guest
	@GetMapping("/assignTaskToGuest")
	public String assignTaskToGuest(Model model, @RequestParam(value = "happeningID") int happeningID,
			@RequestParam String username, @RequestParam(value = "taskID") int taskid) {
		// LoggedIn and has permission?
		if (!isLoggedInAndHasPermission(model)) {
			return "login";
		}

		// User von der Gaesteliste laden (Nicht zugeordnete leute durefen da nicht
		// vorkommen!
		Happening happening = happeningDao.getHappening(happeningID);
		User guest = null;
		HappeningTask task = happeningTaskDao.getHappeningTask(taskid, happeningID);
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
				return showGuestListManagement(model, happeningID);
			}
		}

		// Fallback
		model.addAttribute("errorMessage", "Couldn't find user" + username);
		return this.showGuestListManagement(model, happeningID);

	}

	@GetMapping("/generateGuestListPDF")
	public String generateGuestListPDF(Model model, @RequestParam(value = "happeningID") int happeningID) {
		// LoggedIn and has permission?
		if (!isLoggedInAndHasPermission(model)) {
			return "login";
		}

		// TODO: Laden des Happening
		// Laden der Gueste
		// PDF generieren
		model.addAttribute("warningMessage", "Not implemented <" + "generateGuestListPDF" + ">");
		return showGuestListManagement(model, happeningID);
	}

	// -----------------------------------------------------------------------------------------
	// --- TASKLIST MANAGEMENT ---
	// -----------------------------------------------------------------------------------------

	@RequestMapping(value = { "showTaskListManagement" })
	public String showTaskListManagement(Model model, @RequestParam(value = "happeningID") int happeningID) {
		// TODO: Parameter Happening das ausgewaehlt wurde ueber ID

		// TODO: Set correct required model attributes
		model.addAttribute("warningMessage", "Not implemented <" + "showTaskListManagement" + ">");
		// model.addAttribute("happening", happening);

		// TODO: Struktur: Hat nicht ein Happening eine Liste von Guesten und Tasks?
		// model.addAttribute("happeningGuests", TODO);
		// model.addAttribute("happeningTasks", TODO);

		// model.addAttribute("users", userManager.getAllUsers()); //Sämtliche User als
		// moegliche Gueste
		// verfügbare Benutzer zum möglichen Zuordnen

		return "happeningTaskManagement";
	}

	@RequestMapping(value = { "showCreateHappeningTaskForm" })
	public String showCreateHappeningTaskForm(Model model, @RequestParam(value = "happeningID") int happeningID) {
		// TODO: Noetige Attribute im Model noch setzen(z.B. sämtliche gueste des
		// Happening fuer direkte zuordnung durch DropDown
		model.addAttribute("warningMessage", "Not implemented <" + "showCreateHappeningTaskForm" + ">");
		return "createModifyHappeningTask";
	}

	@PostMapping("/createNewHappeningTask")
	public String createNewHappeningTask(Model model, @RequestParam(value = "happeningID") int happeningID) {
		// TODO: Parameter newTask
		// Auslesen des Happening
		// Hinzufügen des Tasks
		return showTaskListManagement(model, happeningID);
	}

	// Filter GuestListManagement page
	@PostMapping("/filterHappeningTaskList")
	public String filterHappeingTaskList(Model model, @RequestParam String happeningName,
			@RequestParam String searchString) {
		// TODO: Laden des geforderten Events
		// Filtern der tasksnach Searchstring

		// TODO: Set correct required model attributes
		model.addAttribute("warningMessage", "Not implemented <" + "showTaskListManagement" + ">");
		// model.addAttribute("happening", happening);

		// TODO: Struktur: Hat nicht ein Happening eine Liste von Guesten und Tasks?
		// model.addAttribute("happeningGuests", TODO);
		// model.addAttribute("happeningTasks", userManager.getFilteredTasks());

		// model.addAttribute("users", userManager.getAllUsers()); //Sämtliche User als
		// moegliche Gueste
		// verfügbare Benutzer zum möglichen Zuordnen

		return "happeningTaskManagement";
	}

	@RequestMapping(value = { "showModifyHappeningTaskForm" })
	public String showModifyHappeningTaskForm(Model model, @RequestParam String happeningName,
			@RequestParam String taskName) {
		// TODO: Noetige Attribute im Model noch setzen(z.B. sämtliche gueste des
		// Happening fuer direkte zuordnung durch DropDown
		// TODO richtigen Task setzen in der form
		model.addAttribute("warningMessage", "Not implemented <" + "showModifyHappeningTaskForm" + ">");
		return "createModifyHappeningTask";
	}

	@PostMapping("/modifyExistingHappeningTask")
	public String modifyExistingHappeningTask(Model model, @RequestParam(value = "happeningID") int happeningID) {
		// TODO: Parameter newTask
		// Auslesen des Happening
		// Hinzufügen des Tasks
		model.addAttribute("warningMessage", "Not implemented <" + "modifyExistingHappeningTask" + ">");
		return showTaskListManagement(model, happeningID);
	}

	@PostMapping("/deleteExistingTask")
	public String deleteExistingTask(Model model, @RequestParam(value = "happeningID") int happeningID) {
		// TODO: Parameter newTask
		// Auslesen des Happening
		// Hinzufügen des Tasks
		model.addAttribute("warningMessage", "Not implemented <" + "deleteExistingTask" + ">");
		return showTaskListManagement(model, happeningID);
	}
	// -----------------------------------------------------------------------------------------

	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		return "error";
	}
}
