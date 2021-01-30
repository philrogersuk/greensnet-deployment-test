/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.club.domain.Club;
import net.greensnet.competition.CompetitionService;
import net.greensnet.dao.*;
import net.greensnet.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toSet;

/**
 *
 * @author Phil
 */
@Service
public class FixtureServiceImpl implements FixtureService {

    private final CompetitionService competitionService;
    //private final ClubService clubService;
    private final TwitterService twitterService;
    private final FixtureRepository fixtureRepository;
    private final FixtureVenueRepository fixtureVenueRepository;
    private final FixtureAppearanceRepository fixtureAppearanceRepository;
    private final OppositionAppearanceRepository oppositionAppearanceRepository;
    private final FixtureGoalRepository fixtureGoalRepository;
    private final OppositionGoalRepository oppositionGoalRepository;

    @Autowired
    public FixtureServiceImpl(CompetitionService competitionService,
    //                          ClubService clubService,
                              TwitterService twitterService,
                              FixtureRepository fixtureRepository,
                              FixtureVenueRepository fixtureVenueRepository,
                              FixtureAppearanceRepository fixtureAppearanceRepository,
                              OppositionAppearanceRepository oppositionAppearanceRepository,
                              FixtureGoalRepository fixtureGoalRepository,
                              OppositionGoalRepository oppositionGoalRepository) {
        this.competitionService = competitionService;
    //    this.clubService = clubService;
        this.twitterService = twitterService;
        this.fixtureRepository = fixtureRepository;
        this.fixtureVenueRepository = fixtureVenueRepository;
        this.fixtureAppearanceRepository = fixtureAppearanceRepository;
        this.oppositionAppearanceRepository = oppositionAppearanceRepository;
        this.fixtureGoalRepository = fixtureGoalRepository;
        this.oppositionGoalRepository = oppositionGoalRepository;
    }

    @Override
    @CacheEvict(value = "lastFixture", allEntries = true)
    public void createFixture(int season, long competition, FixtureVenue venue, long altVenue, LocalDateTime timestamp,
                              long opposition, int hendonScore, int oppositionScore, int hendonScore90, int oppositionScore90,
                              int hendonPenalties, int oppositionPenalties, int abandonedMinute, String abandonedReason,
                              boolean notPlayed, int attendance, String report, String author, String matchSponsor,
                              String matchballSponsor, String programmeSponsor, String mascot, String highlightsId,
                              String youtubeId, String hfctvSponsor, String ticketURL) {
        Fixture fixture = new Fixture();
        populateFixture(season, competition, venue, altVenue, timestamp, opposition, hendonScore, oppositionScore,
                hendonScore90, oppositionScore90, hendonPenalties, oppositionPenalties, abandonedMinute,
                abandonedReason, notPlayed, attendance, report, author, matchSponsor, matchballSponsor,
                programmeSponsor, mascot, fixture, highlightsId, youtubeId, hfctvSponsor, ticketURL);
        fixtureRepository.save(fixture);
    }

    void populateFixture(int season, long competition, FixtureVenue venue, long altVenue, LocalDateTime timestamp,
                         long opposition, int hendonScore, int oppositionScore, int hendonScore90, int oppositionScore90,
                         int hendonPenalties, int oppositionPenalties, int abandonedMinute, String abandonedReason,
                         boolean notPlayed, int attendance, String report, String author, String matchSponsor,
                         String matchballSponsor, String programmeSponsor, String mascot, Fixture fixture, String highlightsId,
                         String youtubeId, String hfctvSponsor, String ticketURL) {
        fixture.setSeason(season);
        fixture.setKickOff(timestamp);
        fixture.setCompetition(competitionService.getCompetitionEntity(competition));
        fixture.setVenue(venue);
        fixture.setAlternativeVenue(altVenue);
        fixture.setOpposition(opposition);
        fixture.setHendonScore(hendonScore);
        fixture.setOppositionScore(oppositionScore);
        fixture.setHendonScore90(hendonScore90);
        fixture.setOppositionScore90(oppositionScore90);
        fixture.setHendonPenalties(hendonPenalties);
        fixture.setOppositionPenalties(oppositionPenalties);
        fixture.setAbandonedMinute(abandonedMinute);
        fixture.setAbandonedReason(abandonedReason);
        fixture.setNotPlayed(notPlayed);
        fixture.setAttendance(attendance);
        fixture.setReport(report);
        fixture.setAuthor(author);
        fixture.setMatchSponsor(matchSponsor);
        fixture.setMatchballSponsor(matchballSponsor);
        fixture.setProgrammeSponsor(programmeSponsor);
        fixture.setMascot(mascot);
        fixture.setHighlightsId(highlightsId);
        fixture.setYoutubeId(youtubeId);
        fixture.setHfctvSponsor(hfctvSponsor);
        fixture.setTicketURL(ticketURL);
    }

    @Override
    public List<Fixture> getFixturesBySeason(int season) {
        List<Fixture> results = fixtureRepository.getBySeasonOrderByKickOffAsc(season);
        moveTbaToEnd(results);
        return results;
    }

    @Override
    public List<Fixture> getFutureTicketedFixtures() {
        List<Fixture> results = fixtureRepository.getFutureTicketedFixtures();
        moveTbaToEnd(results);
        return results;
    }

    private static void moveTbaToEnd(List<Fixture> results) {
        for (int i = results.size() - 1; i >= 0; i--) {
            if (null == results.get(i).getKickOff()) {
                Fixture fixture = results.remove(i);
                results.add(fixture);
            }
        }
    }

    @Override
    public List<FixtureListMonth> getMonthlyFixturesBySeason(int season) {
        List<Fixture> seasonResults = getFixturesBySeason(season);
        if (null == seasonResults) {
            return null;
        }

        List<FixtureListMonth> results = new ArrayList<>();
        for (Fixture fixture : seasonResults) {
            // get month & year
            String month;
            String year;
            if (null == fixture.getKickOff()) {
                month = "Unknown";
                year = "";
            } else {
                month = fixture.getKickOff().format(DateTimeFormatter.ofPattern("MMMM"));
                year = fixture.getKickOff().format(DateTimeFormatter.ofPattern("yyyy"));
            }
            // check if exists
            boolean found = false;
            for (FixtureListMonth list : results) {
                if (month.equals(list.getMonth()) && year.equals(list.getYear())) {
                    List<Fixture> fixtures = list.getFixtures();
                    fixtures.add(fixture);
                    list.setFixtures(fixtures);
                    found = true;
                }
            }
            if (!found) {
                FixtureListMonth list = new FixtureListMonth();
                list.setMonth(month);
                list.setYear(year);
                List<Fixture> fixtures = new ArrayList<>();
                fixtures.add(fixture);
                list.setFixtures(fixtures);
                results.add(list);
            }
        }

        return results;
    }

    @Override
    public List<Fixture> getHomeFixturesBySeason(int season) {
        List<Fixture> results = fixtureRepository.getHomeFixturesBySeason(season);
        moveTbaToEnd(results);
        return results;
    }

    @Override
    public Fixture getFixtureById(long fixtureId, boolean includeApps, boolean includeGoals) {
        if (!includeApps && !includeGoals) {
            return fixtureRepository.getFixtureById(fixtureId).orElse(null);
        } else if (!includeApps && includeGoals) {
            return fixtureRepository.getFixtureByIdIncludingGoals(fixtureId).orElse(null);
        } else if (includeApps && !includeGoals) {
            return fixtureRepository.getFixtureByIdIncludingAppearances(fixtureId).orElse(null);
        }
        return fixtureRepository.getFixtureByIdIncludingAppearancesAndGoals(fixtureId).orElse(null);
    }

    @Override
    @Cacheable("lastFixture")
    public Fixture getLastFixture() {
        List<Fixture> fixtures = fixtureRepository.getLastFixturesBeforeDate(LocalDateTime.now(), PageRequest.of(0, 1));
        if (fixtures.isEmpty()) {
            return null;
        }
        return fixtures.get(0);
    }

    @Override
    public Fixture getLastHighlights() {
        List<Fixture> fixtures = fixtureRepository.getLastFixturesBeforeDateWithHighlights(LocalDateTime.now(), PageRequest.of(0, 1));
        if (fixtures.isEmpty()) {
            return null;
        }
        return fixtures.get(0);
    }

    @Override
    public Fixture getNextFixture() {
        List<Fixture> fixtures = fixtureRepository.getNextFixtures(LocalDateTime.now(), PageRequest.of(0, 1));
        if (fixtures.isEmpty()) {
            return null;
        }
        return fixtures.get(0);
    }

    @Override
    public Optional<Fixture> getNextFixture(Fixture fixture) {
        if (isNull(fixture.getKickOff())) {
            return Optional.ofNullable(null);
        }
        return fixtureRepository.findFirst1ByKickOffGreaterThanOrderByKickOffAsc(fixture.getKickOff());

    }

    @Override
    public Optional<Fixture> getPreviousFixture(Fixture fixture) {
        if (isNull(fixture.getKickOff())) {
            return Optional.ofNullable(null);
        }
        return fixtureRepository.findFirst1ByKickOffLessThanOrderByKickOffDesc(fixture.getKickOff());
    }

    @Override
    public FixtureVenue getVenueFromString(String name) {
        Optional<FixtureVenue> venue = fixtureVenueRepository.findByVenue(name);
        return venue.orElse(null);
    }

    @Override
    public FixtureVenue getVenue(long id) {
        Optional<FixtureVenue> venue = fixtureVenueRepository.findById(id);
        return venue.orElse(null);
    }

    @Override
    @CacheEvict(value = "lastFixture", allEntries = true)
    public boolean updateFixture(long fixtureId, int season, long competition, FixtureVenue venue, long altVenue,
                                 LocalDateTime timeStamp, long opposition, int hendonScore, int oppositionScore, int hendonScore90,
                                 int oppositionScore90, int hendonPenalties, int oppositionPenalties, int abandonedMinute,
                                 String abandonedReason, boolean notPlayed, int attendance, String report, String author,
                                 String matchSponsor, String matchballSponsor, String programmeSponsor, String mascot, String highlightsId,
                                 String youtubeId, String hfctvSponsor, String ticketURL) {

        Fixture fixture = getFixtureById(fixtureId, true, true);
        if (tweetHighlights(highlightsId, youtubeId, fixture)) {
            String scoreline;
            if (fixture.getVenueAsInitial() == 'A') {
                scoreline = fixture.getOppositionName() + " " + oppositionScore + "-" + hendonScore
                        + " Hendon";
            } else {
                scoreline = "Hendon " + hendonScore + "-" + oppositionScore + " "
                        + fixture.getOppositionName();
            }

            twitterService.tweet("Highlights: " + scoreline,
                    "http://www.hendonfc.net/Reports?id=" + fixtureId + "#highlights");
        }
        populateFixture(season, competition, venue, altVenue, timeStamp, opposition, hendonScore, oppositionScore,
                hendonScore90, oppositionScore90, hendonPenalties, oppositionPenalties, abandonedMinute,
                abandonedReason, notPlayed, attendance, report, author, matchSponsor, matchballSponsor,
                programmeSponsor, mascot, fixture, highlightsId, youtubeId, hfctvSponsor, ticketURL);
        fixtureRepository.save(fixture);
        return true;
    }

    boolean tweetHighlights (String highlightsId, String youtubeId, Fixture fixture) {
        if ((null != fixture.getHighlightsId() && !"".equals(fixture.getHighlightsId())) ||
                (null != fixture.getYoutubeId() && !"".equals(fixture.getYoutubeId()))) {
            return false;
        }
        return !((null == highlightsId || "".equals(highlightsId)) && (null == youtubeId || "".equals(youtubeId)));
    }

    @Override
    @CacheEvict(value = "lastFixture", allEntries = true)
    public boolean setTeams(long fixtureId, List<FixtureAppearance> hendonTeam,
                            List<OppositionAppearance> oppositionTeam) {
        Fixture fixture = getFixtureById(fixtureId, true, false);

        handleHendonTeam(hendonTeam, fixture);
        handleOppositionTeam(oppositionTeam, fixture);

        fixtureRepository.save(fixture);
        return true;
    }

    private void handleOppositionTeam(List<OppositionAppearance> oppositionTeam, Fixture fixture) {
        if (null == fixture.getOppositionTeam()) {
            fixture.setOppositionTeam(new HashSet<>());
        }
        OppositionAppearance[] oteam = fixture.getOppositionTeam().toArray(new OppositionAppearance[0]);

        if (null != oppositionTeam) {
            for (int i = 0; i < oppositionTeam.size(); i++) {
                if (i >= fixture.getOppositionTeam().size()) {
                    OppositionAppearance app = oppositionTeam.get(i);
                    app.setFixture(fixture);
                    oppositionAppearanceRepository.save(app);
                    fixture.getOppositionTeam().add(app);
                } else {
                    OppositionAppearance app = oteam[i];
                    OppositionAppearance tempApp = oppositionTeam.get(i);
                    app.setFirstName(tempApp.getFirstName());
                    app.setLastName(tempApp.getLastName());
                    app.setPlayerReplaced(tempApp.getPlayerReplaced());
                    app.setSubstitute(tempApp.isSubstitute());
                    app.setRedCardMinute(tempApp.getRedCardMinute());
                    app.setShirtNumber(tempApp.getShirtNumber());
                    app.setSubstitutionMinute(tempApp.getSubstitutionMinute());
                    app.setYellowCardMinute(tempApp.getYellowCardMinute());
                    oppositionAppearanceRepository.save(app);
                }
            }
            for (int i = oppositionTeam.size(); i < fixture.getOppositionTeam().size(); i++) {
                OppositionAppearance app = oteam[i];
                fixture.getOppositionTeam().remove(oteam[i]);
                oppositionAppearanceRepository.delete(app);
            }
        }
    }

    private void handleHendonTeam(List<FixtureAppearance> hendonTeam, Fixture fixture) {
        if (null == fixture.getHendonTeam()) {
            fixture.setHendonTeam(new HashSet<>());
        }

        FixtureAppearance[] hteam = fixture.getHendonTeam().toArray(new FixtureAppearance[0]);

        if (null != hendonTeam) {
            for (int i = 0; i < hendonTeam.size(); i++) {
                if (i >= fixture.getHendonTeam().size()) {
                    FixtureAppearance app = hendonTeam.get(i);
                    app.setFixture(fixture);
                    fixtureAppearanceRepository.save(app);
                    fixture.getHendonTeam().add(app);
                } else {
                    FixtureAppearance app = hteam[i];
                    FixtureAppearance tempApp = hendonTeam.get(i);
                    app.setFixture(fixture);
                    app.setPlayer(tempApp.getPlayer());
                    app.setPlayerReplaced(tempApp.getPlayerReplaced());
                    app.setSubstitute(tempApp.isSubstitute());
                    app.setRedCardMinute(tempApp.getRedCardMinute());
                    app.setShirtNumber(tempApp.getShirtNumber());
                    app.setSubstitutionMinute(tempApp.getSubstitutionMinute());
                    app.setYellowCardMinute(tempApp.getYellowCardMinute());
                    fixtureAppearanceRepository.save(app);
                }
            }
            for (int i = hendonTeam.size(); i < fixture.getHendonTeam().size(); i++) {
                FixtureAppearance app = hteam[i];
                fixture.getHendonTeam().remove(hteam[i]);
                fixtureAppearanceRepository.delete(app);
            }
        }
    }

    @Override
    @CacheEvict(value = "lastFixture", allEntries = true)
    public boolean setScorers(long fixtureId, List<FixtureGoal> hendonGoals, List<OppositionGoal> oppositionGoals) {
        Fixture fixture = getFixtureById(fixtureId, false, true);

        handleHendonScorers(hendonGoals, fixture);
        handleOppositionScorers(oppositionGoals, fixture);

        fixtureRepository.save(fixture);
        return true;
    }

    private void handleHendonScorers(List<FixtureGoal> hendonGoals, Fixture fixture) {
        if (null == fixture.getHendonGoals()) {
            fixture.setHendonGoals(new HashSet<>());
        }
        FixtureGoal[] hgoals = fixture.getHendonGoals().toArray(new FixtureGoal[0]);

        if (null != hendonGoals) {
            for (int i = 0; i < hendonGoals.size(); i++) {
                if (i >= fixture.getHendonGoals().size()) {
                    FixtureGoal goal = hendonGoals.get(i);
                    goal.setFixture(fixture);
                    fixtureGoalRepository.save(goal);
                    fixture.getHendonGoals().add(goal);
                } else {
                    FixtureGoal goal = hgoals[i];
                    goal.setFixture(fixture);
                    FixtureGoal tempGoal = hendonGoals.get(i);
                    goal.setScorer(tempGoal.getScorer());
                    goal.setOwnGoal(tempGoal.isOwnGoal());
                    goal.setPenalty(tempGoal.isPenalty());
                    goal.setMinuteScored(tempGoal.getMinuteScored());
                    fixtureGoalRepository.save(goal);
                }
            }
            for (int i = hendonGoals.size(); i < fixture.getHendonGoals().size(); i++) {
                fixture.getHendonGoals().remove(hgoals[i]);
                fixtureRepository.save(fixture);
                fixtureGoalRepository.delete(hgoals[i]);
            }
        }
    }

    private void handleOppositionScorers(List<OppositionGoal> oppositionGoals, Fixture fixture) {
        if (null == fixture.getOppositionGoals()) {
            fixture.setOppositionGoals(new HashSet<>());
        }

        OppositionGoal[] ogoals = fixture.getOppositionGoals().toArray(new OppositionGoal[0]);
        if (null != oppositionGoals) {
            for (int i = 0; i < oppositionGoals.size(); i++) {
                if (i >= fixture.getOppositionGoals().size()) {
                    OppositionGoal goal = oppositionGoals.get(i);
                    goal.setFixture(fixture);
                    oppositionGoalRepository.save(goal);
                    fixture.getOppositionGoals().add(goal);
                } else {
                    OppositionGoal goal = ogoals[i];
                    goal.setFixture(fixture);
                    OppositionGoal tempGoal = oppositionGoals.get(i);
                    goal.setFirstName(tempGoal.getFirstName());
                    goal.setLastName(tempGoal.getLastName());
                    goal.setOwnGoal(tempGoal.isOwnGoal());
                    goal.setPenalty(tempGoal.isPenalty());
                    goal.setMinuteScored(tempGoal.getMinuteScored());
                    oppositionGoalRepository.save(goal);
                }
            }
            for (int i = oppositionGoals.size(); i < fixture.getOppositionGoals().size(); i++) {
                fixture.getOppositionGoals().remove(ogoals[i]);
                fixtureRepository.save(fixture);
                oppositionGoalRepository.delete(ogoals[i]);
            }
        }
    }

    @Override
    public List<FixtureAppearance> getHendonTeam(long fixtureId) {
        Fixture fixture = getFixtureById(fixtureId, true, false);
        return new ArrayList<>(fixture.getHendonTeam());
    }

    @Override
    public List<OppositionAppearance> getOppositionTeam(long fixtureId) {
        Fixture fixture = getFixtureById(fixtureId, true, false);
        return new ArrayList<>(fixture.getOppositionTeam());
    }

    @Override
    public List<FixtureGoal> getHendonScorers(long fixtureId) {
        Fixture fixture = getFixtureById(fixtureId, false, true);
        return new ArrayList<>(fixture.getHendonGoals());
    }

    @Override
    public List<OppositionGoal> getOppositionScorers(long fixtureId) {
        Fixture fixture = getFixtureById(fixtureId, false, true);
        return new ArrayList<>(fixture.getOppositionGoals());
    }

    @Override
    public List<Integer> getValidSeasons() {
        return fixtureRepository.getValidSeasons();
    }

    @Override
    public Set<Long> getOpponentIdsForSeason(int season) {
        return getFixturesBySeason(season).stream()
                .map(fixture -> fixture.getOpposition())
                .collect(toSet());
    }

    @Override
    public void updateOppositionTeamNames(Club club) {
        List<Fixture> fixtures = fixtureRepository.findFixturesByOpposition(club.getId());

        for(Fixture fixture : fixtures) {
            fixture.setOppositionName(club.getNameForSeason(fixture.getSeason()));
        }

        fixtureRepository.saveAll(fixtures);

        fixtures = fixtureRepository.findFixturesByAlternativeVenue(club.getId());

        for(Fixture fixture : fixtures) {
            fixture.setAlternativeVenueName(club.getNameForSeason(fixture.getSeason()));
        }

        fixtureRepository.saveAll(fixtures);
    }
}
