/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.domain.Grid;

public interface GridService {
    Grid getFixtureGrid(long competition, int season);
}
