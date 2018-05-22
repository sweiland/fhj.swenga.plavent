package at.fh.swenga.plavent.controller;

import javax.validation.Valid;

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
import at.fh.swenga.plavent.repo.HappeningTaskRepository;
import at.fh.swenga.plavent.repo.UserRepository;

/**
 * @author Alexander Hoedl:
 * 
 *         Controller to handle Tasks for a specific Happening - Create new Task
 *         - Modify existing Task - Delete Task
 */

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class HappeningTaskController {

	@Autowired
	HappeningTaskRepository happeningTaskRepo;

	@Autowired
	UserRepository userRepo;

	public HappeningTaskController() {
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
		return PageRequest.of(pageNr, 10);
	}

	// -----------------------------------------------------------------------------------------
	// --- TASKLIST MANAGEMENT ---
	// -----------------------------------------------------------------------------------------
	@Secured({ "ROLE_HOST" })
	@RequestMapping(value = { "showTaskListManagement" })
	public String showTaskListManagement(Model model, @RequestParam(value = "happeningID") Happening happening,
			Authentication authentication) {

		// Check if happening is DELETED or current logged in user is Owner of Happening
		// or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)
				|| "DELETED".equals(happening.getHappeningStatus().getStatusName())) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// Show first page with max 10 elements...
		PageRequest page = generatePageRequest(0);
		Page<HappeningTask> happeningTasksPage = happeningTaskRepo
				.findByHappeningHappeningId(happening.getHappeningId(), page);

		model.addAttribute("happening", happening);
		model.addAttribute("happeningTasks", happeningTasksPage);

		model.addAttribute("currPage", happeningTasksPage.getNumber());
		model.addAttribute("totalPages", happeningTasksPage.getTotalPages());
		return "happeningTaskManagement";

	}

	@Secured({ "ROLE_HOST" })
	@RequestMapping(value = { "showTaskListManagementPage" })
	public String showTaskListManagementPage(Model model, @RequestParam(value = "page") int pageNr,
			@RequestParam(value = "happeningID") Happening happening, Authentication authentication) {

		// Check if happening is DELETED or current logged in user is Owner of Happening
		// or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)
				|| "DELETED".equals(happening.getHappeningStatus().getStatusName())) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// Show first page with max 10 elements...
		PageRequest page = generatePageRequest(pageNr);
		Page<HappeningTask> happeningTasksPage = happeningTaskRepo
				.findByHappeningHappeningId(happening.getHappeningId(), page);

		model.addAttribute("happening", happening);
		model.addAttribute("happeningTasks", happeningTasksPage);

		model.addAttribute("currPage", happeningTasksPage.getNumber());
		model.addAttribute("totalPages", happeningTasksPage.getTotalPages());
		return "happeningTaskManagement";

	}

	@Secured({ "ROLE_HOST" })
	@RequestMapping(value = { "showCreateHappeningTaskForm" })
	public String showCreateHappeningTaskForm(Model model, @RequestParam(value = "happeningID") Happening happening,
			Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		model.addAttribute("happening", happening);
		return "createModifyHappeningTask";
	}

	@Secured({ "ROLE_HOST" })
	@PostMapping("/createNewHappeningTask")
	public String createNewHappeningTask(Model model, @RequestParam(value = "happeningID") Happening happening,
			@RequestParam(value = "responsibleUsername") String username, @Valid HappeningTask newTask,
			Authentication authentication, BindingResult bindingResult) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// Any errors? -> Create a String out of all errors and return to the page
		if (errorsDetected(model, bindingResult))
			return showTaskListManagement(model, happening, authentication);

		newTask.setHappening(happening);
		happening.addHappeningTask(newTask);
		happeningTaskRepo.save(newTask);

		// Assign/Link Task with user and happening
		User responsibleUser = userRepo.findFirstByUsername(username);
		if (responsibleUser != null) {
			newTask.setResponsibleUser(responsibleUser);
			happeningTaskRepo.save(newTask);
		}

		return showTaskListManagement(model, happening, authentication);
	}

	// Filter GuestListManagement page
	@Secured({ "ROLE_HOST" })
	@PostMapping("/filterHappeningTaskList")
	public String filterHappeingTaskList(Model model, @RequestParam(value = "happeningID") Happening happening,
			Authentication authentication, @RequestParam String searchString) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}
		
		PageRequest page = generatePageRequest(0);
		Page<HappeningTask> happeningTasksPage = happeningTaskRepo
				.getFilteredTasks(happening.getHappeningId(),searchString, page);

		model.addAttribute("happening", happening);
		model.addAttribute("happeningTasks", happeningTasksPage);

		model.addAttribute("currPage", happeningTasksPage.getNumber());
		model.addAttribute("totalPages", happeningTasksPage.getTotalPages());
		return "happeningTaskManagement";

	}

	@Secured({ "ROLE_HOST" })
	@RequestMapping(value = { "showModifyHappeningTaskForm" })
	public String showModifyHappeningTaskForm(Model model, @RequestParam(value = "taskId") HappeningTask task,
			Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(task.getHappening(), authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		model.addAttribute("happening", task.getHappening());
		model.addAttribute("happeningTask", task);
		return "createModifyHappeningTask";

	}

	@Secured({ "ROLE_HOST" })
	@PostMapping("/modifyExistingHappeningTask")
	public String modifyExistingHappeningTask(Model model, @Valid HappeningTask modifiedTask,
			@RequestParam(value = "happeningID") Happening happening,
			@RequestParam(value = "responsibleUsername") String username, Authentication authentication,
			BindingResult bindingResult) {

		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(happening, authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		// Any errors? -> Create a String out of all errors and return to the page
		if (errorsDetected(model, bindingResult))
			return showTaskListManagement(model, happening, authentication);

		HappeningTask task = happeningTaskRepo.findByTaskIdAndHappeningHappeningId(modifiedTask.getTaskId(),
				happening.getHappeningId());
		if (task != null) {
			task.setTopic(modifiedTask.getTopic());
			task.setDurationInHour(modifiedTask.getDurationInHour());
			task.setDescription(modifiedTask.getDescription());
			// Assign/Link Task with user and happening
			User responsibleUser = userRepo.findFirstByUsername(username);
			if (responsibleUser != null) {
				task.setResponsibleUser(responsibleUser);
			}
			happeningTaskRepo.saveAndFlush(task);
			happening.removeHappeningTaskFromList(modifiedTask);
			happening.addHappeningTask(task);

			return showTaskListManagement(model, happening, authentication);
		} else {
			model.addAttribute("warningMessage", "Task not found!");
			return showTaskListManagement(model, happening, authentication);
		}
	}

	@Secured({ "ROLE_HOST" })
	@GetMapping("/deleteExistingTask")
	public String deleteExistingTask(Model model, @RequestParam(value = "taskId") HappeningTask task,
			Authentication authentication) {
		// Check if user is Owner of Happening or has role ADMIN
		if (!isHappeningHostOrAdmin(task.getHappening(), authentication)) {
			model.addAttribute("warningMessage", "Happening not found or no permission!");
			return "forward:/showHappeningManagement";
		}

		task.getHappening().removeHappeningTaskFromList(task);
		happeningTaskRepo.delete(task);
		return showTaskListManagement(model, task.getHappening(), authentication);

	}
	// -----------------------------------------------------------------------------------------

	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		ex.printStackTrace();
		return "error";
	}
}
