/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import net.greensnet.club.dao.CrestEntity;
import net.greensnet.club.service.ClubService;
import net.greensnet.club.service.CrestService;
import net.greensnet.domain.Fixture;
import net.greensnet.domain.FixtureListMonth;
import net.greensnet.service.FixtureService;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class FixtureController {

	private static final String PAGE_TITLE = "page_title";
	private static final String FIXTURE_LIST = "fixture_list";
	private static final String HENDON_NAME = "hendon_name";
	private static final String FIXTURES = "fixtures";
	private static final String SEASON_LIST = "seasonList";
	private static final String CURRENT_SEASON = "currentSeason";
	private static final String NEXT_SEASON = "nextSeason";
	private static final String PREVIOUS_SEASON = "previousSeason";

	private final FixtureService fixtureService;
	private final ClubService clubService;
	private final DateHelper dateHelper;
	private final CrestService crestService;

	@Autowired
	public FixtureController(FixtureService fixtureService,
							 ClubService clubService,
							 DateHelper dateHelper,
							 CrestService crestService) {
		this.fixtureService = fixtureService;
		this.clubService = clubService;
		this.dateHelper = dateHelper;
		this.crestService = crestService;
	}

	@RequestMapping("/Fixtures")
	public String getFixtures(Model model, @RequestParam(value = "method", required = false) String method,
							  @RequestParam(value = "season", required = false) Integer season) {
		if ("archive".equals(method)) {
			if (null != season) {
				return "redirect:/Fixtures/" + season;
			} else {
				return "redirect:/Fixtures/Archive";
			}
		}
		int currentSeason = dateHelper.getCurrentSeason();
		return getFixtures(model, currentSeason);
	}

	@GetMapping("/Fixtures/Archive")
	public String getArchive(Model model) {
		List<Integer> items = fixtureService.getValidSeasons();
		int[][] items2 = dateHelper.to2DSeasonArray(items);
		model.addAttribute(SEASON_LIST, items2);
		model.addAttribute(PAGE_TITLE, "Fixtures & Results - Archive");

		return "th_fixtures/archive";
	}

	@GetMapping("/Fixtures/{season}")
	public String getFixtures(Model model,
						   @PathVariable(value = "season") int season) {
		List<FixtureListMonth> items = fixtureService.getMonthlyFixturesBySeason(season);

		for(FixtureListMonth month: items) {
			for(Fixture fixture: month.getFixtures()) {
				fixture.setHomeCrest(getCrest(crestService.getByClubForSeason(fixture.getHomeTeamId(), fixture.getSeason())));
				fixture.setAwayCrest(getCrest(crestService.getByClubForSeason(fixture.getAwayTeamId(), fixture.getSeason())));
			}
		}

		model.addAttribute(FIXTURES, items);
		model.addAttribute(PAGE_TITLE,
				"Fixtures & Results - Season " + season + "/" + (season + 1));

		model.addAttribute(CURRENT_SEASON, season);
		if ((season - 1) >= fixtureService.getValidSeasons().stream().mapToInt(Integer::intValue).min().orElse(1908)) {
			model.addAttribute(PREVIOUS_SEASON, season-1);
		}
		if ((season + 1) <= dateHelper.getCurrentSeason()) {
			model.addAttribute(NEXT_SEASON, season + 1);
		}

		return "th_fixtures/view";
	}



	private void getFixtureListForSeason(Model model, int season, boolean currentSeason) {
		List<FixtureListMonth> items = fixtureService.getMonthlyFixturesBySeason(season);
		model.addAttribute(FIXTURE_LIST, items);
		if (currentSeason) {
			model.addAttribute(PAGE_TITLE,
					"Fixtures and Results - Season " + season + "/" + (season + 1));
		} else {
			model.addAttribute(PAGE_TITLE,
					"Results - Season " + season + "/" + (season + 1));
		}
		model.addAttribute(HENDON_NAME, clubService.getHendonTeamName(season));
	}

	private String getCrest(Optional<CrestEntity> crest) {
		if (!crest.isPresent()) {
			return "/images/crests/unknown.png";
		}
		return crest.get().getAwsUrl();
	}
}
