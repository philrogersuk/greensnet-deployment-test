/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import net.greensnet.competition.league.domain.LeagueFixture;

import java.util.Arrays;

/**
 *
 * @author Phil
 */
public class GridRow {
    private long clubId;
    private String clubName;
    private LeagueFixture[] fixtures;

    public long getClubId() {
        return clubId;
    }

    public void setClubId(long clubId) {
        this.clubId = clubId;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public void setFixture(LeagueFixture fixture, int index) {
        fixtures[index] = fixture;
    }

    public LeagueFixture[] getFixtures() {
        return Arrays.copyOf(fixtures, fixtures.length);
    }

    public void setFixtures(LeagueFixture[] fixtures) {
        this.fixtures = Arrays.copyOf(fixtures, fixtures.length);
    }
}
