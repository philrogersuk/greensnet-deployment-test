/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.club.web;

import net.greensnet.club.domain.Club;
import net.greensnet.club.service.ClubService;
import net.greensnet.club.service.ClubServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DirectionsController {

	private static final String JSP_PAGE = "th_directions";
	private static final String PAGE_TITLE = "page_title";
	private static final String CLUB = "club_info";
	private static final String CLUB_LIST = "club_list";

	private final ClubService service;

	@Autowired
	public DirectionsController(ClubService service) {
		this.service = service;
	}

	@RequestMapping("/Directions")
	public String getDirections(Model model,
								@RequestParam(value = "id", required = false) Long id) {

		Club club = null;

		if (null != id) {
			if (-1 != id) {
				club = service.getClub(id);
			}
			populateRequestVariables(model, club);
		} else {
			club = service.getClub(ClubServiceImpl.HENDON_ID);
			populateRequestVariables(model, club);
		}

		return JSP_PAGE;
	}

	private void populateRequestVariables(Model model, Club club) {
		model.addAttribute(CLUB, club);
		model.addAttribute(CLUB_LIST, service.getClubsForCurrentSeason());
		if (null != club) {
			model.addAttribute(PAGE_TITLE, "Directions - " + club.getInternalName());
		} else {
			model.addAttribute(PAGE_TITLE, "Directions");
		}
	}
}
