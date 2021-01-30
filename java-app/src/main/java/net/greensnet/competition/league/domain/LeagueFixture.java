/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league.domain;

import lombok.Builder;
import lombok.Data;
import net.greensnet.club.dao.ClubEntity;
import net.greensnet.competition.domain.Competition;

import java.time.LocalDate;

@Data
@Builder
public class LeagueFixture {

    private Long id;
    private int season;
    private Competition competition;
    private ClubEntity homeClub;
    private ClubEntity awayClub;
    private int homeGoals;
    private int awayGoals;
    private int attendance;
    private LocalDate fixtureDate;

}
