package at.fh.swenga.plavent.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import at.fh.swenga.plavent.model.Happening;
import at.fh.swenga.plavent.model.HappeningTask;
import at.fh.swenga.plavent.model.User;
import at.fh.swenga.plavent.repo.HappeningCategoryRepository;
import at.fh.swenga.plavent.repo.HappeningGuestlistRepository;
import at.fh.swenga.plavent.repo.HappeningRepository;
import at.fh.swenga.plavent.repo.HappeningStatusRepository;
import at.fh.swenga.plavent.repo.HappeningTaskRepository;
import at.fh.swenga.plavent.repo.UserRepository;
import at.fh.swenga.plavent.repo.UserRoleRepository;

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class DashboardController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private HappeningRepository happeningRepository;
	
	@Autowired
	private HappeningStatusRepository happeningStatusRepository;

	@Autowired
	private HappeningCategoryRepository happeningCategoryRepository;
	
	@Autowired
	private HappeningTaskRepository happeningTaskRepository;

	@Autowired
	private HappeningGuestlistRepository happeningGuestlistRepository;
	
	public DashboardController() {
		// TODO Auto-generated constructor stub
	}	

	@Secured({ "ROLE_GUEST"})
	@RequestMapping(value = { "dashboard" })
	public String showDashboard(Model model,Authentication authentication) {
		
		//TODO: Show stuff for logged in user	
		
		//URD 1.1.1.17 Guest see list of happenings where user is guest orderd by start date and start in the future
		model.addAttribute("happeningsForGuestInFuture", this.getHappeningForGuestInFuture(authentication.getName()));
		model.addAttribute("assignedTasksNum", this.getNumberOfAssignedTasksForGuest(authentication.getName()));
		model.addAttribute("happeningInFutureNotGuest", this.getHappeningInFutureWhereGuestNotInvited(authentication.getName()));
		model.addAttribute("assignedTasks", this.getAllAssignedTasksForGuest(authentication.getName()));
		model.addAttribute("numOfHappenings", this.numOfHappenings(this.getHappeningForGuestInFuture(authentication.getName())));
		
		
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HOST"))) {
			model.addAttribute("activeHappeningsHost", this.getAllActiveHappeningsHost(authentication.getName()));
			model.addAttribute("numOfHosted", this.numOfHappeningsHosted(this.getAllActiveHappeningsHost(authentication.getName())));
			model.addAttribute("numOfGuests",this.numOfGuests(this.getAllActiveHappeningsHost(authentication.getName())));
		}
		
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			//TODO: load stuff for admin hawara
		}
		
		
		return "dashboard";
	}
	
	//TODO: Implement meaningfully
	public List<Integer> numOfGuests(List<Happening> happenings) {
		List<Integer> guestNums = new ArrayList<>();
		for (int i = 0; i<happenings.size();i++) {
		guestNums.set(i, happeningGuestlistRepository.getGuestList(happenings.get(i).getHappeningId()).size());
		}
		return guestNums;
	}

	public int numOfHappenings(List<Happening> happenings) {
		return happenings.size();
	}

	public List<Happening> getAllActiveHappeningsHost(String name) {
		List<Happening> happenings = happeningRepository.getAllActiveHappeningsHost(name);
		return happenings;
	}

	public int getNumberOfAssignedTasksForGuest(String username) {
		User user = userRepository.findFirstByUsername(username);
		int taskNum = happeningTaskRepository.getNumOfAssignedTasksForUser(user);
		return taskNum;
	}
	
	public List<HappeningTask> getAllAssignedTasksForGuest(String username) {
		User user = userRepository.findFirstByUsername(username);
		List<HappeningTask> tasks = happeningTaskRepository.getAllAssignedTasksForUser(user);
		return tasks;
	}

	/**
	 * Return a list of happenings where given user is a guest and the happenings start in the future
	 * @param username
	 * @return
	 */
	public List<Happening> getHappeningForGuestInFuture(String username) {
		List<Happening> happenings = happeningRepository.getHappeningForGuestInFuture(username);
		return happenings;	
	}
	
	public List<Happening> getHappeningInFutureWhereGuestNotInvited(String username) {
		List<Happening> happenings = happeningRepository.getHappeningInFutureWhereGuestNotInvited(username);
		return happenings;
	}
	
	public int numOfHappeningsHosted(List<Happening> happenings) {
		return happenings.size();
	}
}

