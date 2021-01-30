/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web.admin;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

	private static final String PAGE_TITLE = "page_title";

	@RequestMapping("/AdminLogin")
	public String loginPage(Model model) {
		model.addAttribute(PAGE_TITLE, "Admin - Login");
		return "th_admin/login";
	}
}
