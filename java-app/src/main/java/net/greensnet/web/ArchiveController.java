/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ArchiveController {

	private static final String PAGE_TITLE = "page_title";

	@RequestMapping(value = "/Archives")
	public String handleRequest(Model model) {
		model.addAttribute(PAGE_TITLE, "Archives");
		return "th_archives";
	}
}
