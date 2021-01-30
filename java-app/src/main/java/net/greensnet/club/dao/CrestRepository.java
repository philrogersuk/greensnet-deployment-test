/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.club.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface CrestRepository extends CrudRepository<CrestEntity, Long> {

    List<CrestEntity> findAllByClub_IdEqualsOrderByFirstSeasonDesc(long club);


    List<CrestEntity> findAllByClub_IdInOrderByFirstSeasonDesc(Set<Long> club);
}
