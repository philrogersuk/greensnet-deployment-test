/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league.domain;

import lombok.Builder;
import lombok.Data;
import net.greensnet.club.dao.ClubEntity;

@Data
@Builder
public class LeagueTableNote {

    private Long id;
    private int season;
    private long competitionId;
    private ClubEntity club;
    private int pointsDeducted;
    private String deductionReason;

    public String getClubName() {
        return club.getNameForSeason(season);
    }
}
