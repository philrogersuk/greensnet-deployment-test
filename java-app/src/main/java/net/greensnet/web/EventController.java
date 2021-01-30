/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import net.greensnet.service.StaticPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EventController {

	private final StaticPageService staticPageService;

	private static final String PAGE_TITLE = "page_title";
	private static final String PAGE_CONTENT = "page_content";


	@Autowired
	public EventController(StaticPageService staticPageService) {
		this.staticPageService = staticPageService;
	}

	@RequestMapping("/Events")
	public String viewAppearances(Model model, @RequestParam(value = "page") String page) {

		if (null != page) {
			model.addAttribute(PAGE_CONTENT, staticPageService.getPage(page).getContent());
			model.addAttribute(PAGE_TITLE, "Events: " + page);
		} else {
			model.addAttribute(PAGE_CONTENT, "We have no details about the requested event");
			model.addAttribute(PAGE_TITLE, "Events: Unknown Event");
		}

		return "th_events";
	}
}
