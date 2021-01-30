/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.club.domain;

import lombok.Builder;
import lombok.Data;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Data
@Builder
public class ClubName {

    Long id;
    String name;
    String shortName;
    String acronym;
    int startSeason;
    int endSeason;

    public String getShortestName() {
        if (isNotBlank(acronym)) {
            return acronym;
        }
        if (isNotBlank(shortName)) {
            return shortName;
        }
        return name;
    }
}
