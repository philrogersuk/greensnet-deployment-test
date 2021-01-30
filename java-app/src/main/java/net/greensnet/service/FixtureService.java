/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.club.domain.Club;
import net.greensnet.domain.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FixtureService {

    @CacheEvict(value = "lastFixture", allEntries = true)
    void createFixture(int season, long competition, FixtureVenue venue, long altVenue, LocalDateTime timestamp, long opposition,
                       int hendonScore, int oppositionScore, int hendonScore90, int oppositionScore90, int hendonPenalties,
                       int oppositionPenalties, int abandonedMinute, String abandonedReason, boolean notPlayed, int attendance,
                       String report, String author, String matchSponsor, String matchballSponsor, String programmeSponsor,
                       String mascot, String highlightsId, String youtubeId, String hfctvSponsor, String ticketURL);

    List<Fixture> getFixturesBySeason(int season);

    List<Fixture> getFutureTicketedFixtures();

    List<FixtureListMonth> getMonthlyFixturesBySeason(int season);

    List<Fixture> getHomeFixturesBySeason(int season);

    Fixture getFixtureById(long fixtureId, boolean includeApps, boolean includeGoals);

    @Cacheable("lastFixture")
    Fixture getLastFixture();

    Fixture getNextFixture();

    Optional<Fixture> getNextFixture(Fixture fixture);

    Optional<Fixture> getPreviousFixture(Fixture fixture);

    @Deprecated
    Fixture getLastHighlights();

    FixtureVenue getVenueFromString(String parameter);

    FixtureVenue getVenue(long id);

    @CacheEvict(value = "lastFixture", allEntries = true)
    boolean updateFixture(long fixtureId, int season, long competition, FixtureVenue venue, long altVenue,
                          LocalDateTime timeStamp, long opposition, int hendonScore, int oppositionScore, int hendonScore90,
                          int oppositionScore90, int hendonPenalties, int oppositionPenalties, int abandonedMinute,
                          String abandonedReason, boolean notPlayed, int attendance, String report, String author,
                          String matchSponsor, String matchballSponsor, String programmeSponsor, String mascot,
                          String highlightsId, String youtubeId, String hfctvSponsor, String ticketURL);

    @CacheEvict(value = "lastFixture", allEntries = true)
    boolean setTeams(long fixtureId, List<FixtureAppearance> hendonTeam, List<OppositionAppearance> oppositionTeam);

    @CacheEvict(value = "lastFixture", allEntries = true)
    boolean setScorers(long fixtureId, List<FixtureGoal> hendonGoals, List<OppositionGoal> oppositionGoals);

    List<FixtureAppearance> getHendonTeam(long fixtureId);

    List<OppositionAppearance> getOppositionTeam(long fixtureId);

    List<FixtureGoal> getHendonScorers(long fixtureId);

    List<OppositionGoal> getOppositionScorers(long fixtureId);

    List<Integer> getValidSeasons();

    Set<Long> getOpponentIdsForSeason(int season);

    void updateOppositionTeamNames(Club entity);
}
