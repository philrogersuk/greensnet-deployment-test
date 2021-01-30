/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import net.greensnet.domain.TeamFixture;
import net.greensnet.domain.TeamNews;
import net.greensnet.domain.TeamTable;
import net.greensnet.service.YouthTeamService;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class YouthController {

	private static final String PAGE_TITLE = "page_title";
	private static final String ARCHIVE_MAP = "archive_map";
	private static final String NEWS_ITEMS = "news_items";
	private static final String FIXTURE_LIST = "fixture_list";
	private static final String LEAGUE_TABLES = "league_tables";


	private final YouthTeamService youthTeamService;
	private final DateHelper dateHelper;

	@Autowired
	public YouthController(YouthTeamService youthTeamService,
						   DateHelper dateHelper) {
		this.youthTeamService = youthTeamService;
		this.dateHelper = dateHelper;
	}

	@RequestMapping("YouthArchive")
	public String handleArchive(Model model) {
		model.addAttribute(ARCHIVE_MAP, youthTeamService.getTeamMap());
		model.addAttribute(PAGE_TITLE, "Youth and Reserves: Archive");
		return "th_youtharchive";
	}

	@RequestMapping("YouthNews")
	public String handleNews(Model model,
							 @RequestParam(name="team", required = false) Integer teamType,
							 @RequestParam(name="season", required = false) Integer seasonParam) {
		if (null != teamType) {

			int season;
			if (null != seasonParam) {
				season = seasonParam;
			} else {
				season = dateHelper.getCurrentSeason();
			}

			// get stories
			List<TeamNews> list = youthTeamService.getNewsForTeam(season, teamType);
			if (0 < list.size()) {
				model.addAttribute(NEWS_ITEMS, list);
			}
			// get team name
			String teamName = youthTeamService.getTeamType(teamType).getName();
			model.addAttribute("team", teamType);
			model.addAttribute("season", seasonParam);
			model.addAttribute(PAGE_TITLE, teamName + " News, " + season + "/" + (season + 1));
		} else {
			model.addAttribute(PAGE_TITLE, "Youth and Reserves: News");
		}

		return "th_youthnews";
	}

	@RequestMapping("YouthFixtures")
	public String handleFixtures(Model model,
							 @RequestParam(name="team", required = false) Integer teamType,
							 @RequestParam(name="season", required = false) Integer seasonParam) {
		if (null != teamType) {

			int season;
			if (null != seasonParam) {
				season = seasonParam;
			} else {
				season = dateHelper.getCurrentSeason();
			}

			// get stories
			List<TeamFixture> list = youthTeamService.getFixturesForTeam(season, teamType);
			if (0 < list.size()) {
				model.addAttribute(FIXTURE_LIST, list);
			}
			// get team name
			String teamName = youthTeamService.getTeamType(teamType).getName();
			model.addAttribute("team", teamType);
			model.addAttribute("season", seasonParam);
			model.addAttribute(PAGE_TITLE, teamName + " Fixtures & Results, " + season + "/" + (season + 1));
		} else {
			model.addAttribute(PAGE_TITLE, "Youth and Reserves: Fixtures & Results");
		}

		return "th_youthfixtures";
	}


	@RequestMapping("YouthTables")
	public String handleTables(Model model,
								 @RequestParam(name="team", required = false) Integer teamType,
								 @RequestParam(name="team", required = false) Integer seasonParam) {
		if (null != teamType) {

			int season;
			if (null != seasonParam) {
				season = seasonParam;
			} else {
				season = dateHelper.getCurrentSeason();
			}

			// get stories
			TeamTable table = youthTeamService.getTableForTeam(season, teamType);
			if (null != table) {
				model.addAttribute(LEAGUE_TABLES, table);
			}
			// get team name
			String teamName = youthTeamService.getTeamType(teamType).getName();
			model.addAttribute("team", teamType);
			model.addAttribute("season", seasonParam);
			model.addAttribute(PAGE_TITLE, teamName + " Table, " + season + "/" + (season + 1));
		} else {
			model.addAttribute(PAGE_TITLE, "Youth and Reserves: Tables");
		}

		return "th_youthtables";
	}
}
