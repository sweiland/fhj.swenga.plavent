package at.fh.swenga.plavent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashboardController {
		
		public DashboardController() {
			// TODO Auto-generated constructor stub
		}
		
		@RequestMapping(value = { "dashboard"})
		public String showLoginPage(Model model) {
				//Show main screen (dashboard)
				return "dashboard";
		}
}
