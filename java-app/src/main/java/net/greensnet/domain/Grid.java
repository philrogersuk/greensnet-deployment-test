/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Phil
 */
@Data
@Builder
public class Grid {

    private LocalDate lastFixture;
    private int season;
    private String competitionName;

    private List<GridRow> rows;

}
