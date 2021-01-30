/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.dao;

import net.greensnet.competition.domain.CompetitionType;
import net.greensnet.domain.FixtureGoal;
import net.greensnet.domain.StaffMember;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FixtureGoalRepository extends CrudRepository<FixtureGoal, Long> {

    @Query("SELECT count(item) FROM FixtureGoal item " +
                "INNER JOIN item.fixture AS fixture " +
                "INNER JOIN fixture.competition AS competition " +
            "WHERE fixture.season = ?1 " +
                "AND item.ownGoal = true " +
                "AND competition.type = ?2 " +
                "AND fixture.abandonedMinute = -1")
    Optional<Integer> getOwnGoals(int season, CompetitionType competitionType);

    @Query("SELECT count(item) FROM FixtureGoal item " +
                "INNER JOIN item.fixture AS fixture " +
                "INNER JOIN fixture.competition AS competition " +
            "WHERE fixture.season = ?1 " +
                "AND item.ownGoal = false " +
                "AND competition.type = ?2 " +
                "AND item.scorer = ?3 " +
                "AND fixture.abandonedMinute = -1")
    Optional<Integer> getGoalsScored(int season, CompetitionType competitionType, StaffMember player);
}
