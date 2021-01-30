/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.dao;

import net.greensnet.domain.StaffMember;
import net.greensnet.domain.StaffUpdate;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StaffUpdateRepository extends CrudRepository<StaffUpdate, Long> {

    Optional<StaffUpdate> findByStaffMember(StaffMember id);
}
