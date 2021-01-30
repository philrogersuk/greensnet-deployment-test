/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import net.greensnet.domain.StaticPage;
import net.greensnet.service.StaticPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TrustController {

	private static final String PAGE_TITLE = "page_title";
	private static final String PAGE_CONTENT = "page_content";


	private final StaticPageService staticPageService;

	@Autowired
	public TrustController(StaticPageService staticPageService) {
		this.staticPageService = staticPageService;
	}

	@RequestMapping("/Trust")
	public String handleRequest(Model model) {
		StaticPage page = staticPageService.getPage("Trust");
		if (null != page) {
			model.addAttribute(PAGE_CONTENT, page.getContent());
		}
		model.addAttribute(PAGE_TITLE, "Hendon FC Supporters Trust");

		return "th_trust";
	}

	@RequestMapping("/Trust/{pageName}")
	public String handleRequest(Model model,
								@PathVariable(value = "pageName") String pageName) {
		StaticPage page = staticPageService.getPage("Trust" + pageName);
		if (null != page) {
			model.addAttribute(PAGE_CONTENT, page.getContent());
		}
		model.addAttribute(PAGE_TITLE, "Hendon FC Supporters Trust");

		return "th_trust";
	}

}
