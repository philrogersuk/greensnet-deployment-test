/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.domain.*;

import java.time.LocalDateTime;
import java.util.List;


public interface YouthTeamService {

    void createFixture(int season, int teamType, String competition, char venue, LocalDateTime timeStamp, String opposition,
                       int hendonScore, int oppositionScore, int hendonScore90, int oppositionScore90, int hendonPenalties,
                       int oppositionPenalties, String scorers);

    void createTable(int season, int teamType, String content);

    TeamTable getTableForTeam(int season, int teamType);

    void updateTable(long id, String content);

    void updateFixture(long fixtureId, int season, int teamType, String competition, char venue, LocalDateTime timeStamp,
                       String opposition, int hendonScore, int oppositionScore, int hendonScore90, int oppositionScore90,
                       int hendonPenalties, int oppositionPenalties, String scorers);

    void deleteFixtureById(long id);

    void deleteNewsItemById(long id);

    void editNewsItem(long id, String headline, String story, LocalDateTime timeStamp);

    List<TeamEntry> getAllEntries();

    List<TeamType> getAllTeams();

    List<TeamEntry> getEntriesBySeason(int season);

    void createTeamEntry(int season, int teamId);

    void createNewsItem(int season, int teamId, String headline, String story, LocalDateTime timeStamp);

    List<TeamNews> getNewsForTeam(int season, int teamType);

    List<TeamFixture> getFixturesForTeam(int season, int teamType);

    TeamType getTeamType(long id);

    TeamFixture getFixtureById(long id);

    TeamNews getNewsItemById(long id);

    Object[][] getTeamMap();
}
