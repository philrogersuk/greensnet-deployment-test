/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.domain;

import lombok.Builder;
import lombok.Data;

/**
 * Domain object representing a competition.
 *
 * @author phil
 */
@Data
@Builder
public class Competition {

    private Long id;
    private String name;
    private String shortCode;
    private CompetitionType type;
}
