package at.fh.swenga.plavent.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import at.fh.swenga.plavent.repo.HappeningGuestlistRepository;
import at.fh.swenga.plavent.repo.HappeningRepository;
import at.fh.swenga.plavent.repo.HappeningTaskRepository;
import at.fh.swenga.plavent.repo.UserRepository;

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class DashboardController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HappeningRepository happeningRepository;

	@Autowired
	private HappeningTaskRepository happeningTaskRepository;

	@Autowired
	private HappeningGuestlistRepository happeningGuestlistRepository;

	public DashboardController() {
		// TODO Auto-generated constructor stub
	}

	@Secured({ "ROLE_GUEST" })
	@RequestMapping(value = { "dashboard" })
	public String showDashboard(Model model, Authentication authentication) {
		model.addAttribute("happeningsForGuestInFuture", this.getHappeningForGuestInFuture(authentication.getName()));
		model.addAttribute("assignedTasksNum", this.getNumberOfAssignedTasksForGuest(authentication.getName()));
		model.addAttribute("happeningInFutureNotGuest",
				this.getHappeningInFutureWhereGuestNotInvited(authentication.getName()));
		model.addAttribute("assignedTasks",
				happeningTaskRepository.getAllAssignedTasksForUser(authentication.getName()));
		model.addAttribute("numOfHappenings",
				this.numOfHappenings(this.getHappeningForGuestInFuture(authentication.getName())));

		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HOST"))) {
			// model.addAttribute("activeHappeningsHost",
			// this.getAllActiveHappeningsHost(authentication.getName()));
			model.addAttribute("numOfHosted",
					this.numOfHappeningsHosted(this.getAllActiveHappeningsHost(authentication.getName())));
			model.addAttribute("numOfGuests",
					this.numOfGuests(this.getAllActiveHappeningsHost(authentication.getName())));
		}

		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			// TODO: load stuff for admin hawara
		}

		return "dashboard";
	}

	public Map<String, ArrayList<Integer>> numOfGuests(List<Happening> happenings) {
		Map<String, ArrayList<Integer>> guestNums = new HashMap<String, ArrayList<Integer>>();
		for (int i = 0; i < happenings.size(); i++) {
			ArrayList<Integer> info = new ArrayList<Integer>();
			info.add(happeningGuestlistRepository.getGuestList(happenings.get(i).getHappeningId()).size());
			info.add(happeningTaskRepository.findByHappeningHappeningId(happenings.get(i).getHappeningId()).size());
			guestNums.put(happenings.get(i).getHappeningName(), info);
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
		int taskNum = happeningTaskRepository.getNumOfAssignedTasksForUser(username);
		return taskNum;
	}

	/**
	 * Return a list of happenings where given user is a guest and the happenings
	 * start in the future
	 * 
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
