/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.domain.SnowballDraw;

import java.time.LocalDateTime;
import java.util.List;

public interface SnowballService {

    SnowballDraw getDraw(Long id);

    boolean createDraw(LocalDateTime date, String name, int numEntries, float fund, int firstPrizeWinner, float firstPrize,
                       int secondPrizeWinner, float secondPrize, float trustContribution, float snowballContribution);

    List<SnowballDraw> getDraws();

    boolean deleteDraw(Long drawId);

    boolean editDraw(Long id, LocalDateTime date, String name, int numEntries, float fund, int firstPrizeWinner,
                     float firstPrize, int secondPrizeWinner, float secondPrize, float trustContribution,
                     float snowballContribution);
}
