/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.dao;

import net.greensnet.competition.domain.CompetitionType;
import net.greensnet.domain.FixtureAppearance;
import net.greensnet.domain.StaffMember;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FixtureAppearanceRepository extends CrudRepository<FixtureAppearance, Long> {

    @Query("SELECT COUNT(item) FROM FixtureAppearance item " +
                "INNER JOIN item.fixture AS fixture " +
                "INNER JOIN fixture.competition AS competition " +
            "WHERE fixture.season = ?1 AND item.substitute = true " +
                "AND competition.type = ?2 " +
                "AND item.player = ?3 " +
                "AND item.playerReplaced != -1 " +
                "AND (fixture.abandonedMinute > 90 " +
                "OR fixture.abandonedMinute = -1)")
    Optional<Integer> getSubAppearances(int season, CompetitionType compType, StaffMember player);

    @Query("SELECT count(item) FROM FixtureAppearance item " +
                "INNER JOIN item.fixture AS fixture " +
                "INNER JOIN fixture.competition AS competition " +
            "WHERE fixture.season = ?1 " +
                "AND item.substitute = false " +
                "AND competition.type = ?2 " +
                "AND item.player = ?3 " +
                "AND (fixture.abandonedMinute > 90 " +
                "OR fixture.abandonedMinute = -1)")
    Optional<Integer> getStartAppearances(int season, CompetitionType compType, StaffMember player);
}
