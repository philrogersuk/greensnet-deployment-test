/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web.admin;

import net.greensnet.service.StaticPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class GenericPageController {

	private final StaticPageService staticPageService;

	private static final String PAGE_TITLE = "page_title";
	private static final String PAGE_CONTENT = "page_content";
	private static final String PAGE_LIST = "page_list";

	@Autowired
	public GenericPageController(StaticPageService staticPageService) {
		this.staticPageService = staticPageService;
	}

	@RequestMapping("AdminGenericPages")
	public String handleRequest(Model model, @RequestParam(value = "method", required = false) String method,
								@RequestParam(value = "page", required = false) String page,
								@RequestParam(value = "pageContent", required = false) String content,
								@RequestParam(value = "title", required = false) String title,
								@RequestParam(value = "id", required = false) String id,
								@RequestParam(value = "oldId", required = false) String oldId) {

		if (null != method) {
			if ("add".equals(method)) {
				staticPageService.createPage(page, content, title);
			} else if ("delete".equals(method)) {
				staticPageService.deletePage(id);
			} else if ("edit".equals(method)) {
				model.addAttribute(PAGE_CONTENT, staticPageService.getPage(id));
			} else if ("editSubmit".equals(method)) {
				staticPageService.editPage(oldId, page, content, title);
			}
		}

		if (!"edit".equals(method)) {
			model.addAttribute(PAGE_LIST, staticPageService.getAll());
		}
		model.addAttribute(PAGE_TITLE, "Generic Pages");

		return "th_admin/genericpage";
	}
}
