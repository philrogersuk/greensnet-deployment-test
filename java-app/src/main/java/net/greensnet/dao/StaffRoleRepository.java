/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.dao;

import net.greensnet.domain.StaffRole;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface StaffRoleRepository extends CrudRepository<StaffRole, Long> {
    Optional<StaffRole> findFirstByName(String name);
}
