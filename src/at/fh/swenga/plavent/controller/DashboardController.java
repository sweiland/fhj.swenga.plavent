package at.fh.swenga.plavent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardController {
		
		public DashboardController() {
			// TODO Auto-generated constructor stub
		}
		
		@RequestMapping(value = { "/" })
		public String showLoginPage(Model model) {

			/*
			 * hoedlale16: If user already set(logged in, show dashboard page 
			 * 	otherwise show login page*/
			//if (currLoggedInUser != null) {
			//	return "dashboard";
			//} else {
				// Show loginpage
				return "login";
			//}
		}
		
		@RequestMapping(value = { "dashboard"})
		public String showDashboard(Model model) {
				//Show main screen (dashboard)
				return "dashboard";
		}
}
