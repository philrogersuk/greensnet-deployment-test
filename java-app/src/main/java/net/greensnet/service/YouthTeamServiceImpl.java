/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import com.google.common.collect.Lists;
import net.greensnet.dao.*;
import net.greensnet.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author Phil
 */
@Service
public class YouthTeamServiceImpl implements YouthTeamService {

    private final TeamEntryRepository teamEntryRepository;
    private final TeamNewsRepository teamNewsRepository;
    private final TeamTypeRepository teamTypeRepository;
    private final TeamFixtureRepository teamFixtureRepository;
    private final TeamTableRepository teamTableRepository;

    @Autowired
    private YouthTeamServiceImpl (TeamEntryRepository teamEntryRepository,
                                  TeamNewsRepository teamNewsRepository,
                                  TeamTypeRepository teamTypeRepository,
                                  TeamFixtureRepository teamFixtureRepository,
                                  TeamTableRepository teamTableRepository) {
        this.teamEntryRepository = teamEntryRepository;
        this.teamNewsRepository = teamNewsRepository;
        this.teamTypeRepository = teamTypeRepository;
        this.teamFixtureRepository = teamFixtureRepository;
        this.teamTableRepository = teamTableRepository;
    }

    @Override
    public void createFixture(int season, int teamType, String competition, char venue, LocalDateTime timeStamp,
                              String opposition, int hendonScore, int oppositionScore, int hendonScore90, int oppositionScore90,
                              int hendonPenalties, int oppositionPenalties, String scorers) {
        TeamType team = getTeamType(teamType);
        TeamFixture fixture = new TeamFixture();
        populateFixture(season, competition, venue, timeStamp, opposition, hendonScore, oppositionScore, hendonScore90,
                oppositionScore90, hendonPenalties, oppositionPenalties, scorers, team, fixture);
        teamFixtureRepository.save(fixture);
    }

    private void populateFixture(int season, String competition, char venue, LocalDateTime timeStamp, String opposition,
                                 int hendonScore, int oppositionScore, int hendonScore90, int oppositionScore90, int hendonPenalties,
                                 int oppositionPenalties, String scorers, TeamType team, TeamFixture fixture) {
        fixture.setSeason(season);
        fixture.setTeam(team);
        fixture.setKickOff(timeStamp);
        fixture.setCompetition(competition);
        fixture.setVenue(venue);
        fixture.setOpposition(opposition);
        fixture.setHendonScore(hendonScore);
        fixture.setOppositionScore(oppositionScore);
        fixture.setHendonScore90(hendonScore90);
        fixture.setOppositionScore90(oppositionScore90);
        fixture.setHendonPenalties(hendonPenalties);
        fixture.setOppositionPenalties(oppositionPenalties);
        fixture.setHendonGoals(scorers);
    }

    @Override
    public void createTable(int season, int teamType, String content) {
        TeamType team = getTeamType(teamType);
        TeamTable table = new TeamTable();
        table.setSeason(season);
        table.setTeam(team);
        table.setContent(content);
        teamTableRepository.save(table);
    }

    @Override
    public TeamTable getTableForTeam(int season, int teamType) {
        TeamType team = getTeamType(teamType);
        return teamTableRepository.findBySeasonAndTeam(season, team);
    }

    @Override
    public void updateTable(long id, String content) {
        Optional<TeamTable> table = teamTableRepository.findById(id);
        if (table.isPresent()) {
            table.get().setContent(content);
            teamTableRepository.save(table.get());
        }
    }

    @Override
    public void updateFixture(long id, int season, int teamType, String competition, char venue, LocalDateTime timeStamp,
                              String opposition, int hendonScore, int oppositionScore, int hendonScore90, int oppositionScore90,
                              int hendonPenalties, int oppositionPenalties, String scorers) {
        TeamType team = getTeamType(teamType);
        TeamFixture fixture = getFixtureById(id);
        if (Objects.isNull(fixture)) {
            return;
        }
        populateFixture(season, competition, venue, timeStamp, opposition, hendonScore, oppositionScore,
                hendonScore90, oppositionScore90, hendonPenalties, oppositionPenalties, scorers, team, fixture);
        teamFixtureRepository.save(fixture);
    }

    @Override
    public void deleteFixtureById(long id) {
        teamFixtureRepository.deleteById(id);
    }

    @Override
    public void deleteNewsItemById(long id) {
        teamNewsRepository.deleteById(id);
    }

    @Override
    public void editNewsItem(long id, String headline, String story, LocalDateTime timeStamp) {
        TeamNews item = getNewsItemById(id);
        if (Objects.isNull(item)) {
            return;
        }
        item.setItem(story);
        if (null == timeStamp) {
            item.setTimeOfRelease(LocalDateTime.now());
        } else {
            item.setTimeOfRelease(timeStamp);
        }
        teamNewsRepository.save(item);
    }

    @Override
    public List<TeamEntry> getAllEntries() {
        return Lists.newArrayList(teamEntryRepository.findAll());
    }

    @Override
    public List<TeamType> getAllTeams() {
        return Lists.newArrayList(teamTypeRepository.findAll());
    }

    @Override
    public List<TeamEntry> getEntriesBySeason(int season) {
        return teamEntryRepository.findBySeason(season);
    }

    @Override
    public void createTeamEntry(int season, int teamId) {
        TeamType team = getTeamType(teamId);
        TeamEntry item = new TeamEntry();
        item.setSeason(season);
        item.setTeam(team);
        teamEntryRepository.save(item);
    }

    @Override
    public void createNewsItem(int season, int teamId, String headline, String story, LocalDateTime timeStamp) {
        TeamType team = getTeamType(teamId);
        TeamNews item = new TeamNews();
        item.setSeason(season);
        item.setTeam(team);
        item.setHeadline(headline);
        item.setItem(story);
        item.setTimeOfRelease(timeStamp);
        teamNewsRepository.save(item);
    }

    @Override
    public List<TeamNews> getNewsForTeam(int season, int teamType) {
        TeamType type = getTeamType(teamType);
        return teamNewsRepository.findBySeasonAndTeamOrderByTimeOfReleaseDesc(season, type);
    }

    @Override
    public List<TeamFixture> getFixturesForTeam(int season, int teamType) {
        TeamType type = getTeamType(teamType);
        return teamFixtureRepository.findBySeasonAndTeamOrderByKickOffDesc(season, type);
    }

    @Override
    public TeamType getTeamType(long id) {
        Optional<TeamType> type = teamTypeRepository.findById(id);
        return type.orElse(null);
    }

    @Override
    public TeamFixture getFixtureById(long id) {
        Optional<TeamFixture> type = teamFixtureRepository.findById(id);
        return type.orElse(null);
    }

    @Override
    public TeamNews getNewsItemById(long id) {
        Optional<TeamNews> type = teamNewsRepository.findById(id);
        return type.orElse(null);
    }

    @Override
    public Object[][] getTeamMap() {
        List<TeamEntry> results = teamEntryRepository.findAllSortedBySeasonAndName();
        return convertTo2dArray(results);
    }

    private Object[][] convertTo2dArray(List<TeamEntry> results) {
        int lastSeason = (results.get(0)).getSeason();
        int firstSeason = (results.get(results.size() - 1)).getSeason();
        int currentSeason;
        int numRows = 2 * (lastSeason / 5 - firstSeason / 5) + 2;
        Object[][] array = new Object[numRows][5];
        for (int x = 0; x < numRows; x = x + 2) {
            for (int y = 0; y < 5; y++) {
                currentSeason = ((lastSeason / 5) * 5) + y - (x / 2 * 5);
                if (currentSeason >= firstSeason && currentSeason <= lastSeason) {
                    array[x][y] = currentSeason;
                    array[x + 1][y] = new ArrayList<>();
                }
            }
        }
        for (TeamEntry entry : results) {
            int x = (lastSeason / 5 - entry.getSeason() / 5) * 2 + 1;
            int y = entry.getSeason() % 5;
            ((ArrayList<TeamEntry>)(array[x][y])).add(entry);
        }
        return array;
    }
}
