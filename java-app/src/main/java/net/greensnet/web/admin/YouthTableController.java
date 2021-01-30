/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web.admin;


import net.greensnet.service.YouthTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("AdminYouthTableController")
public class YouthTableController {

	private static final String LEAGUE_TABLES = "league_tables";
	private static final String YOUTH_ENTRY_LIST = "youth_entry_list";
	private static final String SEASON = "season";
	private static final String TEAM_TYPE = "team_type";
	
	
	private final YouthTeamService youthTeamService;

	@Autowired
	public YouthTableController(YouthTeamService youthTeamService) {
		this.youthTeamService = youthTeamService;
	}


	@RequestMapping("AdminYouthTables")
	public String handleRequest(Model model,
								@RequestParam(value = "method", required = false) String method,
								@RequestParam(value = "season", required = false) Integer season,
								@RequestParam(value = "teamType", required = false) Integer teamType,
								@RequestParam(value = "pageContent", required = false) String content,
								@RequestParam(value = "oldId", required = false) long id) {
		if (null == season || null == teamType) {
			model.addAttribute(YOUTH_ENTRY_LIST, youthTeamService.getAllEntries());
		} else {
			if (null != method && method.equals("add")) {
				youthTeamService.createTable(season, teamType, content);
			} else if (null != method && method.equals("editSubmit")) {

				youthTeamService.updateTable(id, content);
			}
			model.addAttribute(LEAGUE_TABLES, youthTeamService.getTableForTeam(season, teamType));
			model.addAttribute(SEASON, season);
			model.addAttribute(TEAM_TYPE, teamType);
		}

		return "admin/youthtable";
	}
}
