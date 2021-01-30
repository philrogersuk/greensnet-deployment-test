/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LeagueTableNoteRepository extends CrudRepository<LeagueTableNoteEntity, Long> {

    List<LeagueTableNoteEntity> findByCompetitionIdAndSeason(long competitionId, int season);

}
