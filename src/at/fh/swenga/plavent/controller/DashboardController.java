package at.fh.swenga.plavent.controller;

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
import at.fh.swenga.plavent.repo.HappeningCategoryRepository;
import at.fh.swenga.plavent.repo.HappeningRepository;
import at.fh.swenga.plavent.repo.HappeningStatusRepository;
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

	public DashboardController() {
		// TODO Auto-generated constructor stub
	}	

	@Secured({ "ROLE_GUEST"})
	@RequestMapping(value = { "dashboard" })
	public String showDashboard(Model model,Authentication authentication) {
		
		//TODO: Show stuff for logged in user	
		
		//URD 1.1.1.17 Guest see list of happenings where user is guest orderd by start date and start in the future
		model.addAttribute("happeningsForGuestInFuture", this.getHappeningForGuestInFuture(authentication.getName()));
		
		
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_HOST"))) {
			//TODO: load stuff for host havera
		}
		
		if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
			//TODO: load stuff for admin havera
		}
		
		
		return "dashboard";
	}
	
	
	/**
	 * Return a list of happenings where given user is a guest and the happenings start in the future
	 * @param username
	 * @return
	 */
	public List<Happening> getHappeningForGuestInFuture(String username) {
		List<Happening> happenings = happeningRepository.getTop3HappeningForGuestInFuture(username);
		return happenings;
		
	}
}

