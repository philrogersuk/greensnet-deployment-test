/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.club.domain;

import lombok.Builder;
import lombok.Value;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Value
@Builder
public class Club implements Comparable<Club> {

    Long id;
    String internalName;
    @Deprecated
    String shortName;
    @Deprecated
    String tla;
    int yearFounded;
    String directionsByCar;
    String directionsByTrain;
    String directionsByTube;
    String directionsByBus;
    String postcode;
    @Builder.Default
    Set<ClubName> names = newHashSet();

    @Deprecated
    public String getShortestName() {
        if (isNotBlank(tla)) {
            return tla;
        }
        if (isNotBlank(shortName)) {
            return shortName;
        }
        return internalName;
    }

    public String getNameForSeason(int season) {
        if (names == null || names.isEmpty()) {
            return internalName;
        }
        for (ClubName name : names) {
            if (name.getStartSeason() <= season && name.getEndSeason() >= season) {
                return name.getName();
            }
        }
        return internalName;
    }

    public ClubName getCurrentName() {
        for (ClubName name : names) {
            if (name.getEndSeason() == -1) {
                return name;
            }
        }
        return null;
    }

    public String getPostcodeForMap() {
        return postcode.replace(' ', '+');
    }

    public boolean hasDirections() {
        return nonNull(directionsByCar)
                || nonNull(directionsByTrain)
                || nonNull(directionsByBus)
                || nonNull(directionsByTube);
    }

    @Override
    public int compareTo(Club other) {
        return getInternalName().compareTo(other.getInternalName());
    }
}
