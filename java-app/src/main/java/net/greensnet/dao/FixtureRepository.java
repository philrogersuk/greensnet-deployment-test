/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.dao;

import net.greensnet.domain.Fixture;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FixtureRepository extends CrudRepository<Fixture, Long> {
    @Query(value = "SELECT item FROM Fixture item "
            + "INNER JOIN item.hendonTeam AS app INNER JOIN app.player AS player "
            + "INNER JOIN item.competition AS comp "
            + "LEFT JOIN FETCH item.venue "
            + "WHERE player.id = ?1 AND (app.substitute = false OR playerReplaced != -1) "
            + "AND comp.type != 'FRIENDLY' AND item.abandonedMinute = -1 "
            + "ORDER BY item.kickOff ASC")
    List<Fixture> getCompetitiveAppearances(long playerId);

    List<Fixture> getBySeasonOrderByKickOffAsc(int season);

    @Query(value = "SELECT DISTINCT item FROM Fixture item "
            + "WHERE item.kickOff > current_timestamp AND item.ticketURL IS NOT NULL AND "
            + "item.ticketURL != '' ORDER BY item.kickOff ASC")
    List<Fixture> getFutureTicketedFixtures();

    @Query(value = "SELECT item FROM Fixture item "
            + "LEFT JOIN FETCH item.competition AS comp "
            + "LEFT JOIN FETCH item.venue AS v "
            + "WHERE item.season = ?1 AND v.venue = 'HOME' ORDER BY item.kickOff ASC")
    List<Fixture> getHomeFixturesBySeason(int season);

    @Query(value = "SELECT item FROM Fixture item "
            + "LEFT JOIN FETCH item.competition "
            + "LEFT JOIN FETCH item.venue "
            + "WHERE item.id = ?1")
    Optional<Fixture> getFixtureById(long fixtureId);

    @Query(value = "SELECT item FROM Fixture item "
            + "LEFT JOIN FETCH item.competition "
            + "LEFT JOIN FETCH item.venue "
            + "LEFT JOIN FETCH item.hendonGoals AS hgoals "
            + "LEFT JOIN FETCH hgoals.scorer LEFT JOIN FETCH item.oppositionGoals "
            + "WHERE item.id = ?1")
    Optional<Fixture> getFixtureByIdIncludingGoals(long fixtureId);

    @Query(value = "SELECT item FROM Fixture item "
            + "LEFT JOIN FETCH item.competition "
            + "LEFT JOIN FETCH item.venue "
            + "LEFT JOIN FETCH item.hendonTeam AS hteam "
            + "LEFT JOIN FETCH hteam.player AS player LEFT JOIN FETCH player.oldNames LEFT JOIN FETCH item.oppositionTeam"
            + " WHERE item.id = ?1")
    Optional<Fixture> getFixtureByIdIncludingAppearances(long fixtureId);

    @Query(value = "SELECT item FROM Fixture item "
            + "LEFT JOIN FETCH item.competition "
            + "LEFT JOIN FETCH item.venue "
            + "LEFT JOIN FETCH item.hendonTeam AS hteam "
            + "LEFT JOIN FETCH hteam.player AS player LEFT JOIN FETCH player.oldNames LEFT JOIN FETCH item.oppositionTeam"
            + " LEFT JOIN FETCH item.hendonGoals AS hgoals "
            + "LEFT JOIN FETCH hgoals.scorer LEFT JOIN FETCH item.oppositionGoals"
            + " WHERE item.id = ?1")
    Optional<Fixture> getFixtureByIdIncludingAppearancesAndGoals(long fixtureId);

    @Query("SELECT item FROM Fixture item "
            + "LEFT JOIN FETCH item.competition "
            + "LEFT JOIN FETCH item.hendonGoals AS hgoals "
            + "LEFT JOIN FETCH hgoals.scorer AS hscorer LEFT JOIN FETCH hscorer.oldNames "
            + "LEFT JOIN FETCH item.oppositionGoals WHERE item.kickOff < ?1 ORDER BY item.kickOff DESC")
    List<Fixture> getLastFixturesBeforeDate(LocalDateTime date, Pageable pageable);

    @Query("SELECT item FROM Fixture item "
            + "WHERE item.kickOff < ?1 AND item.youtubeId IS NOT NULL "
            + "AND item.youtubeId != '' ORDER BY item.kickOff DESC")
    List<Fixture> getLastFixturesBeforeDateWithHighlights(LocalDateTime date, Pageable pageable);

    @Query("SELECT item FROM Fixture item "
            + "LEFT JOIN FETCH item.competition LEFT JOIN FETCH item.venue "
            + "WHERE item.kickOff > ?1 ORDER BY item.kickOff ASC")
    List<Fixture> getNextFixtures(LocalDateTime date, Pageable pageable);

    @Query("SELECT fix.season from Fixture fix GROUP BY fix.season ORDER BY fix.season ASC")
    List<Integer> getValidSeasons();

    Optional<Fixture> findFirst1ByKickOffGreaterThanOrderByKickOffAsc(LocalDateTime kickOff);

    Optional<Fixture> findFirst1ByKickOffLessThanOrderByKickOffDesc(LocalDateTime kickOff);

    List<Fixture> findFixturesByOpposition(long oppositionId);

    List<Fixture> findFixturesByAlternativeVenue(long alternativeVenueId);
}
