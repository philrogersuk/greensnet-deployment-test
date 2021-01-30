/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.dao.StaticPageRepostiory;
import net.greensnet.domain.StaticPage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional(transactionManager = "transactionManager")
@Commit
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class StaticPageServiceImplTest {

    @Autowired
    private StaticPageRepostiory repository;
    private StaticPageService manager;

    @BeforeEach
    public void setUp() {
        manager = new StaticPageServiceImpl(repository);
    }

    @Transactional
    @Test
    public void testWriteAndRetrieveItem() {
        manager.createPage("ID", "Content", "Title");
        StaticPage item = manager.getPage("ID");
        assertEquals("ID", item.getId());
        assertEquals("Content", item.getContent());
        assertEquals("Title", item.getTitle());
    }

    @Transactional
    @Test
    public void testPageNotExists() {
        assertNull(manager.getPage("ID"));
    }

    @Transactional
    @Test
    public void testWriteIDExists() {
        assertTrue(manager.createPage("ID", "Content", "Title"));
        assertFalse(manager.createPage("ID", "Content2", "Title"));
    }

    @Transactional
    @Test
    public void testGetAll() {
        manager.createPage("ID", "Content", "Title");
        manager.createPage("ID2", "Content2", "Title");
        manager.createPage("ID3", "Content3", "Title");
        manager.createPage("ID4", "Content4", "Title");
        List<StaticPage> items = manager.getAll();
        assertEquals(4, items.size());
    }

    @Transactional
    @Test
    public void testDelete() {
        manager.createPage("ID", "Content", "Title");
        manager.createPage("ID2", "Content2", "Title");
        manager.createPage("ID3", "Content3", "Title");
        manager.createPage("ID4", "Content4", "Title");
        assertTrue(manager.deletePage("ID3"));
        List<StaticPage> items = manager.getAll();
        assertEquals(3, items.size());
    }

    @Transactional
    @Test
    public void testEdit() throws InterruptedException {
        manager.createPage("ID", "Content", "Title");
        StaticPage originalPage = manager.getPage("ID");
        LocalDateTime originalTime = originalPage.getLastUpdated();
        Thread.sleep(10);
        manager.editPage("ID", "ID", "Some other Content", "Another title");
        StaticPage newPage = manager.getPage("ID");
        assertEquals("ID", newPage.getId());
        assertEquals("Some other Content", newPage.getContent());
        assertEquals("Another title", newPage.getTitle());
        assertNotEquals(newPage.getLastUpdated(), originalTime);
    }
}
