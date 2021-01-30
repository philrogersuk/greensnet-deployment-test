/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.domain.AppearanceRow;
import net.greensnet.domain.PlayerAppearanceRow;
import net.greensnet.domain.StaffMember;

import java.util.List;

public interface AppearanceService {

    List<PlayerAppearanceRow> getPlayerApps(StaffMember player);

    List<AppearanceRow> getAppearances(int season);
}
