package net.greensnet.web.admin;

import net.greensnet.club.service.ClubService;
import net.greensnet.competition.CompetitionService;
import net.greensnet.domain.*;
import net.greensnet.service.FixtureService;
import net.greensnet.service.StaffService;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller("AdminFixtureController")
public class FixtureController {

	private final FixtureService fixtureService;
	private final ClubService clubService;
	private final CompetitionService competitionService;
	private final StaffService staffService;
	private final DateHelper dateHelper;

	private static final String PAGE_TITLE = "page_title";
	private static final String SEASON_LIST = "season_list";
	private static final String SELECTED_SEASON = "selected_season";
	private static final String CLUB_LIST = "club_list";
	private static final String COMPETITION_LIST = "competition_list";
	private static final String VENUE_LIST = "venue_list";
	private static final String FIXTURE_LIST = "fixture_list";
	private static final String FIXTURE = "fixture";
	private static final String STAFF_LIST = "staff_list";
	private static final String HENDON_TEAM = "hendon_team";
	private static final String OPPOSITION_TEAM = "opposition_team";
	private static final String HENDON_GOALS = "hendon_goals";
	private static final String OPPOSITION_GOALS = "opposition_goals";


	@Autowired
	FixtureController(FixtureService fixtureService,
					  ClubService clubService,
					  CompetitionService competitionService,
					  StaffService staffService,
					  DateHelper dateHelper) {
		this.fixtureService = fixtureService;
		this.clubService = clubService;
		this.competitionService = competitionService;
		this.staffService = staffService;
		this.dateHelper = dateHelper;
	}

	@RequestMapping("AdminFixtures")
	public String handleRequest(Model model, @RequestParam(value = "method", required = false) String method,
								@RequestParam(value = "season", required = false) Integer season,
								@RequestParam(value = "id", required = false) Long fixtureId,
								HttpServletRequest request) {

		if (null == season && null== method) {
			model.addAttribute(SEASON_LIST, dateHelper.getSeasonArray());
		} else {
			model.addAttribute(SELECTED_SEASON, season);
			model.addAttribute(CLUB_LIST, clubService.getAllClubs());
			model.addAttribute(COMPETITION_LIST, competitionService.getAllCompetitions());
			model.addAttribute(VENUE_LIST, FixtureVenue.getValues());

			if (null == method) {
				model.addAttribute(FIXTURE_LIST, fixtureService.getFixturesBySeason(season));
			} else if ("add".equals(method) || "editSubmit".equals(method)) {
				processFixtureEdit(request, method, season, fixtureId);
				model.addAttribute(FIXTURE_LIST, fixtureService.getFixturesBySeason(season));
			} else if ("edit".equals(method)) {
				model.addAttribute(FIXTURE, fixtureService.getFixtureById(fixtureId, true, true));
			} else if ("squads".equals(method)) {
				System.out.println(season);
				model.addAttribute(STAFF_LIST,
						staffService.getPlayersForSeason(season, true, false));
				model.addAttribute(HENDON_TEAM, fixtureService.getHendonTeam(fixtureId));
				model.addAttribute(OPPOSITION_TEAM, fixtureService.getOppositionTeam(fixtureId));
			} else if ("goals".equals(method)) {
				model.addAttribute(FIXTURE, fixtureService.getFixtureById(fixtureId, true, true));
				model.addAttribute(HENDON_GOALS, fixtureService.getHendonScorers(fixtureId));
				model.addAttribute(OPPOSITION_GOALS, fixtureService.getOppositionScorers(fixtureId));
				model.addAttribute(HENDON_TEAM, fixtureService.getHendonTeam(fixtureId));
				model.addAttribute(OPPOSITION_TEAM, fixtureService.getOppositionTeam(fixtureId));
			} else if ("squadsSubmit".equals(method)) {
				processSquadEdit(request, method, season);
				model.addAttribute(FIXTURE_LIST, fixtureService.getFixturesBySeason(season));
			} else if ("goalsSubmit".equals(method)) {
				processScorersEdit(request, method, season);
				model.addAttribute(FIXTURE_LIST, fixtureService.getFixturesBySeason(season));
			}
		}
		model.addAttribute(PAGE_TITLE, "Fixtures");
		return "th_admin/fixture";
	}

	private void processFixtureEdit(final HttpServletRequest request, final String method, final int season, final Long fixtureId) {
		long competition = Long.valueOf(request.getParameter("competition"));
		LocalDateTime timeStamp = dateHelper.parseStandardDateTimeFormat(request.getParameter("kickoff"));
		FixtureVenue venue = fixtureService.getVenue(Integer.valueOf(request.getParameter("venue")));
		long altVenue = Long.valueOf(request.getParameter("altVenue"));
		long opposition = Long.valueOf(request.getParameter("opposition"));
		int hendonScore = getIntFromRequestParams(request, "hendonScore");
		int oppositionScore = getIntFromRequestParams(request, "oppositionScore");
		int hendonScore90 = getIntFromRequestParams(request, "hendonScore90");
		int oppositionScore90 = getIntFromRequestParams(request, "oppositionScore90");
		int hendonPenalties = getIntFromRequestParams(request, "hendonPenalties");
		int oppositionPenalties = getIntFromRequestParams(request, "oppositionPenalties");
		int abandonedMinute = getIntFromRequestParams(request, "abandonedMinute");
		int attendance = getIntFromRequestParams(request, "attendance");
		String abandonedReason = request.getParameter("abandonedReason");
		boolean notPlayed = isRequestParamTrue("notPlayed", request);
		String report = request.getParameter("report");
		String author = request.getParameter("author");
		String matchSponsor = request.getParameter("match");
		String matchballSponsor = request.getParameter("matchball");
		String programmeSponsor = request.getParameter("programme");
		String mascot = request.getParameter("mascot");
		String highlightsId = request.getParameter("highlightsId");
		String youtubeId = request.getParameter("youtubeId");
		String hfctvSponsor = request.getParameter("hfctvSponsor");
		String ticketURL = request.getParameter("ticketURL");
		if (method.equals("add")) {
			fixtureService.createFixture(season, competition, venue, altVenue, timeStamp, opposition, hendonScore,
					oppositionScore, hendonScore90, oppositionScore90, hendonPenalties, oppositionPenalties,
					abandonedMinute, abandonedReason, notPlayed, attendance, report, author, matchSponsor,
					matchballSponsor, programmeSponsor, mascot, highlightsId, youtubeId, hfctvSponsor, ticketURL);
		} else {
			fixtureService.updateFixture(fixtureId, season, competition, venue, altVenue, timeStamp, opposition, hendonScore,
					oppositionScore, hendonScore90, oppositionScore90, hendonPenalties, oppositionPenalties,
					abandonedMinute, abandonedReason, notPlayed, attendance, report, author, matchSponsor,
					matchballSponsor, programmeSponsor, mascot, highlightsId, youtubeId, hfctvSponsor, ticketURL);
		}
	}

	private int getIntFromRequestParams(final HttpServletRequest request, String paramName) {
		int value = -1;
		if (!isRequestParamEmpty(paramName, request)) {
			value = Integer.valueOf(request.getParameter(paramName));
		}
		return value;
	}

	private boolean isRequestParamEmpty(String value, HttpServletRequest request) {
		if (null == request.getParameter(value)) {
			return true;
		}
		return "".equals(request.getParameter(value));
	}

	private boolean isRequestParamTrue(String paramName, HttpServletRequest request) {
		if (null == request.getParameter(paramName)) {
			return false;
		}
		return true;//request.getParameter(paramName).equalsIgnoreCase("on");
	}

	private boolean isRequestParamFalse(String paramName, HttpServletRequest request) {
		return !isRequestParamTrue(paramName, request);
	}

	private void processSquadEdit(final HttpServletRequest request, final String method, final int season) {
		long fixture = Long.valueOf(request.getParameter("fixtureId"));
		ArrayList<FixtureAppearance> hendonTeam = new ArrayList<>();
		for (int i = 1; i < 35; i++) {
			if (isRequestParamEmpty("player" + i, request)) {
				continue;
			}
			FixtureAppearance appearance = getHendonAppearance(i, request);
			hendonTeam.add(appearance);
		}

		ArrayList<OppositionAppearance> oppositionTeam = new ArrayList<>();
		for (int i = 1; i < 35; i++) {
			if (isRequestParamEmpty("olastName" + i, request)) {
				continue;
			}
			OppositionAppearance appearance = getOpponentAppearance(i, request);
			oppositionTeam.add(appearance);
		}
		fixtureService.setTeams(fixture, hendonTeam, oppositionTeam);
	}

	private void processScorersEdit(final HttpServletRequest request, final String method, final int season) {
		Fixture fixture = fixtureService.getFixtureById(Long.valueOf(request.getParameter("fixtureId")), true, true);

		ArrayList<FixtureGoal> hendonGoals = new ArrayList<>();

		for (int i = 1; i < fixture.getHendonScore() + 1; i++) {
			if (isRequestParamEmpty("player" + i, request)
					&& isRequestParamEmpty("owngoal" + i, request)) {
				continue;
			}
			FixtureGoal goal = getHendonGoal(i, request);
			hendonGoals.add(goal);
		}

		ArrayList<OppositionGoal> oppositionGoals = new ArrayList<>();
		for (int i = 1; i < fixture.getOppositionScore() + 1; i++) {
			if (isRequestParamEmpty("oplayer" + i, request)
					&& isRequestParamEmpty("oowngoal" + i, request)) {
				break;
			}
			OppositionGoal goal = getOppositionGoal(i, request);
			oppositionGoals.add(goal);
		}
		fixtureService.setScorers(fixture.getId(), hendonGoals, oppositionGoals);
	}

	private FixtureAppearance getHendonAppearance(final int i, final HttpServletRequest request)
			throws NumberFormatException {
		int shirtNum = Integer.valueOf(request.getParameter("shirt" + i));
		long playerId = Long.valueOf(request.getParameter("player" + i));
		boolean substitute = isRequestParamFalse("start" + i, request);
		int yellowMin = getIntFromRequestParams(request, "yellow" + i);
		int redMin = getIntFromRequestParams(request, "red" + i);
		int subMin = getIntFromRequestParams(request, "subMin" + i);
		int playerReplaced = getIntFromRequestParams(request, "replaced" + i);

		FixtureAppearance appearance = new FixtureAppearance();
		appearance.setShirtNumber(shirtNum);
		appearance.setPlayer(staffService.getStaffMember(playerId));
		appearance.setSubstitute(substitute);
		appearance.setYellowCardMinute(yellowMin);
		appearance.setRedCardMinute(redMin);
		appearance.setSubstitutionMinute(subMin);
		appearance.setPlayerReplaced(playerReplaced);
		return appearance;
	}

	private OppositionAppearance getOpponentAppearance(final int i, final HttpServletRequest request) {
		int shirtNum = Integer.valueOf(request.getParameter("oshirt" + i));
		boolean substitute = isRequestParamFalse("ostart" + i, request);
		int yellowMin = getIntFromRequestParams(request, "oyellow" + i);
		int redMin = getIntFromRequestParams(request, "ored" + i);
		int subMin = getIntFromRequestParams(request, "osubMin" + i);
		int playerReplaced = getIntFromRequestParams(request, "oreplaced" + i);

		OppositionAppearance appearance = new OppositionAppearance();
		appearance.setShirtNumber(shirtNum);
		appearance.setFirstName(request.getParameter("ofirstName" + i));
		appearance.setLastName(request.getParameter("olastName" + i));
		appearance.setSubstitute(substitute);
		appearance.setYellowCardMinute(yellowMin);
		appearance.setRedCardMinute(redMin);
		appearance.setSubstitutionMinute(subMin);
		appearance.setPlayerReplaced(playerReplaced);
		return appearance;
	}

	private FixtureGoal getHendonGoal(int i, HttpServletRequest request) {
		long playerId;
		boolean penalty = isRequestParamTrue("penalty" + i, request);
		boolean owngoal = isRequestParamTrue("owngoal" + i, request);
		int minuteScored = getIntFromRequestParams(request, "time" + i);

		FixtureGoal goal = new FixtureGoal();
		if (!isRequestParamEmpty("player" + i, request)) {
			playerId = Long.valueOf(request.getParameter("player" + i));
			goal.setScorer(staffService.getStaffMemberByFixtureAppearanceId(playerId));
		}
		goal.setPenalty(penalty);
		goal.setOwnGoal(owngoal);
		goal.setMinuteScored(minuteScored);
		return goal;
	}

	private OppositionGoal getOppositionGoal(int i, HttpServletRequest request) {
		long playerId = Long.valueOf(request.getParameter("oplayer" + i));
		boolean penalty = isRequestParamTrue("openalty" + i, request);
		boolean owngoal = isRequestParamTrue("oowngoal" + i, request);
		int minuteScored = getIntFromRequestParams(request, "otime" + i);

		OppositionGoal goal = new OppositionGoal();
		goal.setFirstName(staffService.getOppositionAppearance(playerId).getFirstName());
		goal.setLastName(staffService.getOppositionAppearance(playerId).getLastName());
		goal.setPenalty(penalty);
		goal.setOwnGoal(owngoal);
		goal.setMinuteScored(minuteScored);
		return goal;
	}
}
