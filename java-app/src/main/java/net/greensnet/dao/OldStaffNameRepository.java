/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.dao;

import net.greensnet.domain.OldStaffName;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OldStaffNameRepository extends CrudRepository<OldStaffName, Long> {
    List<OldStaffName> findAllByLastNameStartsWithOrderByLastNameAscFirstNameAsc(char character);
}
