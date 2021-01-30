/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import net.greensnet.club.dao.CrestEntity;
import net.greensnet.club.service.CrestService;
import net.greensnet.competition.league.TableService;
import net.greensnet.competition.league.domain.LeagueTable;
import net.greensnet.domain.Fixture;
import net.greensnet.news.NewsService;
import net.greensnet.service.FeaturedItemService;
import net.greensnet.service.FixtureService;
import net.greensnet.service.SnowballService;
import net.greensnet.service.SponsorService;
import net.greensnet.service.hfctv.HfctvService;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

import static java.lang.Integer.min;
import static java.util.Objects.nonNull;

@Controller
public class HomeController {

	private final NewsService newsService;
	private final FixtureService fixtureService;
	private final TableService tableService;
	private final SnowballService snowballService;
	private final SponsorService sponsorService;
	private final CrestService crestService;
	private final FeaturedItemService featuredItemService;
	private final DateHelper dateHelper;
	private final HfctvService hfctvService;

	private static final String NEWS_ITEMS = "news_items";
	private static final String SNOWBALL_DRAWS = "snowball_draws";

	private static final String SPONSORS = "sponsors";
	private static final String LEAGUE_TABLE_START = "league_table_start";
	private static final String LEAGUE_TABLE_END = "league_table_end";
	private static final String LEAGUE_TABLES = "league_tables";

	private static final String NEXT_FIXTURE = "next_fixture";
	private static final String LAST_FIXTURE = "last_fixture";
	private static final String LAST_HIGHLIGHTS = "last_highlights";
	private static final String NEXT_HOME_CREST = "nextHomeCrest";
	private static final String NEXT_AWAY_CREST = "nextAwayCrest";
	private static final String PREVIOUS_HOME_CREST = "previousHomeCrest";
	private static final String PREVIOUS_AWAY_CREST = "previousAwayCrest";

	private static final String FEATURED_ITEMS = "featuredItems";

	private static final String PAGE_TITLE = "page_title";

	@Autowired
	public HomeController(NewsService newsService,
						  FixtureService fixtureService,
						  TableService tableService,
						  SnowballService snowballService,
						  SponsorService sponsorService,
						  CrestService crestService,
						  FeaturedItemService featuredItemService,
						  DateHelper dateHelper,
						  HfctvService hfctvService) {
		this.newsService = newsService;
		this.fixtureService = fixtureService;
		this.tableService = tableService;
		this.snowballService = snowballService;
		this.sponsorService = sponsorService;
		this.crestService = crestService;
		this.featuredItemService = featuredItemService;
		this.dateHelper = dateHelper;
		this.hfctvService = hfctvService;
	}

	@RequestMapping({"", "/", "/Home"})
	public ModelAndView getHomePageDetails(Model model) {

		// TODO: This is icky code to work out which parts of the league table to show.
		// Make something nicer!
		List<LeagueTable> tables = tableService.getTables(dateHelper.getCurrentSeason());
		int start;
		int end;
		if (nonNull(tables) && !tables.isEmpty()) {
			if (tables.get(0).getHendonPosition() <= 4) {
				start = 0;
				end = 7;
			} else if (tables.get(0).getHendonPosition() >= tables.get(0).getRows().size() - 4) {
				start = tables.get(0).getRows().size() - 9;
				end = tables.get(0).getRows().size() - 1;
			} else {
				start = tables.get(0).getHendonPosition() - 4;
				end = tables.get(0).getHendonPosition() + 3;
			}
			model.addAttribute(LEAGUE_TABLE_START, start);
			model.addAttribute(LEAGUE_TABLE_END, min(end, tables.get(0).getRows().size()));
		}
		model.addAttribute(LEAGUE_TABLES, tables);

		model.addAttribute(LAST_HIGHLIGHTS, hfctvService.getLatest().orElse(null));

		Fixture last =  fixtureService.getLastFixture();
		Fixture next =  fixtureService.getNextFixture();
		model.addAttribute(LAST_FIXTURE, last);
		model.addAttribute(NEXT_FIXTURE, next);

		Optional<CrestEntity> homeCrest = crestService.getByClubForSeason(last.getHomeTeamId(), last.getSeason());
		Optional<CrestEntity> awayCrest = crestService.getByClubForSeason(last.getAwayTeamId(), last.getSeason());

		model.addAttribute(PREVIOUS_HOME_CREST, getCrest(homeCrest));
		model.addAttribute(PREVIOUS_AWAY_CREST, getCrest(awayCrest));

		if (null !=  next) {
			homeCrest = crestService.getByClubForSeason(next.getHomeTeamId(), next.getSeason());
			awayCrest = crestService.getByClubForSeason(next.getAwayTeamId(), next.getSeason());
		}

		model.addAttribute(NEXT_HOME_CREST, getCrest(homeCrest));
		model.addAttribute(NEXT_AWAY_CREST, getCrest(awayCrest));

		model.addAttribute(NEWS_ITEMS, newsService.getLatest(10, false));
		model.addAttribute(SNOWBALL_DRAWS, snowballService.getDraws());
		model.addAttribute(SPONSORS, sponsorService.getAllActiveSponsors());

		model.addAttribute(FEATURED_ITEMS, featuredItemService.getAllActiveItems());

		model.addAttribute(PAGE_TITLE, "Home");

		return new ModelAndView("th_home");
	}

	private String getCrest(Optional<CrestEntity> crest) {
		if (!crest.isPresent()) {
			return "/images/crests/unknown.png";
		}
		return crest.get().getAwsUrl();
	}


}
