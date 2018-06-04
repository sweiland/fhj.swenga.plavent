package at.fh.swenga.plavent.controller;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import at.fh.swenga.plavent.model.ApplicationProperty;
import at.fh.swenga.plavent.model.Happening;
import at.fh.swenga.plavent.model.HappeningTask;
import at.fh.swenga.plavent.model.User;
import at.fh.swenga.plavent.repo.ApplicationPropertyRepository;
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

	@Autowired
	ApplicationPropertyRepository appPropertyRepo;

	@Autowired
	private MailSender mailSender;

	public HappeningGuestlistController() {
	}

	/**
	 * Helper method to check if current logged in user is either owner of happening
	 * or has role ADMIN which allows to modify the task
	 * 
	 * @param happening
	 *            - Happening to check
	 * @param authentication
	 *            - current logged in user
	 * @param ignoreStartdateCheck
	 *            - Ignore check on start date for HOST
	 * @return
	 */
	public boolean isHappeningHostOrAdmin(Happening happening, Authentication authentication,
			boolean ignoreStartdateCheck) {
		if (happening == null || authentication == null) {
			return false;
		} else {
			// Admins are allowed to modify all happenings
			if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")))
				return true;

			/*
			 * Hosts are allowed to modify their happenings when happening is in the future.
			 * Modification of started happenings or happenings in past depend on
			 * application property
			 */
			if (happening.getHappeningHost().getUsername().equals(authentication.getName())) {
				Optional<ApplicationProperty> prop = appPropertyRepo.findById("HAPPENING.MODIFICATION.AFTER.START");
				// Check happening start date if property is present and true
				if ((!ignoreStartdateCheck) && prop.isPresent() && (!prop.get().isValue())) {
					Calendar now = Calendar.getInstance();
					return happening.getStart().getTime().after(now.getTime());
				} else
					// When property not found or false do not check date.
					return true;
			} else {
				/*
				 * Neither ROLE_ADMIN nor host of given happening
				 */
				return false;
			}
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
		if (!isHappeningHostOrAdmin(happening, authentication, true)
				|| "DELETED".equals(happening.getHappeningStatus().getStatusName())) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// First: load guestlist of happening (initialized as "LAZY"))
		happening.setGuestList(happeningGuestlistRepo.getGuestList(happening.getHappeningId()));

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
		if (!isHappeningHostOrAdmin(happening, authentication, true)
				|| "DELETED".equals(happening.getHappeningStatus().getStatusName())) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// Load Guests (initialized as "LAZY"))
		happening.setGuestList(happeningGuestlistRepo.getGuestList(happening.getHappeningId()));

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
		if (!isHappeningHostOrAdmin(happening, authentication, false)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		User newGuest = userRepo.findFirstByUsername(username);
		if (newGuest != null) {

			// First: load guestlist of happening (initialized as "LAZY"))
			happening.setGuestList(happeningGuestlistRepo.getGuestList(happening.getHappeningId()));

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
		if (!isHappeningHostOrAdmin(happening, authentication, false)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// First: load guestlist of happening (initialized as "LAZY"))
		List<User> guestList = happeningGuestlistRepo.getGuestList(happening.getHappeningId());
		happening.setGuestList(guestList);

		// Check if given user exists and is on guestlist...
		User guestToRemove = userRepo.findFirstByUsername(username);
		if (guestToRemove != null && guestList.contains(guestToRemove)) {

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

			// Load Guests first (initialized as "LAZY"))
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
		if (!isHappeningHostOrAdmin(happening, authentication, true)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// First: load guestlist of happening (initialized as "LAZY"))
		happening.setGuestList(happeningGuestlistRepo.getGuestList(happening.getHappeningId()));

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
		if (!isHappeningHostOrAdmin(happening, authentication, false)) {
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

		// First: load Happening for task (initialized is "LAZY"))
		Happening happening = happeningRepo.getHappeningForTask(task.getTaskId());

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication, false)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		User guest = userRepo.findFirstByUsername(username);

		// First: load guestlist of happening (initialized as "LAZY"))
		List<User> happeningGuestList = happeningGuestlistRepo.getGuestList(happening.getHappeningId());
		happening.setGuestList(happeningGuestList);

		List<HappeningTask> assTasksToUser = happeningTaskRepo.getAllAssignedTasks(happening.getHappeningId(), guest);

		if (guest != null && happeningGuestList.contains(guest) && assTasksToUser.contains(task)) {
			task.setResponsibleUser(null);
			happeningTaskRepo.save(task);
			return showGuestTaskDetailsForm(model, happening, username, authentication);
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

		// First: load Happening for task (initialized is "LAZY"))
		Happening happening = happeningRepo.getHappeningForTask(task.getTaskId());

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication, false)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		User guest = userRepo.findFirstByUsername(username);

		// First: load guestlist of happening (initialized as "LAZY"))
		List<User> happeningGuestList = happeningGuestlistRepo.getGuestList(happening.getHappeningId());
		happening.setGuestList(happeningGuestList);

		// Valid guest object and guest is on guestlist of happening?
		if (guest != null && happeningGuestList.contains(guest)) {
			task.setResponsibleUser(guest);
			happeningTaskRepo.save(task);
			return showGuestTaskDetailsForm(model, happening, username, authentication);
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
		if (!isHappeningHostOrAdmin(happening, authentication, true)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// First: load guestlist of happening (initialized as "LAZY"))
		List<User> guestList = happeningGuestlistRepo.getGuestList(happening.getHappeningId());
		happening.setGuestList(guestList);

		if (CollectionUtils.isEmpty(guestList)) {
			model.addAttribute("warningMessage",
					"No Guests found for Happening <" + happening.getHappeningName() + ">!");
			return "forward:/showGuestListManagement";
		}
		// PDF generieren
		model.addAttribute("guestList", guestList);
		model.addAttribute("happening", happening);
		return "pdfReport";
	}

	@Secured({ "ROLE_HOST" })
	@PostMapping("/sendMailReminder")
	public String sendMailReminder(Model model, @RequestParam(value = "happeningID") Happening happening,
			Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication, true)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// First: load guestlist of happening (initialized as "LAZY"))
		List<User> guestList = happeningGuestlistRepo.getGuestList(happening.getHappeningId());
		happening.setGuestList(guestList);

		if (CollectionUtils.isEmpty(guestList)) {
			model.addAttribute("warningMessage",
					"No Guests found for Happening <" + happening.getHappeningName() + ">!");
			return "forward:/showGuestListManagement";
		}
		User host = happening.getHappeningHost();
		String hostName = host.getFirstname() + " " + happening.getHappeningHost().getLastname();

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setSubject("Reminder for Happening " + happening.getHappeningName());
		msg.setFrom("noreplay@plavent.com");
		msg.setText("Dear Ladies and Gentleman!\n\n" + "I would like to invite you once again to my event.\n"
				+ "Here again the most important information:\n" + happening.getHappeningInfos("\t", "\n") + "\n\n"
				+ "best regards,\n" + hostName);

		/*
		 * hoedlale16: It is better to send mail to host and BCC to all guests, so the
		 * email addresses are hidden But mailtrap.io does not show the BCC Mails
		 * (neither in RAW, so we send it in TO and keep the code here for a possible
		 * productive solution.
		 */

		/*
		 if (host.geteMail() != null) 
		 	msg.(host.geteMail()); 
		 else { 
		 	model.addAttribute("message","HOST has no valid E-Mail: Not able to send reminder!"); return
		 	"forward:/showGuestListManagement"; 
		 }
		 */

		String[] guestMails = new String[guestList.size()];
		for (int i = 0; i < guestList.size(); i++) {
			String mail = guestList.get(i).geteMail();
			if (mail != null)
				guestMails[i] = mail;
		}
		// msg.setBcc(guestMails);
		msg.setTo(guestMails);

		try {
			this.mailSender.send(msg);
		} catch (MailAuthenticationException ex) {
			throw new IllegalStateException(
					"Configuration error for mail server detected. Please contact Administrator to setup correct connection settings for mail server.	");
		} catch (MailException ex) {
			throw new IllegalStateException("Error while sending mail!");
		}

		model.addAttribute("message", "Reminder send to guests");
		return "forward:/showGuestListManagement";
	}

	// -----------------------------------------------------------------------------------------

	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		ex.printStackTrace();
		return "error";
	}

	@ExceptionHandler()
	@ResponseStatus(code = HttpStatus.FORBIDDEN)
	public String handle403(Exception ex) {
		return "login";
	}

}
