package net.greensnet.web.admin;

import net.greensnet.domain.FeaturedItem;
import net.greensnet.service.FeaturedItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class FeaturedItemController {

	private static final String FEATURED_ITEM_LIST_PAGE = "th_admin/featureditems/list";
	private static final String FEATURED_ITEM_PAGE = "th_admin/featureditems/edit";

	private static final String FEATURED_ITEMS = "items";
	private static final String FEATURED_ITEM = "item";

	private final FeaturedItemService featuredItemService;

	@Autowired
	public FeaturedItemController(FeaturedItemService featuredItemService) {
		this.featuredItemService = featuredItemService;
	}

	@GetMapping("/AdminFeaturedItems")
	public String getAllItems(Model model) {
		List<FeaturedItem> items = featuredItemService.getAllFeaturedItems();

		model.addAttribute(FEATURED_ITEMS, items);

		return FEATURED_ITEM_LIST_PAGE;
	}

	@PostMapping("/AdminFeaturedItems")
	public String addFeaturedItem(Model model,
						   @RequestParam(value="priority") int priority,
						   @RequestParam(value="targetUrl") String targetUrl,
						   @RequestParam(value="altText") String altText,
						   @RequestParam(value="image") MultipartFile image) {
		featuredItemService.createFeaturedItem(image, priority, targetUrl, altText, true);

		return getAllItems(model);
	}

	@PostMapping(path="/AdminFeaturedItems/{id}")
	public String getItemForEdit(Model model,
								 @PathVariable(value = "id") long id) {
		model.addAttribute(FEATURED_ITEM, featuredItemService.getItem(id));

		return FEATURED_ITEM_PAGE;
	}

	@PostMapping(path="/AdminFeaturedItems/{id}/Deactivate")
	public String deactivateItem(Model model,
								 @PathVariable(value = "id") long id) {
		featuredItemService.deactivateItem(id);

		return getAllItems(model);
	}

	@PostMapping("/AdminFeaturedItems/{id}/Activate")
	public String activateItem(Model model,
							   @PathVariable(value = "id") long id) {
		featuredItemService.activateItem(id);

		return getAllItems(model);
	}

	@PostMapping("/AdminFeaturedItems/{id}/Edit")
	public String editItem(Model model,
						   @PathVariable(value = "id") long id,
						   @RequestParam(value="priority") int priority,
						   @RequestParam(value="targetUrl") String targetUrl,
						   @RequestParam(value="altText") String altText) {
		featuredItemService.editFeaturedItem(id, priority, targetUrl, altText);

		return getAllItems(model);
	}


	@PostMapping("/AdminFeaturedItems/{id}/UpdateImage")
	public String updateImage(Model model,
							  @PathVariable(value = "id") long id,
							  @RequestParam(value = "image") MultipartFile image) {
		featuredItemService.updateImage(id, image);

		return getAllItems(model);
	}
}
