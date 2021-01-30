/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import com.google.common.collect.Lists;
import net.greensnet.club.dao.CrestEntity;
import net.greensnet.club.service.ClubService;
import net.greensnet.club.service.CrestService;
import net.greensnet.domain.*;
import net.greensnet.domain.fixture.FixtureEvent;
import net.greensnet.domain.hfctv.YoutubeVideo;
import net.greensnet.exceptions.NotFoundException;
import net.greensnet.service.FixtureService;
import net.greensnet.service.hfctv.HfctvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

import static net.greensnet.domain.fixture.EventType.*;

@Controller
public class ReportController {

	private static final String FIXTURE = "fixture";
	private static final String FIXTURE_EVENTS = "fixture_events";
	private static final String HOME_CREST = "homeCrest";
	private static final String AWAY_CREST = "awayCrest";
	private static final String HENDON_NAME = "hendon_name";
	private static final String HENDON_TEAM = "hendon_team";
	private static final String OPPOSITION_TEAM = "opposition_team";
	private static final String REPORT_TITLE = "report_title";
	private static final String PAGE_TITLE = "page_title";
	private static final String NEXT_FIXTURE = "nextFixture";
	private static final String PREVIOUS_FIXTURE = "previousFixture";
	private static final String VIDEOS = "videos";

	private final FixtureService fixtureService;
	private final ClubService clubService;
	private final CrestService crestService;
	private final HfctvService hfctvService;

	@Autowired
	public ReportController(FixtureService fixtureService,
							ClubService clubService,
							CrestService crestService,
							HfctvService hfctvService) {
		this.fixtureService = fixtureService;
		this.clubService = clubService;
		this.crestService = crestService;
		this.hfctvService = hfctvService;
	}

	@RequestMapping("/Reports")
	public String getReport(@RequestParam(value = "id") final long id) {
		return "redirect:/Reports/" + id;
	}

	@GetMapping("/Reports/{id}")
	public String viewReport(final Model model,
							 @PathVariable("id") long id) {
		Fixture fixture = fixtureService.getFixtureById(id, true, true);
		if (null == fixture) {
			throw new NotFoundException();
		}
		model.addAttribute(FIXTURE, fixture);

		setCrests(model, fixture);
		setVideos(model, fixture);

		String hendonName = clubService.getHendonTeamName(fixture.getSeason());
		model.addAttribute(HENDON_NAME, hendonName);
		if (null != fixture.getHendonTeam()) {
			model.addAttribute(HENDON_TEAM,
					populateHendonTeam(fixture.getHendonTeam(), fixture.getSeason()));
		}
		if (null != fixture.getOppositionTeam()) {
			model.addAttribute(OPPOSITION_TEAM,
					populateOppositionTeam(fixture.getOppositionTeam()));
		}

		Fixture next = fixtureService.getNextFixture(fixture).orElse(null);
		model.addAttribute(NEXT_FIXTURE, next);

		Fixture previous = fixtureService.getPreviousFixture(fixture).orElse(null);
		model.addAttribute(PREVIOUS_FIXTURE, previous);

		model.addAttribute(FIXTURE_EVENTS, createFixtureEventList(fixture.getHendonTeam(),
				fixture.getOppositionTeam(),
				fixture.getHendonGoals(),
				fixture.getOppositionGoals()));

		setPageTitle(model, fixture, hendonName);

		return "th_reports";
	}

	private void setCrests(Model model, Fixture fixture) {
		Optional<CrestEntity> homeCrest = crestService.getByClubForSeason(fixture.getHomeTeamId(), fixture.getSeason());
		Optional<CrestEntity> awayCrest = crestService.getByClubForSeason(fixture.getAwayTeamId(), fixture.getSeason());

		model.addAttribute(HOME_CREST, getCrest(homeCrest));
		model.addAttribute(AWAY_CREST, getCrest(awayCrest));
	}

	private void setVideos(Model model, Fixture fixture) {
		List<YoutubeVideo> videos = hfctvService.getByFixture(fixture);

		model.addAttribute(VIDEOS, videos);
	}


	private void setPageTitle(Model model, Fixture fixture, String hendonName) {
		String title = getPageTitle(fixture, hendonName);
		if (!title.equals("")) {
			model.addAttribute(REPORT_TITLE, title.replace("-1", "-L"));
			model.addAttribute(REPORT_TITLE, title.replace("--", "-"));
		}
		if (null == fixture.getReport() || fixture.getReport().equals("")) {
			model.addAttribute(PAGE_TITLE, "Match Details");
		} else {
			model.addAttribute(PAGE_TITLE, "Match Report");
		}
	}

	private String getCrest(Optional<CrestEntity> crest) {
		return crest.map(value -> value.getAwsUrl().replace("_h100", "_h40"))
				.orElse("/images/crests/unknown.png");
	}

	private String getPageTitle(Fixture fixture, String hendonName) {
		if (fixture.getVenueAsInitial() == 'A') {
			return fixture.getOppositionName() + " " + fixture.getOppositionScore() + "--" + fixture.getHendonScore()
					+ " " + hendonName;

		}
		return hendonName + " " + fixture.getHendonScore() + "-" + fixture.getOppositionScore() + " "
				+ fixture.getOppositionName();
	}

	private List<FixtureAppearance> populateHendonTeam(Set<FixtureAppearance> hendonTeam, int season) {
		List<FixtureAppearance> list = new ArrayList<>(hendonTeam);
		Collections.sort(list);
		for (Object o : hendonTeam) {
			StaffMember player = ((FixtureAppearance) o).getPlayer();
			if (null != player.getOldNames() && !player.getOldNames().isEmpty()) {
				player.setFirstName(player.getFirstNameForSeason(season));
				player.setLastName(player.getLastNameForSeason(season));
			}
		}
		return list;
	}

	private List<OppositionAppearance> populateOppositionTeam(Set<OppositionAppearance> oppositionTeam) {
		List<OppositionAppearance> list = new ArrayList<>(oppositionTeam);
		Collections.sort(list);
		return list;
	}

	private List<FixtureEvent> createFixtureEventList(Set<FixtureAppearance> hendonAppearances,
													  Set<OppositionAppearance> oppositionAppearances,
													  Set<FixtureGoal> hendonGoals,
													  Set<OppositionGoal> oppositionGoals) {
		List<FixtureEvent> events = Lists.newArrayList();

		extractEventsFromAppearances(hendonAppearances, events);
		extractEventsFromAppearances(oppositionAppearances, events);
		extractEventsFromHendonGoals(hendonGoals, hendonAppearances, events);
		extractEventsFromOppositionGoals(oppositionGoals, oppositionAppearances, events);

		Collections.sort(events);

		return events;
	}

	private void extractEventsFromHendonGoals(Set<FixtureGoal> goals,
											  Set<FixtureAppearance> squad,
											  List<FixtureEvent> events) {
		for(FixtureGoal goal: goals) {
			events.add(FixtureEvent.builder()
					.type(GOAL)
					.minute(goal.getMinuteScored())
					.player(findPlayerById(squad, goal.getScorer().getId()))
					.hendonEvent(true)
					.build());
		}
	}

	private void extractEventsFromOppositionGoals(Set<OppositionGoal> goals,
												  Set<OppositionAppearance> squad,
											  List<FixtureEvent> events) {
		for(OppositionGoal goal: goals) {
			events.add(FixtureEvent.builder()
					.type(GOAL)
					.minute(goal.getMinuteScored())
					.player(findPlayerByName(squad, goal.getFirstName(), goal.getLastName()))
					.hendonEvent(false)
					.build());
		}
	}

	private void extractEventsFromAppearances(Set<? extends GenericAppearance> appearances,
											  List<FixtureEvent> events) {
		for(GenericAppearance app: appearances) {
			if (-1 != app.getRedCardMinute()) {
				events.add(FixtureEvent.builder().type(RED_CARD)
												.minute(app.getRedCardMinute())
												.player(app)
												.hendonEvent(app instanceof FixtureAppearance)
												.build());
			}
			if (-1 != app.getYellowCardMinute()) {
				events.add(FixtureEvent.builder().type(YELLOW_CARD)
						.minute(app.getYellowCardMinute())
						.player(app)
						.hendonEvent(app instanceof FixtureAppearance)
						.build());
			}
			if (-1 != app.getSubstitutionMinute()) {
				events.add(FixtureEvent.builder().type(SUBSTITUTION)
						.minute(app.getSubstitutionMinute())
						.player(app)
						.player2(findPlayerByShirtNumber(appearances, app.getPlayerReplaced()))
						.hendonEvent(app instanceof FixtureAppearance)
						.build());
			}
		}
	}

	private GenericAppearance findPlayerByShirtNumber(Set<? extends GenericAppearance> appearances, int shirtNumber) {
		for (GenericAppearance app: appearances) {
			if (app.getShirtNumber() == shirtNumber) {
				return app;
			}
		}
		return null;
	}

	private GenericAppearance findPlayerById(Set<FixtureAppearance> appearances, long id) {
		for (FixtureAppearance app: appearances) {
			if (app.getPlayer().getId() == id) {
				return app;
			}
		}
		return null;
	}

	private GenericAppearance findPlayerByName(Set<OppositionAppearance> appearances, String firstName, String lastName) {
		for (OppositionAppearance app: appearances) {
			if (app.getFirstName().equals(firstName) && app.getLastName().equals(lastName)) {
				return app;
			}
		}
		return null;
	}

}