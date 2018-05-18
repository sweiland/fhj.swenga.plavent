package at.fh.swenga.plavent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
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
import at.fh.swenga.plavent.model.HappeningTask;
import at.fh.swenga.plavent.model.User;
import at.fh.swenga.plavent.repo.HappeningCategoryRepository;
import at.fh.swenga.plavent.repo.HappeningRepository;
import at.fh.swenga.plavent.repo.HappeningStatusRepository;
import at.fh.swenga.plavent.repo.HappeningTaskRepository;
import at.fh.swenga.plavent.repo.UserRepository;

/**
 * @author Alexander Hoedl:
 * 
 *         Controller to handle Guestlist for a specific Happening - Assign new
 *         Guest to Happening - Remove Guest from Happening - Assign Task to
 *         Guest
 *
 */

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class HappeningGuestlistController {

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

	public HappeningGuestlistController() {
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

	/**
	 * Helper method to check if current logged in user is either owner of happening
	 * or has role ADMIN which allows to modify the task
	 * 
	 * @param happening
	 *            - Happening to check
	 * @param authentication
	 *            - current logged in user
	 * @return
	 */
	public boolean isHappeningHostOrAdmin(Happening happening, Authentication authentication) {

		if (happening == null || authentication == null) {
			return false;
		} else {
			return (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
					|| happening.getHappeningHost().getUsername().equals(authentication.getName()));
		}
	}

	// -----------------------------------------------------------------------------------------
	// --- GUESTLIST MANAGEMENT ---
	// -----------------------------------------------------------------------------------------

	@Secured({ "ROLE_HOST" })
	@RequestMapping(value = { "showGuestListManagement" })
	public String showGuestListManagement(Model model, @RequestParam(value = "happeningID") Happening happening,
			Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		model.addAttribute("happening", happening);
		model.addAttribute("happeningGuests", happening.getGuestList()); // Required in a separate attribute for //
																			// filtering
		model.addAttribute("potentialGuests", userDao.getPotentialGuestsForHappening(happening.getHappeningId()));
		return "happeningGuestManagement";
	}

	// Add guest to guestlist
	@Secured({ "ROLE_HOST" })
	@PostMapping("/assignNewGuestToHappening")
	public String assignNewGuestToHappening(Model model, @RequestParam(value = "happeningID") Happening happening,
			@RequestParam(value = "newGuestUsername") String username, Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		User newGuest = userDao.findFirstByUsername(username);
		if (newGuest != null) {
			// Assign new Guest to happening
			happening.addGuestToList(newGuest);
			happeningDao.save(happening);
			model.addAttribute("message", "User " + newGuest.getUsername() + " added from guestlist!");
		} else {
			model.addAttribute("warningMessage", "User not fonud!");
		}
		return showGuestListManagement(model, happening, authentication);

	}

	// Remove Guest from guestlist
	@Secured({ "ROLE_HOST" })
	@GetMapping("/unassignExistingGuest")
	public String unassignGuest(Model model, @RequestParam(value = "happeningID") Happening happening,
			@RequestParam(value = "username") String username, Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// TODO: Check if given user is on guestlist...
		User guestToRemove = userDao.findFirstByUsername(username);
		if (guestToRemove != null) {
			happening.removeFromList(guestToRemove);
			happeningDao.save(happening);
			model.addAttribute("message", "User " + guestToRemove.getUsername() + " removed from guestlist!");

		} else {
			model.addAttribute("warningMessage", "User or Happening not fonud!");
		}

		return showGuestListManagement(model, happening, authentication);
	}

	// Filter GuestListManagement page
	@Secured({ "ROLE_HOST" })
	@PostMapping("/filterHappeningGuestList")
	public String filterGuestList(Model model, @RequestParam(value = "happeningID") Happening happening,
			@RequestParam String searchString, Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		model.addAttribute("happening", happening);
		model.addAttribute("potentialGuests", userDao.getPotentialGuestsForHappening(happening.getHappeningId()));
		model.addAttribute("warningMessage", "Not implemented <" + "showGuestListManagement" + ">");
		return "happeningGuestManagement";
	}

	// Show form to assign Task to a guest
	@Secured({ "ROLE_HOST" })
	@GetMapping("/showAssignTaskToGuestForm")
	public String showAssignTaskToGuestForm(Model model, @RequestParam(value = "taskID") HappeningTask task,
			Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(task.getHappening(), authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// TODO: set relevante attributes for form
		model.addAttribute("happeningTask", task);
		model.addAttribute("potentialResponsibleGuests", task.getHappening().getGuestList());

		model.addAttribute("warningMessage", "Not implemented <" + "showGuestListManagement" + ">");
		return showGuestListManagement(model, task.getHappening(), authentication);
		// return "assignTaskToGuest";
	}

	// Assign given Task to Guest
	@Secured({ "ROLE_HOST" })
	@PostMapping("/assignTaskToGuest")
	public String assignTaskToGuest(Model model, @RequestParam(value = "taskID") HappeningTask task,
			@RequestParam(value = "responsibleGuest") String username, Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(task.getHappening(), authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}
		
		User guest = userDao.findFirstByUsername(username);
		
		//Valid guest object and guest is on guestlist of happening?
		if(guest != null && task.getHappening().getGuestList().contains(guest)) {
			task.setResponsibleUser(guest);
			happeningTaskDao.save(task);
			model.addAttribute("warningMessage", "Not implemented <" + "showGuestListManagement" + ">");
			return showGuestListManagement(model, task.getHappening(), authentication);
		} else {
			model.addAttribute("warningMessage", "Guest not found or not on guestlist!");
			return "forward:/showHappeningManagement";
		}
	}

	@Secured({ "ROLE_HOST" })
	@GetMapping("/generateGuestListPDF")
	public String generateGuestListPDF(Model model, @RequestParam(value = "happeningID") Happening happening,
			Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// TODO PDF generieren
		model.addAttribute("warningMessage", "Not implemented <" + "generateGuestListPDF" + ">");
		return showGuestListManagement(model, happening, authentication);
	}
	// -----------------------------------------------------------------------------------------

	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		ex.printStackTrace();
		return "error";
	}
}
