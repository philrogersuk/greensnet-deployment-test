/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import lombok.extern.slf4j.Slf4j;
import net.greensnet.domain.StaticPage;
import net.greensnet.service.StaticPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
public class GenericController {

	private final StaticPageService staticPageService;

	private static final String PAGE_TITLE = "page_title";
	private static final String PAGE_CONTENT = "page_content";

	@Autowired
	public GenericController(StaticPageService staticPageService) {
		this.staticPageService = staticPageService;
	}

	@RequestMapping(value = "/{pageName:[A-Z0-9][a-zA-Z0-9]+}")
	public String handleRequest(Model model,
								@PathVariable("pageName") String pageName) {

		log.info("Generic page requested: {}", pageName);

		StaticPage page = staticPageService.getPage(pageName);
		if (null != page) {
			model.addAttribute(PAGE_CONTENT, page.getContent());
			model.addAttribute(PAGE_TITLE, page.getTitle());
		}
		return "th_" + pageName.toLowerCase();
	}
}
