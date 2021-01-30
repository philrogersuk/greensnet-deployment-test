/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Phil
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppearanceRow implements Comparable<AppearanceRow> {

    private StaffMember player;
    private String playerName;
    private String sortName;
    private boolean onLoan;

    private int leagueStarts;
    private int leagueSub;
    private int leagueGoals;
    private int cupStarts;
    private int cupSub;
    private int cupGoals;
    private int friendlyStarts;
    private int friendlySub;
    private int friendlyGoals;

    public int getFriendlyApps() {
        return friendlyStarts + friendlySub;
    }

    public int getCupApps() {
        return cupStarts + cupSub;
    }

    public int getLeagueApps() {
        return leagueStarts + leagueSub;
    }

    public int getTotalStarts() {
        return cupStarts + leagueStarts;
    }

    public int getTotalGoals() {
        return cupGoals + leagueGoals;
    }

    public int getTotalSub() {
        return cupSub + leagueSub;
    }

    public int getTotalApps() {
        return cupStarts + leagueStarts + cupSub + leagueSub;
    }

    public int compareTo(AppearanceRow other) {
        return getSortName().compareTo(other.getSortName());
    }

}
