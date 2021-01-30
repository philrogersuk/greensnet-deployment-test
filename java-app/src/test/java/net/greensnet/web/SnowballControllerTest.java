/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import com.google.common.collect.Lists;
import net.greensnet.domain.SnowballDraw;
import net.greensnet.domain.StaticPage;
import net.greensnet.service.SnowballService;
import net.greensnet.service.StaticPageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SnowballControllerTest {
	private static final String EXPECTED_PAGE_TITLE = "Snowball Draw";
	private static final String PAGE_URL = "Snowball";

	@Mock
	private SnowballService manager;
	@Mock
	private StaticPageService pageManager;

	private SnowballController controller;

	private Model model;

	@BeforeEach
	public void setup() {
		controller = new SnowballController(manager, pageManager);
		model = new ExtendedModelMap();
	}

	@Test
	public void testNoParameters() {
		StaticPage page = StaticPage.builder().content("Snowball content").build();
		when(pageManager.getPage(PAGE_URL)).thenReturn(page);

		List<SnowballDraw> draws = getSnowballDraws();
		when(manager.getDraws()).thenReturn(draws);

		String viewName = controller.getDraws(model);

		assertEquals("th_snowball", viewName);
		assertEquals(draws, model.asMap().get("snowball_draws"));
		assertEquals(page.getContent(), model.asMap().get("page_content"));
		assertEquals(EXPECTED_PAGE_TITLE, model.asMap().get("page_title"));
	}

	@Test
	public void testNoParametersNullPage() {
		List<SnowballDraw> draws = getSnowballDraws();
		when(manager.getDraws()).thenReturn(draws);


		String viewName = controller.getDraws(model);

		assertEquals("th_snowball", viewName);
		assertEquals(draws, model.asMap().get("snowball_draws"));
		assertNull(model.asMap().get("page_content"));
		assertEquals(EXPECTED_PAGE_TITLE, model.asMap().get("page_title"));
	}

	private List<SnowballDraw> getSnowballDraws() {
		return Lists.newArrayList(
				SnowballDraw.builder().build(),
				SnowballDraw.builder().name("Nov 2012").date(LocalDateTime.of(2012, 11, 2, 1, 0, 0)).build(),
				SnowballDraw.builder().name("Dec 2012").date(LocalDateTime.of(2012, 12, 2, 1, 0, 0)).build(),
				SnowballDraw.builder().name("Snowball 2012").date(LocalDateTime.of(2012, 12, 31, 1, 0, 0)).build());
	}



}