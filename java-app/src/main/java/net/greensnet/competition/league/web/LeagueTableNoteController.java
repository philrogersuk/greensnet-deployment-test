/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league.web;


import net.greensnet.competition.CompetitionService;
import net.greensnet.competition.league.LeagueEntryService;
import net.greensnet.competition.league.LeagueTableNoteService;
import net.greensnet.competition.league.domain.LeagueEntry;
import net.greensnet.competition.league.domain.LeagueTableNote;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class LeagueTableNoteController {

	public static final String PAGE_TITLE = "page_title";
	public static final String SEASON_LIST = "season_list";
	public static final String LEAGUE_LIST = "league_list";
	public static final String SELECTED_SEASON = "selected_season";
	public static final String SELECTED_COMPETITION = "selected_competition";
	public static final String CLUB_LIST = "club_list";
	public static final String LEAGUE_TABLE_NOTE = "league_table_note";
	public static final String LEAGUE_TABLE_NOTES = "league_table_notes";

	private final CompetitionService competitionService;
	private final LeagueTableNoteService leagueTableNoteService;
	private final LeagueEntryService leagueEntryService;
	private final DateHelper dateHelper;

	@Autowired
	public LeagueTableNoteController(CompetitionService competitionService,
                                     LeagueTableNoteService leagueTableNoteService,
									 LeagueEntryService leagueEntryService,
                                     DateHelper dateHelper) {
		this.competitionService = competitionService;
		this.leagueTableNoteService = leagueTableNoteService;
		this.leagueEntryService = leagueEntryService;
		this.dateHelper = dateHelper;
	}

	@RequestMapping("AdminLeagueTableNotes")
	public String handleRequest(Model model,
								@RequestParam(value = "method", required = false) String method,
								@RequestParam(value = "season", required = false) Integer season,
								@RequestParam(value = "competition", required = false) Long competition,
								@RequestParam(value = "club", required = false) Long club,
								@RequestParam(value = "points", required = false) Integer points,
								@RequestParam(value = "reason", required = false) String reason,
								@RequestParam(value = "id", required = false) Long id) {
		if (null == season || null == competition) {
			model.addAttribute(SEASON_LIST, dateHelper.getSeasonArray());
			model.addAttribute(LEAGUE_LIST, competitionService.getLeagueCompetitions());
			model.addAttribute(PAGE_TITLE, "League Table Notes - Select Competition");
			return "th_admin/leaguenote/select";
		} else {
			model.addAttribute(SELECTED_SEASON, season);
			model.addAttribute(SELECTED_COMPETITION, competition);

			if (method == null) {
				return showNotesForCompetition(model, season, competition);
			} else if ("edit".equals(method)) {
				List<LeagueEntry> clubList = leagueEntryService.getLeagueEntries(season, competition);
				model.addAttribute(CLUB_LIST, clubList);
				model.addAttribute(LEAGUE_TABLE_NOTE, leagueTableNoteService.getLeagueTableNote(id));
				model.addAttribute(PAGE_TITLE,
						"Competitions - League Table Notes " + season + "/" + (season + 1));
			} else if ("editSubmit".equals(method)) {
				leagueTableNoteService.editLeagueTableNote(id, club, points, reason);

				return showNotesForCompetition(model, season, competition);
			} else if ("add".equals(method)) {
				leagueTableNoteService.createLeagueTableNote(season, competition, club, points, reason);

				return showNotesForCompetition(model, season, competition);
			}
		}
		return "admin/leaguetablenotes";
	}

	@PostMapping("AdminLeagueTableNotes/{season}/{competition}")
	public String showNotesForCompetition(Model model,
								@PathVariable(value = "season") int season,
								@PathVariable(value = "competition") long competition){
		List<LeagueEntry> clubList = leagueEntryService.getLeagueEntries(season, competition);
		model.addAttribute(CLUB_LIST, clubList);
		model.addAttribute(LEAGUE_TABLE_NOTES, leagueTableNoteService.getLeagueTableNotes(season, competition));
		model.addAttribute(PAGE_TITLE,
				"League Table Notes - Season " + season + "/" + (season + 1));

		return "th_admin/leaguenote/list";
	}

	@PostMapping("AdminLeagueTableNotes/Delete/{id}")
	public String deleteNote(Model model,
							 @PathVariable(value = "id") long id){
		LeagueTableNote note = leagueTableNoteService.getLeagueTableNote(id);
		leagueTableNoteService.removeLeagueNote(id);

		return showNotesForCompetition(model, note.getSeason(), note.getCompetitionId());
	}
}