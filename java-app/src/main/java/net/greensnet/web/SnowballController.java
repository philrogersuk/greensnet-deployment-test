/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import net.greensnet.domain.StaticPage;
import net.greensnet.service.SnowballService;
import net.greensnet.service.StaticPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SnowballController {

    private static final String PAGE_CONTENT = "page_content";
    private static final String SNOWBALL_DRAWS = "snowball_draws";
    private static final String PAGE_TITLE = "page_title";

    private final SnowballService manager;
    private final StaticPageService pageManager;

    @Autowired
    public SnowballController(SnowballService manager, StaticPageService pageManager) {
        this.manager = manager;
        this.pageManager = pageManager;
    }

    @RequestMapping("/Snowball")
    public String getDraws(Model model) {
        StaticPage page = pageManager.getPage("Snowball");
        if (null != page) {
            model.addAttribute(PAGE_CONTENT, page.getContent());
        }
        model.addAttribute(SNOWBALL_DRAWS, manager.getDraws());
        model.addAttribute(PAGE_TITLE, "Snowball Draw");

        return "th_snowball";
    }
}
