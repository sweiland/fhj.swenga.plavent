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
import at.fh.swenga.plavent.repo.HappeningGuestlistRepository;
import at.fh.swenga.plavent.repo.HappeningRepository;
import at.fh.swenga.plavent.repo.HappeningTaskRepository;

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class DashboardController {

	
	@Autowired
	private HappeningRepository happeningRepository;
	
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
		List<Happening> happeningsForGuest =  this.getHappeningForGuestInFuture(authentication.getName());
		
		//URD 1.1.1.17 Guest see list of happenings where user is guest orderd by start date and start in the future
		model.addAttribute("happeningsForGuestInFuture",happeningsForGuest);
		model.addAttribute("assignedTasksNum",  happeningTaskRepository.getNumOfAssignedTasksForUser(authentication.getName()));
		model.addAttribute("happeningInFutureNotGuest", happeningRepository.getHappeningInFutureWhereGuestNotInvited(authentication.getName()));
		model.addAttribute("assignedTasks", happeningTaskRepository.getAllAssignedTasksForUser(authentication.getName()));
		model.addAttribute("numOfHappenings", happeningsForGuest.size());
		
		
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HOST"))) {
			List<Happening> happeningsAsHost = happeningRepository.getAllActiveHappeningsHost(authentication.getName());
			
			model.addAttribute("activeHappeningsHost", happeningsAsHost);
			model.addAttribute("numOfHosted", happeningsAsHost.size());
			model.addAttribute("numOfGuests",this.numOfGuests(happeningsAsHost));
		}
		
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			//TODO: load stuff for admin hawara
		}
		
		
		return "dashboard";
	}
	
	//TODO: Implement meaningfully
	public List<Integer> numOfGuests(List<Happening> happenings) {
		List<Integer> guestNums = new ArrayList<>();
		int i = 0;
		for (Happening h: happenings) {
			guestNums.add(i, happeningGuestlistRepository.getGuestListSize(h.getHappeningId()));
			i++;					
		}
		return guestNums;
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
	
}

