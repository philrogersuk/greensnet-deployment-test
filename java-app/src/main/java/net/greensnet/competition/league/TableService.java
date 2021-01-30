/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league;

import net.greensnet.competition.league.domain.LeagueTable;

import java.util.List;

public interface TableService {
    List<LeagueTable> getTables(int season);

    LeagueTable getTable(long competition, int season);

    List<Integer> getValidSeasons();
}
