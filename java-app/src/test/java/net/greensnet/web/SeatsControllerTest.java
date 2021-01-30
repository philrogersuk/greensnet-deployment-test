/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import net.greensnet.domain.Seat;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class SeatsControllerTest {
	private static final String EXPECTED_VIEW_NAME = "th_seats";

	@Mock
	private SponsorService service;
	@Mock
	private StaticPageService pageService;

	private SeatsController controller;
	private Model model;

	@BeforeEach
	public void setup() {
		controller = new SeatsController(service, pageService);
		model = new ExtendedModelMap();
	}

	@Test
	public void testNullMethod() {
		StaticPage page = StaticPage.builder().build();
		when(pageService.getPage("GaryMcCannStand")).thenReturn(page);

		Seat[][] seats = new Seat[1][1];
		seats[0][0] = new Seat();
		when(service.getSeatsInGrid()).thenReturn(seats);

		String viewName = controller.getSeats(model);

		assertEquals(EXPECTED_VIEW_NAME, viewName);
		assertEquals(seats, model.asMap().get("seats"));
		assertEquals(page.getContent(), model.asMap().get("page_content"));
		assertEquals("The Gary McCann Stand Sponsorship", model.asMap().get("page_title"));

	}

	@Test
	public void testNullMethodWithNullPage() {
		Seat[][] seats = new Seat[1][1];
		seats[0][0] = new Seat();
		when(service.getSeatsInGrid()).thenReturn(seats);

		String viewName = controller.getSeats(model);

		assertEquals(EXPECTED_VIEW_NAME, viewName);
		assertEquals(seats, model.asMap().get("seats"));
		assertNull(model.asMap().get("page_content"));
		assertEquals("The Gary McCann Stand Sponsorship", model.asMap().get("page_title"));
	}
}