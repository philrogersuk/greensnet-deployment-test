/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.club.dao;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClubRepository extends CrudRepository<ClubEntity, Long> {
    List<ClubEntity> findByNameStartingWithOrderByName(char letter);
}
