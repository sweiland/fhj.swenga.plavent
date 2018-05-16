package at.fh.swenga.plavent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;

import at.fh.swenga.plavent.dao.HappeningCategoryDao;
import at.fh.swenga.plavent.model.User;

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class CategoryManagementController {

	@Autowired
	private HappeningCategoryDao categoryDao;
	
	public CategoryManagementController() {
		// TODO Auto-generated constructor stub
	}
	
	private boolean isLoggedInAndHasPermission(Model model) {
		// hoedlale16: Verify that user is logged in
		if (!UserManagementController.isLoggedIn(model)) {
			return false;
		} else {
			// User logged in - check if he has the permission for happening management
			User currLoggedInUser = UserManagementController.getCurrentLoggedInUser();
			return currLoggedInUser.getRole().isPermissionCategoryMgmt();
		}
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
		if (!isLoggedInAndHasPermission(model)) {
			return "login";
		}
		
		//Set attributes
		model.addAttribute("happeningCategories", categoryDao.findAll());
		return "categoryManagement";
	}
	
	
	//showCreateCategoryForm
	//filterCategories
	

}
