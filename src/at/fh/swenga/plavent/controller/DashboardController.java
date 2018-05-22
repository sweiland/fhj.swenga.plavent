package at.fh.swenga.plavent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import at.fh.swenga.plavent.repo.HappeningCategoryRepository;
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
	private HappeningStatusRepository happeningStatusRepository;

	@Autowired
	private HappeningCategoryRepository happeningCategoryRepository;

	public DashboardController() {
		// TODO Auto-generated constructor stub
	}	

	@Secured({ "ROLE_GUEST"})
	@RequestMapping(value = { "dashboard" })
	public String showDashboard(Model model) {
		
		//TODO: Show stuff for logged in user
		// Show main screen (dashboard)
		return "dashboard";
	}
}

