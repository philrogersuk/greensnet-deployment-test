/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import com.google.common.collect.Lists;
import net.greensnet.club.domain.Club;
import net.greensnet.club.service.ClubService;
import net.greensnet.club.service.ClubServiceImpl;
import net.greensnet.club.web.DirectionsController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@Disabled
@ExtendWith(MockitoExtension.class)
public class DirectionsControllerTest {

	private static final String EXPECTED_VIEW_NAME = "th_directions";
    private static final String EXPECTED_PAGE_PREFIX = "Directions";
	public static final String HENDON = "Hendon";
	public static final String HARROW_BOROUGH = "Harrow Borough";

	@Mock
    private ClubService clubService;

    private DirectionsController controller;
    private Model model;

    @BeforeEach
    public void setup() {
        controller = new DirectionsController(clubService);
        model = new ExtendedModelMap();
    }


    @Test
    public void testNullId() {
        List<Club> list = Lists.newArrayList(
                createClub(HENDON),
                createClub(HARROW_BOROUGH));
        when(clubService.getClubsForCurrentSeason()).thenReturn(list);

        Club club = createClub(HENDON);
        when(clubService.getClub(ClubServiceImpl.HENDON_ID)).thenReturn(club);

        String viewName = controller.getDirections(model, null);

        assertEquals(EXPECTED_VIEW_NAME, viewName);
        assertEquals(club, model.asMap().get("club"));
        assertEquals(list, model.asMap().get("club_list"));
        assertEquals(EXPECTED_PAGE_PREFIX + " - " + club.getInternalName(),
                model.asMap().get("page_title"));
    }

    @Test
    @Disabled
    public void testValidID() {
        List<Club> list = Lists.newArrayList(
                createClub(HENDON),
                createClub(HARROW_BOROUGH));
        when(clubService.getClubsForCurrentSeason()).thenReturn(list);

        Club club = createClub(HARROW_BOROUGH);
        when(clubService.getClub(3)).thenReturn(club);

        String viewName = controller.getDirections(model, 3L);

        assertEquals(EXPECTED_VIEW_NAME, viewName);
        assertEquals(club, model.asMap().get("club"));
        assertEquals(list, model.asMap().get("club_list"));
        assertEquals(EXPECTED_PAGE_PREFIX + " - " + club.getInternalName(),
                model.asMap().get("page_title"));
    }

    @Test
    public void testInvalidID() {
        List<Club> list = Lists.newArrayList(
                createClub(HENDON),
                createClub(HARROW_BOROUGH));
        when(clubService.getClubsForCurrentSeason()).thenReturn(list);

        String viewName = controller.getDirections(model, -1L);

        assertEquals(EXPECTED_VIEW_NAME, viewName);
        assertNull(model.asMap().get("club"));
        assertEquals(list, model.asMap().get("club_list"));
        assertEquals(EXPECTED_PAGE_PREFIX, model.asMap().get("page_title"));
    }

    private Club createClub(String name) {
        return Club.builder().internalName(name).build();
    }
}
