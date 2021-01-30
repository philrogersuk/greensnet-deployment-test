/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LeagueFixtureRepository extends CrudRepository<LeagueFixtureEntity, Long> {

    List<LeagueFixtureEntity> findByCompetition_IdAndSeason(long competitionId, int season);

    Optional<LeagueFixtureEntity> findFirstByCompetition_IdAndSeasonOrderByFixtureDateDesc(long competitionId, int season);
}
