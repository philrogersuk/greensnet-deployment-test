/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import lombok.Builder;
import lombok.Data;

/**
 *
 * @author Phil
 */
@Data
@Builder
public class PlayerAppearanceRow {

    private int leagueStarts;
    private int leagueSub;
    private int leagueGoals;
    private int cupStarts;
    private int cupSub;
    private int cupGoals;
    private int friendlyStarts;
    private int friendlySub;
    private int friendlyGoals;

    private int season;

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
}
