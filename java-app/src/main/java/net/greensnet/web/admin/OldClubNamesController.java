
/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web.admin;

import net.greensnet.club.domain.ClubName;
import net.greensnet.club.service.ClubService;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OldClubNamesController {

	private static final String PAGE_TITLE = "page_title";
	private static final String CLUB_LIST = "club_list";
	private static final String SEASON_LIST = "season_list";
	private static final String OLD_CLUB_NAME_LIST = "old_club_name_list";

	private final ClubService clubService;
	private	final DateHelper dateHelper;

	@Autowired
	public OldClubNamesController (ClubService clubService,
								   DateHelper dateHelper) {
		this.clubService = clubService;
		this.dateHelper = dateHelper;
	}

	@RequestMapping("AdminOldClubNames")
	public String handleRequest(Model model,
								@RequestParam(value = "method", required = false) String method,
								@RequestParam(value = "firstSeason", required = false, defaultValue = "-1") int firstSeason,
								@RequestParam(value = "lastSeason", required = false, defaultValue = "-1") int lastSeason,
								@RequestParam(value = "oldName", required = false) String oldName,
								@RequestParam(value = "id", required = false) Long id,
								@RequestParam(value = "club", required = false) Long club)  {
		if (null != method && method.equals("add")) {
			ClubName newName = ClubName.builder()
					.name(oldName)
					.startSeason(firstSeason)
					.endSeason(lastSeason).build();
			clubService.addOldName(club, newName);
		} else if (null != method && method.equals("delete")) {
			clubService.removeOldName(id);
		}
		model.addAttribute(CLUB_LIST, clubService.getAllClubs());
		model.addAttribute(OLD_CLUB_NAME_LIST, clubService.getAllOldClubNames());
		model.addAttribute(SEASON_LIST, dateHelper.getSeasonArray());
		model.addAttribute(PAGE_TITLE, "Old Club Names");

		return "th_admin/oldclubnames";
	}
}