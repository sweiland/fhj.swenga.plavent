package at.fh.swenga.plavent.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.fh.swenga.plavent.model.HappeningCategory;
import at.fh.swenga.plavent.repo.HappeningCategoryRepository;

/**
 * @author Stefan Heider:
 * 
 *         Handling to create,modify od disable categories
 *
 */

@Controller
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class HappeningCategoryController {

	@Autowired
	private HappeningCategoryRepository categoryDao;

	public HappeningCategoryController() {
	}
	
	/**
	 * Helper method to include the paging handling. The content is static, so user
	 * can just change the pagenumber
	 * 
	 * @param pageNr
	 *            .. Page number which should be displayed
	 * @return
	 */
	private PageRequest generatePageRequest(int pageNr) {
		return PageRequest.of(pageNr, 6);
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
	public String showCategoryManagement(Model model) {
		// Set attributes
		PageRequest page = generatePageRequest(0);
		Page<HappeningCategory> categoryPage = categoryDao.findAll(page);
		
		model.addAttribute("happeningCategories", categoryPage );
		model.addAttribute("usedCategories", categoryDao.getUsedCategories());
		model.addAttribute("currPage", categoryPage.getNumber());
		model.addAttribute("totalPages", categoryPage.getTotalPages());
		return "categoryManagement";
	}
	
	@Secured({ "ROLE_ADMIN" })
	@RequestMapping(value = { "showCategoryManagementPage" })
	public String showCategoryManagementPage(Model model,@RequestParam(value = "page") int pageNr) {
		
		PageRequest page = generatePageRequest(pageNr);
		Page<HappeningCategory> categoryPage = categoryDao.findAll(page);
		
		// Set attributes
		model.addAttribute("happeningCategories", categoryPage);
		model.addAttribute("usedCategories", categoryDao.getUsedCategories());
		model.addAttribute("currPage", categoryPage.getNumber());
		model.addAttribute("totalPages", categoryPage.getTotalPages());
		return "categoryManagement";
	}

	@Secured({ "ROLE_ADMIN" })
	@PostMapping("/filterCategories")
	public String filterCategories(Model model, @RequestParam String searchString) {

		PageRequest page = generatePageRequest(0);
		Page<HappeningCategory> happeningCategoryPage;

		happeningCategoryPage = categoryDao.filterHappeningCategories(searchString, page);
		
		model.addAttribute("happeningCategories", happeningCategoryPage);
		model.addAttribute("usedCategories", categoryDao.getUsedCategories());
		model.addAttribute("currPage", happeningCategoryPage.getNumber());
		model.addAttribute("totalPages", happeningCategoryPage.getTotalPages());
		return "categoryManagement";
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping(value = { "showCreateCategory" })
	public String showCreateCategory(Model model) {
		return "createModifyCategory";
	}

	@Secured({ "ROLE_ADMIN" })
	@RequestMapping(value = { "showModifyCategory" })
	public String showModifyCategory(Model model, @RequestParam(value = "categoryID") HappeningCategory category) {
		model.addAttribute("happeningCategory", category);
		return "createModifyCategory";
	}

	// Creating a NEW CATEGROY
	@Secured({ "ROLE_ADMIN" })
	@PostMapping("/createNewHappeningCategory")
	public String createNewHappeningCategory(Model model, @Valid HappeningCategory newCategory,
			@RequestParam(value = "categoryName") String categoryName,
			@RequestParam(value = "description") String description, Authentication authentication,
			BindingResult bindingResult) {

		// Any errors? -> Create a String out of all errors and return to the page
		if (errorsDetected(model, bindingResult)) {
			return showCategoryManagement(model);
		}

		newCategory.setCategoryName(categoryName);
		newCategory.setDescription(description);
		newCategory.setEnabled(true);
		categoryDao.save(newCategory);

		return showCategoryManagement(model);
	}

	// Modifying an EXISTING CATEGORY
	@Secured({ "ROLE_HOST" })
	@PostMapping("/modifyExistingHappeningCategory")
	public String modifyExistingHappeningCategory(Model model, @Valid HappeningCategory modifiedCategory,
			Authentication authentication, BindingResult bindingResult) {

		// Any errors? -> Create a String out of all errors and return to the page
		if (errorsDetected(model, bindingResult)) {
			return showCategoryManagement(model);
		}

		HappeningCategory category = categoryDao.findFirstByCategoryID(modifiedCategory.getCategoryID());
		if (category != null) {
			category.setCategoryName(modifiedCategory.getCategoryName());
			category.setDescription(modifiedCategory.getDescription());

			categoryDao.save(category);

			return showCategoryManagement(model);
		} else {
			model.addAttribute("warningMessage", "Category not found!");
			return showCategoryManagement(model);
		}
	}

	// Disable and used happening category
	@Secured({ "ROLE_HOST" })
	@RequestMapping("/disableUsedHappeningCategory")
	public String disableUsedHappeningCategory(Model model,
			@RequestParam(value = "categoryID") HappeningCategory category) {

		//Validation checks
		if("Unassigned".equals(category.getCategoryName())) {
			model.addAttribute("You cannot delete the Unassigned Category!");
			return showCategoryManagement(model);
		}

		category.setEnabled(false);
		categoryDao.save(category);

		model.addAttribute("message", "Category" + category.getCategoryName() + "disabled");
		return showCategoryManagement(model);
	}

	// Disable and used happening category
	@Secured({ "ROLE_HOST" })
	@RequestMapping("/reactivateDisabledHappeningCategory")
	public String reactivateDisabledHappeningCategory(Model model,
			@RequestParam(value = "categoryID") HappeningCategory category) {

		category.setEnabled(true);
		categoryDao.save(category);

		model.addAttribute("message", "Category" + category.getCategoryName() + "enabled");
		return showCategoryManagement(model);
	}

	// Disable and used happening category
	@Secured({ "ROLE_HOST" })
	@RequestMapping("/deleteUnusedHappeningCategory")
	public String deleteUnusedHappeningCategory(Model model,
			@RequestParam(value = "categoryID") HappeningCategory category) {

		// Validation checks
		if("Unassigned".equals(category.getCategoryName())) {
			model.addAttribute("You cannot delete the Unassigned Category!");
			return showCategoryManagement(model);
		}
		
		List<HappeningCategory> usedCategories = categoryDao.getUsedCategories();
		if (usedCategories.contains(category)) {
			model.addAttribute("errorMessage", "You can not delete a used cateogry!");
			return showCategoryManagement(model);
		}

		categoryDao.delete(category);
		model.addAttribute("message", "Category" + category.getCategoryName() + "deleted");
		return showCategoryManagement(model);
	}

	@ExceptionHandler(Exception.class)
	public String handleAllException(Exception ex) {
		ex.printStackTrace();
		return "error";
	}
}
