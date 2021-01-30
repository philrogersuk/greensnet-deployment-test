/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition;

import net.greensnet.competition.dao.CompetitionEntity;
import net.greensnet.competition.domain.Competition;
import net.greensnet.competition.domain.CompetitionType;

import java.util.List;

public interface CompetitionService {

    Competition getCompetition(long id);

    @Deprecated
    CompetitionEntity getCompetitionEntity(long id);

    void createCompetition(String name, String shortCode, CompetitionType type);

    List<Competition> getAllCompetitions();

    List<Competition> getLeagueCompetitions();
}
