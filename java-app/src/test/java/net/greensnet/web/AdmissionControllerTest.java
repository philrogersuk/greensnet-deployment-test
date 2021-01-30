/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import net.greensnet.domain.StaticPage;
import net.greensnet.service.FixtureService;
import net.greensnet.service.StaticPageService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdmissionControllerTest {
    private static final String EXPECTED_VIEW_NAME = "th_admission";
    private static final String EXPECTED_PAGE_NAME = "Admission";

    @Mock
    private FixtureService fixtureService;
    @Mock
    private StaticPageService staticPageService;

    private AdmissionController controller;
    private Model model;

    @BeforeEach
    public void setup() {
        controller = new AdmissionController(fixtureService, staticPageService);
        model = new ExtendedModelMap();
    }

    @Test
    public void testNullPage() {
        when(staticPageService.getPage(EXPECTED_PAGE_NAME)).thenReturn(null);
        when(fixtureService.getFutureTicketedFixtures()).thenReturn(Lists.newArrayList());

        String viewName = controller.viewAdmissionDetails(model);

        assertThat(viewName, equalTo(EXPECTED_VIEW_NAME));
        assertThat(model.asMap(), hasEntry("page_title", EXPECTED_PAGE_NAME));
        assertThat(model.asMap(), hasEntry("ticketed_fixtures", Lists.newArrayList()));
        assertThat(model.asMap(), not(hasKey("page_content")));
    }

    @Test
    public void testWithPage() {
        StaticPage expectedPage = StaticPage.builder().build();
        when(staticPageService.getPage(EXPECTED_PAGE_NAME)).thenReturn(expectedPage);
        when(fixtureService.getFutureTicketedFixtures()).thenReturn(Lists.newArrayList());

        String viewName = controller.viewAdmissionDetails(model);

        assertThat(viewName, equalTo(EXPECTED_VIEW_NAME));
        assertThat(model.asMap(), hasEntry("page_title", EXPECTED_PAGE_NAME));
        assertThat(model.asMap(), hasEntry("ticketed_fixtures", Lists.newArrayList()));
        assertThat(model.asMap(), hasEntry("page_content", expectedPage.getContent()));
    }
}
