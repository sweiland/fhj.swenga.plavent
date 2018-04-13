package at.fh.swenga.plavent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.fh.swenga.plavent.model.UserManager;
import at.fh.swenga.plavent.model.UserModel;

@Controller
public class UserManagementController {
	
		@Autowired
		private UserManager userManager;
		
		public UserManagementController() {
			// TODO Auto-generated constructor stub
		}
		
		/**
		 * General function to verify if user is logged in
		 * @param model
		 * @return
		 */
		private boolean isLoggedIn(Model model) {
			if (model.containsAttribute("user")) {
				return true;
			} else {
				model.addAttribute("warningMessage", "Currently not logged in!");
				return false;
			}
		}
		
		@RequestMapping(value = { "/"})
		public String showLoginPage(Model model) {
			
			/*hoedlale16: If user already set(logged in, show dashboard page
			 * otherwise show login page with a warning message
			 */
			if (model.containsAttribute("user")) {
				return "dashboard";
			} else {
				//Show loginpage
				return "login";
			}
		}
		
		@RequestMapping(value = { "verifyLogin" })
		public String verifyLoginData(Model model, @RequestParam String username, @RequestParam String password) {
			//TODO: Check if enters user/pw matches to criterias and so on...
			
			UserModel user = userManager.verifyLogin(username, password);
			if (user == null) {
				model.addAttribute("errorMessage", "Username or password incorrect!");
				return "login";
			} else {
				model.addAttribute("user",user);
				model.addAttribute("message","Welcome "+user.getFirstname() + "!");
				return "dashboard";
			}
		}

		@RequestMapping(value = { "listUsers" })
		public String showAllUsers(Model model) {
			//hoedlale16: Verify that user is logged in
			if ( ! isLoggedIn(model)) {
				return "login";
			}
			
			model.addAttribute("users", userManager.getAllUsers());
			return "listUsers";
		}
	
		
		@PostMapping("/searchUsers")
		public String search(Model model, @RequestParam String searchString) {
			model.addAttribute("users", userManager.getFilteredUsers(searchString));
			return "listUsers";
		}
		

		@GetMapping("/createNewUser")
		public String showNewUserForm(Model model) {
			//TODO: redirect to a form to create a new user...
			return "login";
		}
}
