/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league.web;

import net.greensnet.competition.league.TableService;
import net.greensnet.competition.league.domain.LeagueTable;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class TableController {

    private static final String PAGE_TITLE = "page_title";
    private static final String LEAGUE_TABLES = "leagueTables";
    private static final String SEASON_LIST = "seasonList";
    private static final String CURRENT_SEASON = "currentSeason";
    private static final String NEXT_SEASON = "nextSeason";
    private static final String PREVIOUS_SEASON = "previousSeason";

    private final TableService tableService;
    private final DateHelper dateHelper;

    @Autowired
    public TableController (TableService tableService,
                            DateHelper dateHelper) {
        this.tableService = tableService;
        this.dateHelper = dateHelper;
    }

    @RequestMapping("/Tables")
    public String getTables(Model model, @RequestParam(value = "method", required = false) String method,
                            @RequestParam(value = "season", required = false) Integer season) {
        if (null != method && method.equals("archive")) {
            if (null != season) {
                return "redirect:/Tables/" + season;
            } else {
                return "redirect:/Tables/Archive";
            }
        }

        final int currentSeason = dateHelper.getCurrentSeason();
        return getTable(model, currentSeason);
    }

    @GetMapping("/Tables/{season}")
    public String getTable(Model model,
                            @PathVariable(value = "season") int season) {
        List<LeagueTable> items = tableService.getTables(season);

        model.addAttribute(LEAGUE_TABLES, items);
        model.addAttribute(PAGE_TITLE,
                "League Tables - Season " + season + "/" + (season + 1));

        model.addAttribute(CURRENT_SEASON, season);
        if ((season - 1) >= tableService.getValidSeasons().stream().mapToInt(Integer::intValue).min().orElse(1908)) {
            model.addAttribute(PREVIOUS_SEASON, season-1);
        }
        if ((season + 1) <= dateHelper.getCurrentSeason()) {
            model.addAttribute(NEXT_SEASON, season + 1);
        }

        return "th_tables/view";
    }

    @GetMapping("/Tables/Archive")
    public String getArchive(Model model) {
        List<Integer> items = tableService.getValidSeasons();
        int[][] items2 = dateHelper.to2DSeasonArray(items);
        model.addAttribute(SEASON_LIST, items2);
        model.addAttribute(PAGE_TITLE, "League Tables - Archive");

        return "th_tables/archive";
    }
}