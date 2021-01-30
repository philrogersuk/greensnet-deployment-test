/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition;

import net.greensnet.competition.domain.CompetitionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CompetitionAdminController {

	static final String COMPETITION_TYPES = "competition_types";
	static final String PAGE_TITLE = "page_title";
	static final String COMPETITIONS = "competitions";

	private final CompetitionService service;

	@Autowired
	public CompetitionAdminController(CompetitionService service) {
		this.service = service;
	}

	@GetMapping("AdminCompetitions")
	public String showCompetitions(Model model) {
		model.addAttribute(COMPETITION_TYPES, CompetitionType.values());
		model.addAttribute(COMPETITIONS, service.getAllCompetitions());
		model.addAttribute(PAGE_TITLE, "Competitions");

		return "th_admin/competition";
	}

	@PostMapping("AdminCompetitions/Create")
	public String createCompetition(Model model,
									@RequestParam(value = "name", required = false) String name,
									@RequestParam(value = "shortCode", required = false) String shortCode,
									@RequestParam(value = "type", required = false) CompetitionType type) {
		service.createCompetition(name, shortCode, type);

		return showCompetitions(model);
	}

/*	@RequestMapping("AdminCompetitions/Delete/{id}")
	public String deleteCompetition(Model model,
									@PathVariable(value = "id") long id) {
		service.deleteCompetition(id);

		return showCompetitions(model);
	}*/

}