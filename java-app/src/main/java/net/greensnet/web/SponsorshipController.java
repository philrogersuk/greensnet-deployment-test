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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Controller
public class SponsorshipController {
	private static final String PAGE_TITLE = "page_title";
	private static final String PAGE_CONTENT = "page_content";
	private static final String MATCHBALL_SPONSORS = "matchball_sponsors";
	private static final String MATCH_SPONSORS = "match_sponsors";
	private static final String PLAYER_SPONSORS = "player_sponsors";
	private static final String PROGRAMME_SPONSORS = "programme_sponsors";
	private static final String HFCTV_SPONSORS = "hfctv_sponsors";

	private final StaticPageService staticPageService;
	private final SponsorService sponsorService;

	@Autowired
	public SponsorshipController(StaticPageService staticPageService, SponsorService sponsorService) {
		this.staticPageService = staticPageService;
		this.sponsorService = sponsorService;
	}

	@RequestMapping("/Sponsorship")
	public String getSponsorshipDetails(Model model,
									  @RequestParam(value = "method", required = false) String method) {

		if (Objects.nonNull(method)) {
			if ("matchball".equals(method)) {
				StaticPage page = staticPageService.getPage("Matchball");
				if (null != page) {
					model.addAttribute(PAGE_CONTENT, page.getContent());
				}
				model.addAttribute(MATCHBALL_SPONSORS, sponsorService.getMatchballSponsors());
				model.addAttribute(PAGE_TITLE, "Matchball Sponsorship");
			} else if ("match".equals(method)) {
				StaticPage page = staticPageService.getPage("Match");
				if (null != page) {
					model.addAttribute(PAGE_CONTENT, page.getContent());
				}
				model.addAttribute(MATCH_SPONSORS, sponsorService.getMatchSponsors());
				model.addAttribute(PAGE_TITLE, "Match Sponsorship");
			} else if ("players".equals(method)) {
				StaticPage page = staticPageService.getPage("PlayerSponsorship");
				if (null != page) {
					model.addAttribute(PAGE_CONTENT, page.getContent());
				}
				model.addAttribute(PLAYER_SPONSORS, sponsorService.getPlayerSponsors());
				model.addAttribute(PAGE_TITLE, "Player Sponsorship");
			} else if ("programme".equals(method)) {
				StaticPage page = staticPageService.getPage("ProgrammeSponsorship");
				if (null != page) {
					model.addAttribute(PAGE_CONTENT, page.getContent());
				}
				model.addAttribute(PROGRAMME_SPONSORS, sponsorService.getProgrammeSponsors());
				model.addAttribute(PAGE_TITLE, "Programme Sponsorship");
			} else if ("hfctv".equals(method)) {
				StaticPage page = staticPageService.getPage("HFCTVSponsorship");
				if (null != page) {
					model.addAttribute(PAGE_CONTENT, page.getContent());
				}
				model.addAttribute(HFCTV_SPONSORS, sponsorService.getProgrammeSponsors());
				model.addAttribute(PAGE_TITLE, "HFCTV Sponsorship");
			} else if ("manofthematch".equals(method)) {
				model.addAttribute(PAGE_TITLE, "Man of the Match Sponsorship");
			}
		} else {
			StaticPage page = staticPageService.getPage("Sponsorship");
			if (null != page) {
				model.addAttribute(PAGE_CONTENT, page.getContent());
			}
			model.addAttribute(PAGE_TITLE, "Sponsorship & Advertising");
		}

		return "th_sponsorship";
	}
}