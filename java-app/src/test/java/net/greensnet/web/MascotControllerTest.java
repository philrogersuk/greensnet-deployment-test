/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import com.google.common.collect.Lists;
import net.greensnet.domain.Fixture;
import net.greensnet.domain.StaticPage;
import net.greensnet.service.SponsorService;
import net.greensnet.service.StaticPageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class MascotControllerTest {
    private static final String EXPECTED_VIEW_NAME = "th_mascots";

    @Mock
    private StaticPageService staticPageService;
    @Mock
    private SponsorService sponsorService;

    private MascotController controller;
    private Model model;

    @BeforeEach
    public void setup() {
        controller = new MascotController(staticPageService, sponsorService);
        model = new ExtendedModelMap();
    }

    @Test
    public void testNullMethod() {
        StaticPage page = StaticPage.builder().build();
        when(staticPageService.getPage("Mascots")).thenReturn(page);

        List<Fixture> fixtures = Lists.newArrayList(
                Fixture.builder().build()
        );
        when(sponsorService.getMascots()).thenReturn(fixtures);

        String viewName = controller.getMascots(model);

        assertEquals(EXPECTED_VIEW_NAME, viewName);
        assertEquals(fixtures, model.asMap().get("mascots"));
        assertEquals(page.getContent(), model.asMap().get("page_content"));
        assertEquals("Mascot Packages", model.asMap().get("page_title"));
    }

    @Test
    public void testNullMethodNoStaticPageOrFixtures() {
        String viewName = controller.getMascots(model);

        assertEquals(EXPECTED_VIEW_NAME, viewName);
        assertTrue(((List<Fixture>)model.asMap().get("mascots")).isEmpty());
        assertNull(model.asMap().get("page_content"));
        assertEquals("Mascot Packages", model.asMap().get("page_title"));
    }

}

