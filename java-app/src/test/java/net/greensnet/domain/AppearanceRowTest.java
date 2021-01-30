/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppearanceRowTest {

    private AppearanceRow row;

    @BeforeEach
    public void setUp() {
        row = new AppearanceRow();
    }

    @AfterEach
    public void tearDown() {
        row = null;
    }

    @Test
    public void testGetLeagueStarts() {
        row.setLeagueStarts(10);
        assertEquals(10, row.getLeagueStarts());
    }

    @Test
    public void testGetLeagueSub() {
        row.setLeagueSub(10);
        assertEquals(10, row.getLeagueSub());
    }

    @Test
    public void testGetLeagueGoals() {
        row.setLeagueGoals(10);
        assertEquals(10, row.getLeagueGoals());
    }

    @Test
    public void testGetCupStarts() {
        row.setCupStarts(10);
        assertEquals(10, row.getCupStarts());
    }

    @Test
    public void testGetCupSub() {
        row.setCupSub(10);
        assertEquals(10, row.getCupSub());
    }

    @Test
    public void testGetCupGoals() {
        row.setCupGoals(10);
        assertEquals(10, row.getCupGoals());
    }

    @Test
    public void testGetFriendlyStarts() {
        row.setFriendlyStarts(10);
        assertEquals(10, row.getFriendlyStarts());
    }

    @Test
    public void testGetFriendlySub() {
        row.setFriendlySub(10);
        assertEquals(10, row.getFriendlySub());
    }

    @Test
    public void testGetFriendlyGoals() {
        row.setFriendlyGoals(10);
        assertEquals(10, row.getFriendlyGoals());
    }

    @Test
    public void testGetFriendlyApps() {
        row.setFriendlyStarts(10);
        row.setFriendlySub(7);
        assertEquals(17, row.getFriendlyApps());
    }

    @Test
    public void testGetCupApps() {
        row.setCupStarts(10);
        row.setCupSub(7);
        assertEquals(17, row.getCupApps());
    }

    @Test
    public void testGetLeagueApps() {
        row.setLeagueStarts(10);
        row.setLeagueSub(7);
        assertEquals(17, row.getLeagueApps());
    }

    @Test
    public void testGetTotalStarts() {
        row.setLeagueStarts(10);
        row.setCupStarts(10);
        row.setFriendlyStarts(10);
        assertEquals(20, row.getTotalStarts());
    }

    @Test
    public void testGetTotalGoals() {
        row.setLeagueGoals(10);
        row.setCupGoals(10);
        row.setFriendlyGoals(10);
        assertEquals(20, row.getTotalGoals());
    }

    @Test
    public void testGetTotalSub() {
        row.setLeagueSub(10);
        row.setCupSub(10);
        row.setFriendlySub(10);
        assertEquals(20, row.getTotalSub());
    }

    @Test
    public void testGetTotalApps() {
        row.setLeagueStarts(10);
        row.setCupStarts(10);
        row.setFriendlyStarts(10);
        row.setLeagueSub(10);
        row.setCupSub(10);
        row.setFriendlySub(10);
        assertEquals(40, row.getTotalApps());
    }

    @Test
    public void testCompareToAfter() {
        row.setSortName("Jeff");
        AppearanceRow row2 = new AppearanceRow();
        row2.setSortName("Adam");
        assertTrue(0 < row.compareTo(row2));
    }

    @Test
    public void testCompareToBefore() {
        row.setSortName("Jeff");
        AppearanceRow row2 = new AppearanceRow();
        row2.setSortName("Zack");
        assertTrue(0 > row.compareTo(row2));
    }
}
