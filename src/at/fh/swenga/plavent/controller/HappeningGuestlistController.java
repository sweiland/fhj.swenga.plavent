package at.fh.swenga.plavent.controller;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import at.fh.swenga.plavent.repo.HappeningGuestlistRepository;
import at.fh.swenga.plavent.repo.HappeningRepository;
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
	HappeningRepository happeningRepo;

	@Autowired
	HappeningGuestlistRepository happeningGuestlistRepo;

	@Autowired
	HappeningTaskRepository happeningTaskRepo;

	@Autowired
	UserRepository userRepo;

	public HappeningGuestlistController() {
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

	/**
	 * Helper method to include the paging handling. The content is static, so user
	 * can just change the pagenumber
	 * 
	 * @param pageNr
	 *            .. Page number which should be displayed
	 * @return
	 */
	private PageRequest generatePageRequest(int pageNr) {
		return PageRequest.of(pageNr, 6);
	}

	// -----------------------------------------------------------------------------------------
	// --- GUESTLIST MANAGEMENT ---
	// -----------------------------------------------------------------------------------------

	@Secured({ "ROLE_HOST" })
	@RequestMapping(value = { "showGuestListManagement" })
	public String showGuestListManagement(Model model, @RequestParam(value = "happeningID") Happening happening,
			Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)
				|| "DELETED".equals(happening.getHappeningStatus().getStatusName())) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// Show first page with max 10 elements...
		PageRequest page = generatePageRequest(0);
		Page<User> happeningGuestsPage = happeningGuestlistRepo.getGuestListAsPage(happening.getHappeningId(), page);

		model.addAttribute("happening", happening);
		model.addAttribute("happeningGuests", happeningGuestsPage);
		model.addAttribute("potentialGuests",
				happeningGuestlistRepo.getPotentialGuestsForHappening(happening.getHappeningId()));

		model.addAttribute("currPage", happeningGuestsPage.getNumber());
		model.addAttribute("totalPages", happeningGuestsPage.getTotalPages());

		return "happeningGuestManagement";
	}

	@RequestMapping(value = { "showGuestListManagementPage" })
	public String showGuestListManagementPage(Model model, @RequestParam(value = "page") int pageNr,
			@RequestParam(value = "happeningID") Happening happening, Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)
				|| "DELETED".equals(happening.getHappeningStatus().getStatusName())) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// Show first page with max 10 elements...
		PageRequest page = generatePageRequest(pageNr);
		Page<User> happeningGuestsPage = happeningGuestlistRepo.getGuestListAsPage(happening.getHappeningId(), page);

		model.addAttribute("happening", happening);
		model.addAttribute("happeningGuests", happeningGuestsPage);
		model.addAttribute("potentialGuests",
				happeningGuestlistRepo.getPotentialGuestsForHappening(happening.getHappeningId()));

		model.addAttribute("currPage", happeningGuestsPage.getNumber());
		model.addAttribute("totalPages", happeningGuestsPage.getTotalPages());

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

		User newGuest = userRepo.findFirstByUsername(username);
		if (newGuest != null) {
			// Assign new Guest to happening
			happening.addGuestToList(newGuest);
			happeningRepo.save(happening);
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

		// Check if given user exists and is on guestlist...
		User guestToRemove = userRepo.findFirstByUsername(username);
		if (guestToRemove != null
				&& happeningGuestlistRepo.getGuestList(happening.getHappeningId()).contains(guestToRemove)) {

			/*
			 * Check for tasks which are under control of deleted guest (User is responsible
			 * for task). In this case, remove assignment and inform via web-page
			 */
			List<HappeningTask> respTasks = happeningTaskRepo
					.findByResponsibleUserUsernameAndHappeningHappeningId(username, happening.getHappeningId());
			if (!respTasks.isEmpty()) {
				for (HappeningTask task : respTasks) {
					task.setResponsibleUser(null);
					happeningTaskRepo.save(task);
				}
				model.addAttribute("message",
						"Removed Task-Assignement for <" + respTasks.size() + "> tasks of user <" + username + ">");
			}

			happening.removeFromList(guestToRemove);
			happeningRepo.save(happening);
			model.addAttribute("warningMessage", "User " + guestToRemove.getUsername() + " removed from guestlist!");

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

		// Show first page with max 10 elements...
		PageRequest page = generatePageRequest(0);
		Page<User> happeningGuestsPage = happeningGuestlistRepo.getFilteredGuestList(happening.getHappeningId(),
				searchString, page);

		model.addAttribute("happening", happening);
		model.addAttribute("happeningGuests", happeningGuestsPage);
		model.addAttribute("potentialGuests",
				happeningGuestlistRepo.getPotentialGuestsForHappening(happening.getHappeningId()));

		model.addAttribute("currPage", happeningGuestsPage.getNumber());
		model.addAttribute("totalPages", happeningGuestsPage.getTotalPages());
		return "happeningGuestManagement";
	}

	// Show form to assign Task to a guest
	@Secured({ "ROLE_HOST" })
	@RequestMapping(value = { "showGuestTaskDetailsForm" })
	public String showGuestTaskDetailsForm(Model model, @RequestParam(value = "happeningID") Happening happening,
			@RequestParam(value = "username") String username, Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		User guest = happeningGuestlistRepo.getGuestFromGuestList(happening.getHappeningId(), username);

		if (guest == null) {
			model.addAttribute("warningMessage", "Guest not found on guestlist!");
			showGuestListManagement(model, happening, authentication);
		} else {
			model.addAttribute("happening", happening);
			model.addAttribute("happeningGuest", guest);
			model.addAttribute("assignedTasks",
					happeningTaskRepo.getAllAssignedTasks(happening.getHappeningId(), guest));
			model.addAttribute("unassignedTasks", happeningTaskRepo.getAllUnassignedTasks(happening.getHappeningId()));
		}

		return "guestTaskDetail";
	}

	// Assign given Task to Guest
	@Secured({ "ROLE_HOST" })
	@PostMapping("/unassignTaskToGuest")
	public String unassignTaskToGuest(Model model, @RequestParam(value = "taskId") HappeningTask task,
			@RequestParam(value = "responsibleGuest") String username, Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(task.getHappening(), authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		User guest = userRepo.findFirstByUsername(username);

		// Valid guest object and guest is on guestlist of happening and given task is
		// managed by guest...
		if (guest != null && task.getHappening().getGuestList().contains(guest)
				&& happeningTaskRepo.getAllAssignedTasks(task.getHappening().getHappeningId(), guest).contains(task)) {
			task.setResponsibleUser(null);
			happeningTaskRepo.save(task);
			return showGuestTaskDetailsForm(model, task.getHappening(), username, authentication);
		} else {
			model.addAttribute("warningMessage", "Guest not found or not on guestlist!");
			return "forward:/showHappeningManagement";
		}
	}

	// Assign given Task to Guest
	@Secured({ "ROLE_HOST" })
	@PostMapping("/assignTaskToGuest")
	public String assignTaskToGuest(Model model, @RequestParam(value = "taskId") HappeningTask task,
			@RequestParam(value = "responsibleGuest") String username, Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(task.getHappening(), authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		User guest = userRepo.findFirstByUsername(username);

		// Valid guest object and guest is on guestlist of happening?
		if (guest != null && task.getHappening().getGuestList().contains(guest)) {
			task.setResponsibleUser(guest);
			happeningTaskRepo.save(task);
			return showGuestTaskDetailsForm(model, task.getHappening(), username, authentication);
		} else {
			model.addAttribute("warningMessage", "Guest not found or not on guestlist!");
			return "forward:/showHappeningManagement";
		}
	}

	@Secured({ "ROLE_HOST" })
	@PostMapping("/generateGuestListPDF")
	public String generateGuestListPDF(Model model, @RequestParam(value = "happeningID") Happening happening,
			Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}
		
		List<User> guestList = happeningGuestlistRepo.getGuestList(happening.getHappeningId());
		
		if(CollectionUtils.isEmpty(guestList)) {
			model.addAttribute("warningMessage", "No Guests found for Happening <" + happening.getHappeningName() +">!");
			return "forward:/showGuestListManagement";
		}
		//PDF generieren
		model.addAttribute("guestList", guestList);
		model.addAttribute("happening",happening);		
		return "pdfReport";	
	}

	// -----------------------------------------------------------------------------------------

	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		ex.printStackTrace();
		return "error";
	}
}
