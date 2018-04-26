package at.fh.swenga.plavent.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class CategoryManagementController {

	public CategoryManagementController() {
		// TODO Auto-generated constructor stub
	}
	
	private boolean isLoggedIn(Model model) {
		// TODO: Implement and check if user is logged in...
		// TODO: Check if user has the permission to have access here!
		return true;

	}

	private boolean errorsDetected(Model model, BindingResult bindingResult) {
		// Any errors? -> Create a String out of all errors and return to the page
		if (bindingResult.hasErrors()) {
			String errorMessage = "";
			for (FieldError fieldError : bindingResult.getFieldErrors()) {
				errorMessage += fieldError.getField() + " is invalid";
			}
			model.addAttribute("errorMessage", errorMessage);
			return true;
		}
		return false;
	}

	@RequestMapping(value = { "showCategoryManagement" })
	public String showCategories(Model model) {
		// hoedlale16: Verify that user is logged in
		if (!isLoggedIn(model)) {
			return "login";
		}

		// TODO: set attributes
		// model.addAttribute("currLoggedInuser", currLoggedInUser);
		// model.addAttribute("categories", userManager.getAllUsers());
		model.addAttribute("warningMessage", "Not implemented <" + "showCategories" + ">");
		return "categoryManagement";
	}
	
	
	//showCreateCategoryForm
	//filterCategories
	

}
