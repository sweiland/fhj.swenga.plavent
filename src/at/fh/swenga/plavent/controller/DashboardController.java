package at.fh.swenga.plavent.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.fh.swenga.plavent.model.Happening;
import at.fh.swenga.plavent.model.User;
import at.fh.swenga.plavent.repo.HappeningGuestlistRepository;
import at.fh.swenga.plavent.repo.HappeningRepository;
import at.fh.swenga.plavent.repo.HappeningTaskRepository;
import at.fh.swenga.plavent.repo.UserRepository;

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class DashboardController {


	@Autowired
	private HappeningRepository happeningRepository;

	@Autowired
	private HappeningTaskRepository happeningTaskRepository;

	@Autowired
	private HappeningGuestlistRepository happeningGuestlistRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private MailSender mailSender;

	public DashboardController() {
		// TODO Auto-generated constructor stub
	}

	@Secured({ "ROLE_GUEST" })
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
			model.addAttribute("allHappenings", happeningRepository.findAll());
		}

		return "dashboard";
	}
	
	public Map<String, ArrayList<Object>> numOfGuests(List<Happening> happenings) {
		Map<String, ArrayList<Object>> guestNums = new HashMap<String, ArrayList<Object>>();
		for (int i = 0; i < happenings.size(); i++) {
			ArrayList<Object> info = new ArrayList<>();
			info.add(happeningGuestlistRepository.getGuestList(happenings.get(i).getHappeningId()).size());
			info.add(happeningTaskRepository.findByHappeningHappeningId(happenings.get(i).getHappeningId()).size());
			info.add(happenings.get(i).getStart());
			info.add(happenings.get(i).getEnd());
			guestNums.put(happenings.get(i).getHappeningName(), info);
		}
		return guestNums;
	}

	/**
	 * Return a list of happenings where given user is a guest and the happenings
	 * start in the future
	 * 
	 * @param username
	 * @return
	 */
	private List<Happening> getHappeningForGuestInFuture(String username) {
		List<Happening> happenings = happeningRepository.getHappeningForGuestInFuture(username);
		return happenings;
	}
	
	@Secured({ "ROLE_GUEST" })
	@PostMapping("/sendMailInviteMe")
	public String sendMailInviteMe(Model model, @RequestParam(value = "happeningID") Happening happening,
			Authentication authentication) {

		// Check if user is Owner of Happening or has role ADMIN
		if(happening == null) {
			model.addAttribute("warningMessage","Happening not found!");
			return showDashboard(model, authentication);
		}
			
		User guest = userRepository.findFirstByUsername(authentication.getName());
		
		User host = happening.getHappeningHost();
		String hostName = host.getFirstname() + " " + host.getLastname();

		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setSubject("Invite me to Happening " + happening.getHappeningName());
		msg.setFrom(guest.geteMail());
		msg.setTo(host.geteMail());

		msg.setText("Dear "+hostName+"\n\n" + "Can you please invite me to your happening (" + happening.getHappeningName() +")?.\n"
				+ "Thank you very much!\n\n"
				+ "best regards,\n" + guest.getFirstname() + " " + guest.getLastname());

		try {
			this.mailSender.send(msg);
		} catch (MailAuthenticationException ex) {
			throw new IllegalStateException(
					"Configuration error for mail server detected. Please contact Administrator to setup correct connection settings for mail server.	");
		} catch (MailException ex) {
			throw new IllegalStateException("Error while sending mail!");
		}

		model.addAttribute("message", "Invitation send!");
		return "forward:/dashboard";
	}
	
	
}
