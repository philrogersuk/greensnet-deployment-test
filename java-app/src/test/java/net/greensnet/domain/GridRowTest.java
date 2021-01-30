/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import net.greensnet.competition.league.domain.LeagueFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GridRowTest {

    private GridRow row;

    @BeforeEach
    public void setUp() {
        row = new GridRow();
    }

    @Test
    public void testGetFixtures() {
        LeagueFixture[] fixtures = new LeagueFixture[2];
        fixtures[0] = LeagueFixture.builder().build();
        fixtures[1] = LeagueFixture.builder().build();
        row.setFixtures(fixtures);
        assertEquals(2, row.getFixtures().length);
    }
}
