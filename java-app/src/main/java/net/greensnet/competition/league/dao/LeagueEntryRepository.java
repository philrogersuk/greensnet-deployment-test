/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league.dao;

import net.greensnet.competition.league.domain.LeagueEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LeagueEntryRepository extends CrudRepository<LeagueEntryEntity, Long> {
    List<LeagueEntryEntity> findByCompetitionIdAndSeason(Long competition, int season);

    List<LeagueEntryEntity> findByClubIdAndSeason(long club, int season);

    List<LeagueEntry> findByClubId(long club);

    @Query("SELECT item.season FROM LeagueEntryEntity item WHERE item.clubId = ?1 ORDER BY item.season ASC")
    List<Integer> findSeasonsEnteredByClub(long clubId);
}
