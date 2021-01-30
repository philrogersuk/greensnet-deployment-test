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

@Controller("AdminYouthNewsController")
public class YouthNewsController {

	private static final String PAGE_TITLE = "page_title";
	private static final String YOUTH_ENTRY_LIST = "youth_entry_list";
	private static final String NEWS_ITEM = "news_item";
	private static final String NEWS_ITEMS = "news_items";
	
	private final YouthTeamService youthTeamService;
	private final DateHelper dateHelper;

	@Autowired
	public YouthNewsController(YouthTeamService youthTeamService,
							   DateHelper dateHelper) {
		this.youthTeamService = youthTeamService;
		this.dateHelper = dateHelper;
	}


	@RequestMapping("AdminYouthNews")
	public String handleRequest(Model model,
								@RequestParam(value = "method", required = false) String method,
								@RequestParam(value = "season", required = false) Integer season,
								@RequestParam(value = "teamType", required = false) Integer teamType,

								@RequestParam(value = "headline", required = false) String headline,
								@RequestParam(value = "story", required = false) String story,
								@RequestParam(value = "oldId", required = false) long oldId,
								@RequestParam(value = "id", required = false) long id,
								@RequestParam(value = "datetime", required = false) String datetime) {
		if (null == season || null == teamType) {
			model.addAttribute(YOUTH_ENTRY_LIST, youthTeamService.getAllEntries());
		} else {
			if (null != method && method.equals("add")) {
				LocalDateTime timeStamp = dateHelper.parseStandardDateTimeFormat(datetime);
				youthTeamService.createNewsItem(season, teamType, headline, story, timeStamp);
			} else if (null != method && method.equals("editSubmit")) {
				LocalDateTime timeStamp = dateHelper.parseStandardDateTimeFormat(datetime);
				youthTeamService.editNewsItem(oldId, headline, story, timeStamp);
			} else if (null != method && method.equals("edit")) {
				model.addAttribute(NEWS_ITEM, youthTeamService.getNewsItemById(id));
			} else if (null != method && method.equals("delete")) {
				youthTeamService.deleteNewsItemById(id);
				model.addAttribute(NEWS_ITEMS, youthTeamService.getNewsForTeam(season, teamType));
			} else {
				model.addAttribute(NEWS_ITEMS, youthTeamService.getNewsForTeam(season, teamType));
			}
			model.addAttribute("season", season);
			model.addAttribute("teamType", teamType);
		}

		model.addAttribute(PAGE_TITLE, "Youth and Reserves: News");

		return "admin/youthnews";
	}
}
