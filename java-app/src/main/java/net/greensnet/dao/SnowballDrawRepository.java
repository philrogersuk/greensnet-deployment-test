/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.dao;

import net.greensnet.domain.SnowballDraw;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SnowballDrawRepository extends CrudRepository<SnowballDraw, Long> {

    List<SnowballDraw> findAllByOrderByDateDesc();
}
