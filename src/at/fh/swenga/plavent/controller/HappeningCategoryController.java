package at.fh.swenga.plavent.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.fh.swenga.plavent.model.Happening;
import at.fh.swenga.plavent.model.HappeningCategory;
import at.fh.swenga.plavent.model.HappeningTask;
import at.fh.swenga.plavent.model.User;
import at.fh.swenga.plavent.repo.HappeningCategoryRepository;

/**
 * @author Stefan Heider:
 *         
 * description of this controller
 *
 */



@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class HappeningCategoryController {

	@Autowired
	private HappeningCategoryRepository categoryDao;
	
	public HappeningCategoryController() {
		// TODO Auto-generated constructor stub
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

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping(value = { "showCategoryManagement" })
	public String showCategories(Model model) {	
		//Set attributes
		model.addAttribute("happeningCategories", categoryDao.findAll());
		return "categoryManagement";
	}
	
	@Secured({ "ROLE_ADMIN" })
	@RequestMapping(value = { "showCreateCategory" })
	public String showCreateCategories(Model model) {
		return "createModifyCategory";
	}
	
	@Secured({ "ROLE_ADMIN" })
	@RequestMapping(value = { "showModifyCategory" })
	public String showModifyCategories(Model model, @RequestParam(value = "categoryID") HappeningCategory category) {
		model.addAttribute("modifiedCategory", category);
		return "createModifyCategory";
	}
/*	
	public boolean isAdmin(Authentication authentication) {
		return (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
	}
*/	
	@Secured({ "ROLE_ADMIN" })
	@PostMapping("/createNewHappeningCategory")
	public String createNewHappeningCategory(Model model, @Valid HappeningCategory newCategory, @RequestParam(value = "categoryName") String categoryName,
			@RequestParam(value = "description") String description, Authentication authentication, BindingResult bindingResult) {
/*
		// Check if user is ADMIN
		if (!isAdmin(authentication)) {
			model.addAttribute("warningMessage", "No permission!");
			return "forward:/showHappeningManagement";
		}
*/
		// Any errors? -> Create a String out of all errors and return to the page
		if (errorsDetected(model, bindingResult)) {
			return showCategories(model);
		}

		newCategory.setCategoryName(categoryName);
		newCategory.setDescription(description);
		categoryDao.save(newCategory);

		return showCategories(model);
	}
	
	//TODO: Create methods for requests:
	//showCreateCategoryForm
	//filterCategories
	
	//deleteCategory ==> Just update 'enabled' flag, do not delete them!
	//reactivateCategory => 'Update enabled flag to actiate them again
	

	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		ex.printStackTrace();
		return "error";
	}
}
