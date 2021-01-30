package net.greensnet.competition.league.web;

/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */

import net.greensnet.club.service.ClubService;
import net.greensnet.competition.CompetitionService;
import net.greensnet.competition.league.LeagueEntryService;
import net.greensnet.competition.league.domain.LeagueEntry;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LeagueEntryController {

	private static final String LEAGUE_ENTRY_LIST = "league_entry_list";
	private static final String PAGE_TITLE = "page_title";
	private static final String SEASON_LIST = "season_list";
	private static final String CLUB_LIST = "club_list";
	private static final String LEAGUE_LIST = "league_list";


	private final CompetitionService competitionService;
	private final LeagueEntryService leagueEntryService;
	private final ClubService clubService;
	private final DateHelper dateHelper;

	@Autowired
	public LeagueEntryController(CompetitionService competitionService,
								 LeagueEntryService leagueEntryService,
								 ClubService clubService,
								 DateHelper dateHelper) {
		this.competitionService = competitionService;
		this.leagueEntryService = leagueEntryService;
		this.clubService = clubService;
		this.dateHelper = dateHelper;
	}

	@RequestMapping("AdminLeagueEntries")
	public String handleRequest(Model model,
								@RequestParam(value = "method", required = false) String method,
								@RequestParam(value = "season", required = false) Integer season,
								@RequestParam(value = "club", required = false) Long club,
								@RequestParam(value = "competition", required = false) Long competition) {
		if (null != method && method.equals("add")) {
			leagueEntryService.createLeagueEntry(LeagueEntry.builder()
					.season(season)
					.competitionId(competition)
					.clubId(club)
					.clubName(null)
					.build());
		}

		model.addAttribute(LEAGUE_ENTRY_LIST, leagueEntryService.getAllEntries());

		model.addAttribute(SEASON_LIST, dateHelper.getSeasonArray());
		model.addAttribute(LEAGUE_LIST, competitionService.getLeagueCompetitions());
		model.addAttribute(CLUB_LIST, clubService.getAllClubs());
		model.addAttribute(PAGE_TITLE, "Competitions - League Entries");

		return "th_admin/leagueentry/list";
	}

	@PostMapping("AdminLeagueEntries/Add")
	public String createLeagueEntry(Model model,
									@RequestParam(value = "season", required = false) Integer season,
									@RequestParam(value = "club", required = false) Long club,
									@RequestParam(value = "competition", required = false) Long competition) {
		leagueEntryService.createLeagueEntry(LeagueEntry.builder()
				.season(season)
				.competitionId(competition)
				.clubId(club)
				.clubName(null)
				.build());
		model.addAttribute(LEAGUE_ENTRY_LIST, leagueEntryService.getAllEntries());
		model.addAttribute(SEASON_LIST, dateHelper.getSeasonArray());
		model.addAttribute(LEAGUE_LIST, competitionService.getLeagueCompetitions());
		model.addAttribute(CLUB_LIST, clubService.getAllClubs());
		model.addAttribute(PAGE_TITLE, "Competitions - League Entries");

		return "th_admin/leagueentry/list";
	}

}