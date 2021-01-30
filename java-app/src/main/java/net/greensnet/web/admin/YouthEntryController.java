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

@Controller
public class YouthEntryController {

	private static final String PAGE_TITLE = "page_title";
	private static final String TEAM_TYPE_LIST = "team_type_list";
	private static final String SEASON_LIST = "season_list";

	private final YouthTeamService youthTeamService;
	private final DateHelper dateHelper;

	@Autowired
	public YouthEntryController(YouthTeamService youthTeamService,
								DateHelper dateHelper) {
		this.youthTeamService = youthTeamService;
		this.dateHelper = dateHelper;
	}

	@RequestMapping("AdminYouthTeams")
	public String handleRequest(Model model,
								@RequestParam(value = "method", required = false) String method,
								@RequestParam(value = "season", required = false) int season,
								@RequestParam(value = "team", required = false) int type) {
		if (null != method && method.equals("add")) {
			youthTeamService.createTeamEntry(season, type);
		}
		model.addAttribute(TEAM_TYPE_LIST, youthTeamService.getAllTeams());
		model.addAttribute(SEASON_LIST, dateHelper.getSeasonArray());
		model.addAttribute(PAGE_TITLE, "Youth and Reserves: Entries");

		return "admin/youthentry";
	}
}
