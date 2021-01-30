/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league.web;

import net.greensnet.competition.CompetitionService;
import net.greensnet.competition.league.LeagueEntryService;
import net.greensnet.competition.league.LeagueFixtureService;
import net.greensnet.competition.league.domain.LeagueEntry;
import net.greensnet.service.GridService;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class LeagueFixtureController {

	private static final String PAGE_TITLE = "page_title";
	private static final String SEASON_LIST = "season_list";
	private static final String LEAGUE_LIST = "league_list";
	private static final String SELECTED_SEASON = "selected_season";
	private static final String SELECTED_COMPETITION = "selected_competition";
	private static final String CLUB_LIST = "club_list";
	private static final String LEAGUE_FIXTURE = "league_fixture";
	private static final String FIXTURE_GRID = "fixture_grid";

	private final CompetitionService competitionService;
	private final LeagueEntryService leagueEntryService;
	private final LeagueFixtureService leagueFixtureService;
	private final GridService gridService;
	private final DateHelper dateHelper;

	@Autowired
	public LeagueFixtureController(CompetitionService competitionService,
								   LeagueEntryService leagueEntryService,
								   LeagueFixtureService leagueFixtureService,
								   GridService gridService,
								   DateHelper dateHelper) {
		this.competitionService = competitionService;
		this.leagueEntryService = leagueEntryService;
		this.leagueFixtureService = leagueFixtureService;
		this.gridService = gridService;
		this.dateHelper = dateHelper;
	}

	@RequestMapping("AdminLeagueFixtures")
	public String handleRequest(Model model, @RequestParam(value = "method", required = false) String method,
								@RequestParam(value = "season", required = false) Integer season,
								@RequestParam(value = "competition", required = false) Long competition,
								@RequestParam(value = "id", required = false) Long id,
								@RequestParam(value = "homeClub", required = false) Long homeClubId,
								@RequestParam(value = "awayClub", required = false) Long awayClubId,
								@RequestParam(value = "homeScore", required = false) Integer homeScore,
								@RequestParam(value = "awayScore", required = false) Integer awayScore,
								@RequestParam(value = "attendance", required = false) Integer attendance,
								@RequestParam(value = "date", required = false) String stringDate) {

		if (null == season || null == competition) {
			model.addAttribute(SEASON_LIST, dateHelper.getSeasonArray());
			model.addAttribute(LEAGUE_LIST, competitionService.getLeagueCompetitions());
			model.addAttribute(PAGE_TITLE, "Competitions - Select Competition");
		} else {
			model.addAttribute(SELECTED_SEASON, season);
			model.addAttribute(SELECTED_COMPETITION, competition);

			List<LeagueEntry> clubList = leagueEntryService.getLeagueEntries(season, competition);
			if (null != method) {
				if ("edit".equals(method)) {
					model.addAttribute(CLUB_LIST, clubList);
					model.addAttribute(LEAGUE_FIXTURE, leagueFixtureService.getLeagueFixture(id));
					model.addAttribute(PAGE_TITLE, "Competitions - Edit League Fixture");
				} else if ("editSubmit".equals(method) || "add".equals(method)) {
					LocalDate date = dateHelper.parseStandardDateFormat(stringDate);

					if ("editSubmit".equals(method)) {
						leagueFixtureService.editLeagueFixture(id, homeClubId, awayClubId, season, competition, homeScore, awayScore,
								attendance, date);
					} else {
						leagueFixtureService.createLeagueFixture(homeClubId, awayClubId, season, competition, homeScore, awayScore,
								attendance, date);
					}

					prepareGridPage(model, season, competition, clubList);
				} else if ("remove".equals(method)) {
					leagueFixtureService.removeLeagueFixture(id);
					prepareGridPage(model, season, competition, clubList);
				}
			} else if (0 != clubList.size()) {
				prepareGridPage(model, season, competition, clubList);
			} else {
				model.addAttribute(CLUB_LIST, null);
				model.addAttribute(SEASON_LIST, dateHelper.getSeasonArray());
				model.addAttribute(LEAGUE_LIST, competitionService.getLeagueCompetitions());
				model.addAttribute(PAGE_TITLE, "Competitions - Select Competition");
			}
		}

		return "th_admin/leaguefixture";
	}

	private void prepareGridPage(Model model, int season, long competition, List<LeagueEntry> clubList) {
		model.addAttribute(CLUB_LIST, clubList);
		model.addAttribute(FIXTURE_GRID, gridService.getFixtureGrid(competition, season));
		model.addAttribute(PAGE_TITLE,
				"Competitions - League Fixtures " + season + "/" + (season + 1));
	}
}