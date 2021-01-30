/*
 * Copyright (c) 2008, Phil Rogers
 * All Rights Reserved
 */

package net.greensnet.service;

import net.greensnet.dao.StaticPageRepostiory;
import net.greensnet.domain.StaticPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author Phil
 */

@Service
public class StaticPageServiceImpl implements StaticPageService {

    private final StaticPageRepostiory repository;

    @Autowired
    public StaticPageServiceImpl(StaticPageRepostiory repository) {
        this.repository = repository;
    }


    @Override
    public StaticPage getPage(String id) {
        Optional<StaticPage> optional = repository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public boolean createPage(String pageId, String content, String title) {
        if (null != getPage(pageId)) {
            return false;
        }
        StaticPage page = new StaticPage();
        page.setId(pageId);
        page.setContent(content);
        page.setTitle(title);
        page.setLastUpdated(LocalDateTime.now());
        repository.save(page);
        return true;
    }

    @Override
    public List<StaticPage> getAll() {
        return repository.findAllByOrderByIdAsc();
    }

    @Override
    public boolean deletePage(String pageId) {
        repository.deleteById(pageId);
        return true;
    }

    @Override
    public boolean editPage(String oldId, String newId, String content, String title) {

        Optional<StaticPage> optional = repository.findById(oldId);
        if (!optional.isPresent()) {
            return false;
        }
        optional.get().setId(newId);
        optional.get().setTitle(title);
        optional.get().setContent(content);
        optional.get().setLastUpdated(LocalDateTime.now());
        repository.save(optional.get());
        return true;
    }
}
