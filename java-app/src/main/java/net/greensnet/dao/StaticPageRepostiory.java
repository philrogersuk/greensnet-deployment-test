/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.dao;

import net.greensnet.domain.StaticPage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StaticPageRepostiory extends CrudRepository<StaticPage, String> {
    List<StaticPage> findAllByOrderByIdAsc();

}
