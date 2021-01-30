/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import net.greensnet.service.AppearanceService;
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
import java.util.Objects;

@Controller
public class AppearanceController {

	private static final String PAGE_TITLE = "page_title";
	private static final String SEASON_LIST = "seasonList";
	private static final String APPEARANCES = "appearances";
	private static final String CURRENT_SEASON = "currentSeason";
	private static final String NEXT_SEASON = "nextSeason";
	private static final String PREVIOUS_SEASON = "previousSeason";

	private final AppearanceService appearanceService;
	private final FixtureService fixtureService;
	private final DateHelper dateHelper;

	@Autowired
	public AppearanceController(AppearanceService appearanceService,
								FixtureService fixtureService,
								DateHelper dateHelper) {
		this.appearanceService = appearanceService;
		this.fixtureService = fixtureService;
		this.dateHelper = dateHelper;
	}

	@RequestMapping("/Appearances")
	public String viewAppearances(Model model, @RequestParam(value = "method", required = false) String method,
								  @RequestParam(value = "season", required = false) Integer season) {

		if (null != method && method.equals("archive")) {
			if (Objects.nonNull(season)) {
				return "redirect:/Appearances/" + season;
			} else {
				return "redirect:/Appearances/Archive";
			}
		}
		return viewAppearances(model, dateHelper.getCurrentSeason());
	}

	@GetMapping("/Appearances/Archive")
	public String viewArchiveList(Model model) {
		List<Integer> items = fixtureService.getValidSeasons();
		int[][] items2 = dateHelper.to2DSeasonArray(items);
		model.addAttribute(SEASON_LIST, items2);
		model.addAttribute(PAGE_TITLE, "Appearances - Archive");

		return "th_appearances/archive";
	}

	@GetMapping("/Appearances/{season}")
	public String viewAppearances(Model model,
								  @PathVariable int season) {
		model.addAttribute(APPEARANCES, appearanceService.getAppearances(season));
		model.addAttribute(PAGE_TITLE, "Appearances - Season " + season + "/" + (season + 1));

		model.addAttribute(CURRENT_SEASON, season);
		if ((season - 1) >= fixtureService.getValidSeasons().stream().mapToInt(Integer::intValue).min().orElse(1908)) {
			model.addAttribute(PREVIOUS_SEASON, season-1);
		}
		if ((season + 1) <= dateHelper.getCurrentSeason()) {
			model.addAttribute(NEXT_SEASON, season + 1);
		}


		return "th_appearances/view";
	}
}
