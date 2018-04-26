package at.fh.swenga.plavent.controller;

import javax.validation.Valid;

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

import at.fh.swenga.plavent.model.Happening;

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class HappeningManagementController {

	public HappeningManagementController() {
		// TODO Auto-generated constructor stub
	}

	private boolean isLoggedIn(Model model) {
		// TODO: Implement and check if user is logged in...
		// TODO: Check if user has the permission to have access here!
		return true;

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
		// hoedlale16: Verify that user is logged in
		if (!isLoggedIn(model)) {
			return "login";
		}

		// TODO: set attributes
		// model.addAttribute("currLoggedInuser", currLoggedInUser);
		// model.addAttribute("users", userManager.getAllUsers());
		model.addAttribute("warningMessage", "Not implemented <" + "showHappenings" + ">");
		return "happeningManagement";
	}

	@RequestMapping(value = { "showCreateHappeningForm" })
	public String showCreateHappeningForm(Model model) {
		if (!isLoggedIn(model)) {
			return "login";
		}

		// TODO: Implement
		model.addAttribute("warningMessage", "Not implemented <" + "showCreateNewHappingForm" + ">");
		return "createModifyHappening";
	}

	@RequestMapping(value = { "showModifyExistingHappingForm" })
	public String showModifyExistingHappingForm(Model model) {
		if (!isLoggedIn(model)) {
			return "login";
		}

		// TODO: Implement
		model.addAttribute("warningMessage", "Not implemented <" + "showModifyExistingHappingForm" + ">");
		return "happeningManagement";
		// return "createModifyHappening";
	}

	@PostMapping("/createNewHappening")
	public String createNewHappening(@Valid Happening newHappening, BindingResult bindingResult, Model model) {
		if (!isLoggedIn(model)) {
			return "login";
		}

		// TODO: Implement to store new happening
		model.addAttribute("warningMessage", "Not implemented <" + "createNewHappening" + ">");

		return showHappenings(model);
	}

	@PostMapping("/modifyExistingHappening")
	public String modifyExistingHappening(@Valid Happening newHappening, BindingResult bindingResult, Model model) {
		if (!isLoggedIn(model)) {
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
	public String deleteHappening(Model model, @RequestParam String happeningName) {
		if (!isLoggedIn(model)) {
			return "login";
		}

		// TODO: implement deletion
		model.addAttribute("warningMessage", "Not implemented <" + "deleteExistingHappening" + ">");

		return showHappenings(model);
	}

	@PostMapping("/filterHappenings")
	public String filterHappenings(Model model, @RequestParam String searchString) {
		if (!isLoggedIn(model)) {
			return "login";
		}

		// TODO: implement deletion
		model.addAttribute("warningMessage", "Not implemented <filterHappenings>");
		// model.addAttribute("happenings",
		// userManager.getFilteredHappenings(searchString));
		// model.addAttribute("currLoggedInuser", currLoggedInUser);
		return "happeningManagement";
	}

	// -----------------------------------------------------------------------------------------
	// --- GUESTLIST MANAGEMENT ---
	// -----------------------------------------------------------------------------------------

	@RequestMapping(value = { "showGuestListManagement" })
	public String showGuestListManagement(Model model, @RequestParam String happeningName) {
		// TODO: Parameter Happening das ausgewaehlt wurde ueber ID

		// TODO: Set correct required model attributes
		model.addAttribute("warningMessage", "Not implemented <" + "showGuestListManagement" + ">");
		// model.addAttribute("happening", happening);

		// TODO: Struktur: Hat nicht ein Happening eine Liste von Guesten und Tasks?
		// model.addAttribute("happeningGuests", TODO);
		// model.addAttribute("happeningTasks", TODO);

		// model.addAttribute("users", userManager.getAllUsers()); //Sämtliche User als
		// moegliche Gueste
		// verfügbare Benutzer zum möglichen Zuordnen

		return "happeningGuestManagement";
	}

	// Add guest to guestlist
	@PostMapping("/assignNewGuestToHappening")
	public String assignNewGuestToHappening(Model model, @RequestParam String happeningName, @RequestParam String userName) {
		// TODO: User uber corrected identifier(ID?) holen und zuordnen

		model.addAttribute("warningMessage", "Not implemented <" + "assignNewGuest" + ">");

		return showGuestListManagement(model, happeningName);
	}

	// Remove Guest from guestlist
	@PostMapping("/unassignExistingGuest")
	public String unassignGuest(Model model, @RequestParam String happeningName, @RequestParam String userName) {
		// TODO: User uber corrected identifier(ID?) holen und zuordnen
		// TODO: Loeshcen vo nder Guesteliste
		// TODO: Mögliche Zuordnung der Tasks aufheben und noch nicht zugewiesene Tasks
		// laden

		// TODO: Set correct required model attributes
		model.addAttribute("warningMessage", "Not implemented <" + "unassignExistingGuest" + ">");

		return showGuestListManagement(model, happeningName);
	}

	// Filter GuestListManagement page
	@PostMapping("/filterHappeningGuestList")
	public String filterGuestList(Model model, @RequestParam String happeningName, @RequestParam String searchString) {
		// TODO: Laden des geforderten Events
		// Filtern der guesteliste nach Searchstring

		// TODO: Parameter Happening das ausgewaehlt wurde ueber ID

		// TODO: Set correct required model attributes
		model.addAttribute("warningMessage", "Not implemented <" + "showGuestListManagement" + ">");
		// model.addAttribute("happening", happening);

		// TODO: Struktur: Hat nicht ein Happening eine Liste von Guesten und Tasks?
		// model.addAttribute("happeningGuests", TODO);
		// model.addAttribute("happeningTasks", TODO);

		// model.addAttribute("users", userManager.getAllUsers()); //Sämtliche
		// verfügbare Benutzer zum möglichen Zuordnen

		return "happeningGuestManagement";
	}

	// Show form to assign Task to a guest
	@GetMapping("/showAssignTaskToGuestForm")
	public String showAssignTaskToGuestForm(Model model, @RequestParam String happeningName,
			@RequestParam String username) {

		if (!isLoggedIn(model)) {
			return "login";
		}

		// TODO: User anhand der auswahl laden
		// User guest = userManager.getUser(username);

		/*
		 * if (user != null) {
		 * 
		 * //TODO: Alle relevanten Attribute Setzen
		 * //model.addAttribute("happeningTasks", TODO-Nur die noch nicht
		 * zugewiesenen!); //model.addAttribute("happening", happening);
		 * //model.addAttribute("happeningGuest", guest); return "assignTaskToGuest"; }
		 * else { model.addAttribute("errorMessage", "Couldn't find user" + username);
		 * return this.showGuestListManagement(model, happeningName) }
		 */
		model.addAttribute("warningMessage", "Not implemented <" + "showGuestListManagement" + ">");
		return showGuestListManagement(model, happeningName);

	}

	// Assign given Task to Guest
	@GetMapping("/assignTaskToGuest")
	public String assignTaskToGuest(Model model, @RequestParam String happeningName, @RequestParam String username,
			@RequestParam String taskName) {
		if (!isLoggedIn(model)) {
			return "login";
		}

		// TODO: Assign given user to task and show page

		model.addAttribute("warningMessage", "Not implemented <" + "assignTaskToGuest" + ">");
		return showGuestListManagement(model, happeningName);
	}

	@GetMapping("/generateGuestListPDF")
	public String generateGuestListPDF(Model model, @RequestParam String happeningName) {
		if (!isLoggedIn(model)) {
			return "login";
		}

		// TODO: Laden des Happening
		// Laden der Gueste
		// PDF generieren
		model.addAttribute("warningMessage", "Not implemented <" + "generateGuestListPDF" + ">");
		return showGuestListManagement(model, happeningName);
	}

	// -----------------------------------------------------------------------------------------
	// --- TASKLIST MANAGEMENT ---
	// -----------------------------------------------------------------------------------------

	@RequestMapping(value = { "showTaskListManagement" })
	public String showTaskListManagement(Model model, @RequestParam String happeningName) {
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
	public String showCreateHappeningTaskForm(Model model, @RequestParam String happeningName) {
		// TODO: Noetige Attribute im Model noch setzen(z.B. sämtliche gueste des
		// Happening fuer direkte zuordnung durch DropDown
		model.addAttribute("warningMessage", "Not implemented <" + "showCreateHappeningTaskForm" + ">");
		return "createModifyHappeningTask";
	}

	@PostMapping("/createNewHappeningTask")
	public String createNewHappeningTask(Model model, @RequestParam String happeningName) {
		// TODO: Parameter newTask
		// Auslesen des Happening
		// Hinzufügen des Tasks
		return showTaskListManagement(model, happeningName);
	}

	// Filter GuestListManagement page
	@PostMapping("/filterHappeningTaskList")
	public String filterHappeingTaskList(Model model, @RequestParam String happeningName, @RequestParam String searchString) {
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
	public String showModifyHappeningTaskForm(Model model, @RequestParam String happeningName, @RequestParam String taskName) {
		// TODO: Noetige Attribute im Model noch setzen(z.B. sämtliche gueste des
		// Happening fuer direkte zuordnung durch DropDown
		//TODO richtigen Task setzen in der form
		model.addAttribute("warningMessage", "Not implemented <" + "showModifyHappeningTaskForm" + ">");
		return "createModifyHappeningTask";
	}

	@PostMapping("/modifyExistingHappeningTask")
	public String modifyExistingHappeningTask(Model model, @RequestParam String happeningName) {
		// TODO: Parameter newTask
		// Auslesen des Happening
		// Hinzufügen des Tasks
		model.addAttribute("warningMessage", "Not implemented <" + "modifyExistingHappeningTask" + ">");
		return showTaskListManagement(model, happeningName);
	}

	
	@PostMapping("/deleteExistingTask")
	public String deleteExistingTask(Model model, @RequestParam String happeningName) {
		// TODO: Parameter newTask
		// Auslesen des Happening
		// Hinzufügen des Tasks
		model.addAttribute("warningMessage", "Not implemented <" + "deleteExistingTask" + ">");
		return showTaskListManagement(model, happeningName);
	}
	// -----------------------------------------------------------------------------------------
	
	
	
	
	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		return "error";
	}
}
