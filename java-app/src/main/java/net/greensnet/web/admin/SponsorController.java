package net.greensnet.web.admin;

import net.greensnet.domain.Sponsor;
import net.greensnet.service.SponsorService;
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
public class SponsorController {

	private static final String SPONSOR_LIST_PAGE = "th_admin/sponsors/list";
	private static final String SPONSOR_PAGE = "th_admin/sponsors/edit";

	private static final String SPONSORS = "sponsors";
	private static final String SPONSOR = "sponsor";

	private final SponsorService sponsorService;

	@Autowired
	public SponsorController(SponsorService sponsorService) {
		this.sponsorService = sponsorService;
	}

	@GetMapping("AdminSponsors")
	public String showSponsors(Model model) {
		List<Sponsor> sponsors = sponsorService.getAllMainSponsors();

		model.addAttribute(SPONSORS, sponsors);

		return SPONSOR_LIST_PAGE;
	}

	@PostMapping("AdminSponsors")
	public String addSponsor(Model model,
							 @RequestParam(value="name") String name,
							 @RequestParam(value="websiteUrl", defaultValue="-1") String websiteUrl,
							 @RequestParam(value="altText", defaultValue="-1") String altText,
							 @RequestParam(value="logo") MultipartFile logo) {
		sponsorService.createMainSponsor(logo, name, websiteUrl, altText, true);

		return showSponsors(model);
	}

	@PostMapping(path="/AdminSponsors/{id}")
	public String getItemForEdit(Model model,
								 @PathVariable(value = "id") long id) {
		model.addAttribute(SPONSOR, sponsorService.getSponsor(id));

		return SPONSOR_PAGE;
	}

	@PostMapping(path="/AdminSponsors/{id}/Deactivate")
	public String deactivateItem(Model model,
								 @PathVariable(value = "id") long id) {
		sponsorService.deactivateSponsor(id);

		return showSponsors(model);
	}

	@PostMapping("/AdminSponsors/{id}/Activate")
	public String activateItem(Model model,
							   @PathVariable(value = "id") long id) {
		sponsorService.activateSponsor(id);

		return showSponsors(model);
	}

	@PostMapping("/AdminSponsors/{id}/Edit")
	public String editItem(Model model,
						   @PathVariable(value = "id") long id,
						   @RequestParam(value="name") String name,
						   @RequestParam(value="websiteUrl") String websiteUrl,
						   @RequestParam(value="altText") String altText) {
		sponsorService.editMainSponsor(id, name, websiteUrl, altText);

		return showSponsors(model);
	}


	@PostMapping("/AdminSponsors/{id}/UpdateImage")
	public String updateImage(Model model,
							  @PathVariable(value = "id") long id,
							  @RequestParam(value = "logo") MultipartFile logo) {
		sponsorService.updateImage(id, logo);

		return showSponsors(model);
	}
}
