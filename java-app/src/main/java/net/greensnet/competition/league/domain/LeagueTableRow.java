/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league.domain;

import lombok.Builder;
import lombok.Data;
import net.greensnet.club.dao.CrestEntity;

import java.util.Objects;

import static net.greensnet.club.service.ClubServiceImpl.HENDON_ID;

@Data
@Builder
public class LeagueTableRow implements Comparable<LeagueTableRow> {

    static final String UNKNOWN_CREST = "/images/crests/unknown.png";

    private CrestEntity crest;
    private long clubId;
    private String clubName;
    private int played;
    private int won;
    private int drawn;
    private int lost;
    private int scored;
    private int conceeded;
    private int deducted;

    public void incrementDeducted(int num) {
        deducted = deducted + num;
    }

    public int getPoints() {
        return (won * 3) + drawn - deducted;
    }

    public int getGoalDifference() {
        return scored - conceeded;
    }

    public boolean isHendon() {
        return HENDON_ID == getClubId();
    }

    public String getCrestUrl() {
        if (Objects.isNull(crest)) {
            return UNKNOWN_CREST;
        }
        return crest.getAwsUrl().replace("_h100", "_h40");
    }

    @Override
    public int compareTo(LeagueTableRow item) {
        int points = Integer.compare(item.getPoints(), getPoints());
        if (0 != points) {
            return points;
        }

        int goalDifference = Integer.compare(item.getGoalDifference(), getGoalDifference());
        if (0 != goalDifference) {
            return goalDifference;
        }

        int scored = Integer.compare(item.getScored(), getScored());
        if (0 != scored) {
            return scored;
        }

        return getClubName().compareTo(item.getClubName());
    }
}
