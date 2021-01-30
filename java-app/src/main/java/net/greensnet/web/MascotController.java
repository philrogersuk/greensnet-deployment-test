/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import net.greensnet.domain.StaticPage;
import net.greensnet.service.SponsorService;
import net.greensnet.service.StaticPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MascotController {
	private static final String PAGE_TITLE = "page_title";
	private static final String PAGE_CONTENT = "page_content";
	private static final String MASCOTS = "mascots";

	private final StaticPageService staticPageService;
	private final SponsorService sponsorService;

	@Autowired
	public MascotController(StaticPageService staticPageService, SponsorService sponsorService) {
		this.staticPageService = staticPageService;
		this.sponsorService = sponsorService;
	}


	@RequestMapping("/Mascots")
	public String getMascots(Model model) {
		StaticPage page = staticPageService.getPage("Mascots");
		if (null != page) {
			model.addAttribute(PAGE_CONTENT, page.getContent());
		}
		model.addAttribute(MASCOTS, sponsorService.getMascots());
		model.addAttribute(PAGE_TITLE, "Mascot Packages");

		return "th_mascots";
	}
}