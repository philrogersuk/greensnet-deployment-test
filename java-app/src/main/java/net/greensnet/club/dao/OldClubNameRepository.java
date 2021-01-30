/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.club.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OldClubNameRepository extends CrudRepository<ClubNameEntity, Long> {

    @Query(value = "SELECT names FROM ClubEntity club "
            + "INNER JOIN club.oldNames AS names "
            + "WHERE club.id = ?1")
    List<ClubNameEntity> findByClub(long clubId);
}
