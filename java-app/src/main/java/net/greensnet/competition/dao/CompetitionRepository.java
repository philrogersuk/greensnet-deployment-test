/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.dao;

import net.greensnet.competition.domain.CompetitionType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompetitionRepository extends CrudRepository<CompetitionEntity, Long> {
    List<CompetitionEntity> findByType(CompetitionType type);
}
