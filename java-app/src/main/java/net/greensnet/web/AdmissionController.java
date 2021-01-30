/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import net.greensnet.domain.StaticPage;
import net.greensnet.service.FixtureService;
import net.greensnet.service.StaticPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Controller
public class AdmissionController {

    private final FixtureService service;
    private final StaticPageService staticPageService;

    private static final String PAGE_CONTENT = "page_content";
    private static final String PAGE_TITLE = "page_title";
    private static final String TICKETED_FIXTURES = "ticketed_fixtures";

    @Autowired
    public AdmissionController(FixtureService service, StaticPageService staticPageService) {
        this.service = service;
        this.staticPageService = staticPageService;
    }


    @RequestMapping("/Admission")
    public String viewAdmissionDetails(Model model) {

        StaticPage page = staticPageService.getPage("Admission");
        if (Objects.nonNull(page)) {
            model.addAttribute(PAGE_CONTENT, page.getContent());
        }
        model.addAttribute(TICKETED_FIXTURES, service.getFutureTicketedFixtures());
        model.addAttribute(PAGE_TITLE, "Admission");

        return "th_admission";
    }
}
