/*
 * Copyright (c) 2008, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.domain.StaticPage;

import java.util.List;

public interface StaticPageService {

    StaticPage getPage(String id);

    boolean createPage(String pageId, String content, String title);

    List<StaticPage> getAll();

    boolean deletePage(String pageId);

    boolean editPage(String oldId, String newId, String content, String title);
}
