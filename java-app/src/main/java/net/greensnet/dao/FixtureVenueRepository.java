/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.dao;

import net.greensnet.domain.FixtureVenue;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FixtureVenueRepository extends CrudRepository<FixtureVenue, Long> {
    Optional<FixtureVenue> findByVenue(String name);
}
