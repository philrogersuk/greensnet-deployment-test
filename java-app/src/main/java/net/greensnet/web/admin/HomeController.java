/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("adminHomeController")
public class HomeController {

	private static final String PAGE_TITLE = "page_title";

	@RequestMapping("/Admin")
	public String viewAdminHomePage(Model model) {

		model.addAttribute(PAGE_TITLE, "Admin");
		return "th_admin/home";
	}
}
