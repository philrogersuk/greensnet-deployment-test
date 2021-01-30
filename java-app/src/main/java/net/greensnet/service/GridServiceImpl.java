/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.competition.CompetitionService;
import net.greensnet.competition.league.LeagueEntryService;
import net.greensnet.competition.league.LeagueFixtureService;
import net.greensnet.competition.league.TableService;
import net.greensnet.competition.league.domain.LeagueEntry;
import net.greensnet.competition.league.domain.LeagueFixture;
import net.greensnet.domain.Grid;
import net.greensnet.domain.GridRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Phil
 */
@Service
public class GridServiceImpl implements GridService {
    private final CompetitionService competitionService;
    private final LeagueFixtureService leagueFixtureService;
    private final TableService tableService;
    private final LeagueEntryService leagueEntryService;

    @Autowired
    public GridServiceImpl(CompetitionService competitionService,
                           LeagueFixtureService leagueFixtureService,
                           TableService tableService,
                           LeagueEntryService leagueEntryService) {
        this.competitionService = competitionService;
        this.leagueFixtureService = leagueFixtureService;
        this.tableService = tableService;
        this.leagueEntryService = leagueEntryService;
    }

    @Override
    public Grid getFixtureGrid(long competition, int season) {
        List<GridRow> rows = initialiseTable(competition, season);
        processFixtures(rows, competition, season);

        LeagueFixture fixture = leagueFixtureService.getMostRecentFixture(competition, season);
        Grid grid;
        if (null != fixture) {
            grid = Grid.builder()
                    .lastFixture(fixture.getFixtureDate())
                    .season(season)
                    .competitionName(competitionService.getCompetition(competition).getName())
                    .build();
        } else {
            grid = Grid.builder()
                    .season(season)
                    .competitionName(competitionService.getCompetition(competition).getName())
                    .build();
        }

        grid.setRows(rows);
        return grid;
    }

    private List<GridRow> initialiseTable(final long competition, final int season) {
        List<LeagueEntry> entries = leagueEntryService.getLeagueEntries(season, competition);

        List<GridRow> table = new ArrayList<>();
        for (int i = 0; i < entries.size(); i++) {
            LeagueEntry entry = entries.get(i);
            GridRow row = new GridRow();
            row.setClubId(entry.getClubId());
            row.setClubName(entry.getClubName());
            row.setFixtures(new LeagueFixture[entries.size()]);
            table.add(row);
        }
        return table;
    }

    private void processFixtures(List<GridRow> rows, long competition, int season) {
        List<LeagueFixture> fixtures = leagueFixtureService.getFixtures(competition, season);

        for (LeagueFixture fixture : fixtures) {
            GridRow homeTeam = findRowByClub(rows, fixture.getHomeClub().getId());
            int awayTeamIndex = findIndexByClub(rows, fixture.getAwayClub().getId());
            if (Objects.isNull(homeTeam)) {
                continue;
            }
            homeTeam.setFixture(fixture, awayTeamIndex);
        }
    }

    protected static GridRow findRowByClub(List<GridRow> rows, long clubId) {
        for (GridRow row : rows) {
            if (row.getClubId() == clubId) {
                return row;
            }
        }
        return null;
    }

    protected static int findIndexByClub(List<GridRow> rows, long clubId) {
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).getClubId() == clubId) {
                return i;
            }
        }
        return -1;
    }

}
