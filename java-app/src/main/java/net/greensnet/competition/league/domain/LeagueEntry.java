/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeagueEntry {

    private Long id;
    private long clubId;
    private String clubName;
    private int season;
    private long competitionId;
}
