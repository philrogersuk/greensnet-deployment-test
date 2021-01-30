/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web.admin;


import net.greensnet.service.YouthTeamService;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Controller("AdminYouthFixtureController")
public class YouthFixtureController {
	private static final String PAGE_TITLE = "page_title";
	private static final String YOUTH_ENTRY_LIST = "youth_entry_list";
	private static final String SEASON = "season";
	private static final String FIXTURE = "fixture";
	private static final String FIXTURE_LIST = "fixture_list";
	private static final String TEAM_TYPE = "team_type";

	private final YouthTeamService youthTeamService;
	private final DateHelper dateHelper;

	@Autowired
	public YouthFixtureController(YouthTeamService youthTeamService,
								  DateHelper dateHelper) {
		this.youthTeamService = youthTeamService;
		this.dateHelper = dateHelper;
	}


	@RequestMapping("AdminYouthFixtures")
	public String handleRequest(Model model,
								@RequestParam(value = "method", required = false) String method,
								@RequestParam(value = "season", required = false) Integer season,
								@RequestParam(value = "teamType", required = false) Integer teamType,

								@RequestParam(value = "competition", required = false) String competition,
								@RequestParam(value = "kickoff", required = false) String kickoff,
								@RequestParam(value = "opposition", required = false) String opposition,
								@RequestParam(value = "scorers", required = false) String scorers,
								@RequestParam(value = "id", required = false) long id,
								@RequestParam(value = "venue", required = false) Character venue,
								@RequestParam(value = "hendonScore", defaultValue = "-1") int hendonScore,
								@RequestParam(value = "oppositionScore", defaultValue = "-1") int oppositionScore,
								@RequestParam(value = "hendonScore90", defaultValue = "-1") int hendonScore90,
								@RequestParam(value = "oppositionScore90", defaultValue = "-1") int oppositionScore90,
								@RequestParam(value = "hendonPenalties", defaultValue = "-1") int hendonPenalties,
								@RequestParam(value = "oppositionPenalties", defaultValue = "-1") int oppositionPenalties) {
		if (null == season || null == teamType) {
			model.addAttribute(YOUTH_ENTRY_LIST, youthTeamService.getAllEntries());
		} else {
			if (null != method) {
				if ("add".equals(method)) {
					processFixtureEdit(model, id, competition, kickoff, opposition, venue, hendonScore,
							oppositionScore, hendonScore90, oppositionScore90, hendonPenalties, oppositionPenalties,
							scorers, method, season, teamType);
				} else if ("editSubmit".equals(method)) {
					processFixtureEdit(model, id, competition, kickoff, opposition, venue, hendonScore,
							oppositionScore, hendonScore90, oppositionScore90, hendonPenalties, oppositionPenalties,
							scorers, method, season, teamType);
				} else if ("edit".equals(method)) {
					model.addAttribute(FIXTURE, youthTeamService.getFixtureById(id));
				} else if ("delete".equals(method)) {
					youthTeamService.deleteFixtureById(id);
					model.addAttribute(FIXTURE_LIST, youthTeamService.getFixturesForTeam(season, teamType));
				}
			} else {
				model.addAttribute(FIXTURE_LIST, youthTeamService.getFixturesForTeam(season, teamType));
			}
			model.addAttribute(SEASON, season);
			model.addAttribute(TEAM_TYPE, teamType);
		}

		model.addAttribute(PAGE_TITLE, "Youth and Reserves: Fixtures");
		return "admin/youthfixture";
	}

	private void processFixtureEdit(Model model, Long id, String competition, String kickoff, final String opposition,
									final Character venue, final int hendonScore, final int oppositionScore,
									final int hendonScore90, final int oppositionScore90, final int hendonPenalties,
									final int oppositionPenalties, String scorers,
									final String method, final int season,
									final int teamType) throws NumberFormatException {
		LocalDateTime timeStamp = dateHelper.parseStandardDateTimeFormat(kickoff);
		if (method.equals("add")) {
			youthTeamService.createFixture(season, teamType, competition, venue, timeStamp, opposition, hendonScore,
					oppositionScore, hendonScore90, oppositionScore90, hendonPenalties, oppositionPenalties, scorers);
		} else {
			youthTeamService.updateFixture(id, season, teamType, competition, venue, timeStamp, opposition, hendonScore,
					oppositionScore, hendonScore90, oppositionScore90, hendonPenalties, oppositionPenalties, scorers);
		}
		model.addAttribute(FIXTURE_LIST, youthTeamService.getFixturesForTeam(season, teamType));
	}
}
