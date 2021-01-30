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
public class SeatsController {
    private final SponsorService manager;
    private final StaticPageService pageManager;

    private static final String SEATS = "seats";
    private static final String PAGE_CONTENT = "page_content";
    private static final String PAGE_TITLE = "page_title";

    @Autowired
    public SeatsController(SponsorService manager, StaticPageService pageManager) {
        this.manager = manager;
        this.pageManager = pageManager;
    }

    @RequestMapping("/GaryMcCannStand")
    public String getSeats(Model model) {
        StaticPage page = pageManager.getPage("GaryMcCannStand");

        if (null != page) {
            model.addAttribute(PAGE_CONTENT, page.getContent());
        }
        model.addAttribute(SEATS, manager.getSeatsInGrid());
        model.addAttribute(PAGE_TITLE, "The Gary McCann Stand Sponsorship");

        return "th_seats";
    }
}
