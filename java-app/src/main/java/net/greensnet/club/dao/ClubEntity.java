/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.club.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isNotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "CLUB")
public class ClubEntity implements Comparable<ClubEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "CLUBNAME")
    private String name;
    @Deprecated
    private String shortName;
    @Deprecated
    private String tla;
    private int yearFounded;
    private String directionsByCar;
    private String directionsByTrain;
    private String directionsByTube;
    private String directionsByBus;
    private String postcode;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "clubid", nullable=false)
    @Builder.Default
    private Set<ClubNameEntity> oldNames = newHashSet();

    public String getShortestName() {
        if (isNotBlank(tla)) {
            return tla;
        }
        if (isNotBlank(shortName)) {
            return shortName;
        }
        return name;
    }

    public String getNameForSeason(int season) {
        if (oldNames == null || oldNames.isEmpty()) {
            return name;
        }
        for (ClubNameEntity oldName : oldNames) {
            if (oldName.getStartSeason() <= season && oldName.getEndSeason() >= season) {
                return oldName.getName();
            }
        }
        return name;
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
    public int compareTo(ClubEntity other) {
        return getName().compareTo(other.getName());
    }
}
