/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.club.domain;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;

class ClubTest {

    private static final int FIRST_YEAR_BEFORE = 1899;
    private static final int FIRST_YEAR = 1900;
    private static final int YEAR_IN_BETWEEN = 1950;
    private static final int LAST_YEAR = 1960;
    private static final int FIRST_YEAR_AFTER = 1961;
    private static final String NAME_GOLDERS_GREEN = "Golders Green";

    private static final String POSTCODE = "NW2 1AE";
    private static final String NAME_HENDON = "Hendon";

    @Test
    void testGetPostcodeForMap() {
        Club club = Club.builder().postcode(POSTCODE).build();
        assertThat(club.getPostcodeForMap()).isEqualTo("NW2+1AE");
    }

    @Test
    void testGetNameForSeasonCurrentNameAndOriginalName() {
        ClubName oldName = ClubName.builder()
                .name(NAME_GOLDERS_GREEN)
                .startSeason(FIRST_YEAR)
                .endSeason(LAST_YEAR)
                .build();
        Set<ClubName> oldNames = newHashSet(oldName);
        Club club = Club.builder().internalName(NAME_HENDON)
                .names(oldNames)
                .build();
        assertThat(club.getNameForSeason(FIRST_YEAR_AFTER)).isEqualTo(NAME_HENDON);
        assertThat(club.getNameForSeason(FIRST_YEAR_BEFORE)).isEqualTo(NAME_HENDON);
    }

    @Test
    void testGetNameForSeasonOldName() {
        ClubName oldName = ClubName.builder()
                .name(NAME_GOLDERS_GREEN)
                .startSeason(FIRST_YEAR)
                .endSeason(LAST_YEAR)
                .build();
        Set<ClubName> oldNames = newHashSet(oldName);
        Club club = Club.builder().internalName(NAME_HENDON)
                .names(oldNames)
                .build();
        assertThat(club.getNameForSeason(YEAR_IN_BETWEEN)).isEqualTo(NAME_GOLDERS_GREEN);
        assertThat(club.getNameForSeason(LAST_YEAR)).isEqualTo(NAME_GOLDERS_GREEN);
        assertThat(club.getNameForSeason(FIRST_YEAR)).isEqualTo(NAME_GOLDERS_GREEN);
    }

    @Test
    void testGetNameForSeasonNullOldNames() {
        Club club = Club.builder().internalName(NAME_HENDON)
                .build();
        assertThat(club.getNameForSeason(YEAR_IN_BETWEEN)).isEqualTo(NAME_HENDON);
    }

    @Test
    void shouldHaveDirectionsWhenHasTrain() {
        Club club = Club.builder().directionsByTrain("").build();
        assertThat(club.hasDirections()).isTrue();
    }

    @Test
    void shouldHaveDirectionsWhenHasTube() {
        Club club = Club.builder().directionsByTube("").build();
        assertThat(club.hasDirections()).isTrue();
    }

    @Test
    void shouldHaveDirectionsWhenHasBus() {
        Club club = Club.builder().directionsByBus("").build();
        assertThat(club.hasDirections()).isTrue();
    }

    @Test
    void shouldHaveDirectionsWhenHasCar() {
        Club club = Club.builder().directionsByCar("").build();
        assertThat(club.hasDirections()).isTrue();
    }

    @Test
    void shouldNotHaveDirectionsWhenNoneSet() {
        Club club = Club.builder().build();
        assertThat(club.hasDirections()).isFalse();
    }

    @Test
    void shouldGetFullNameAsShortest() {
        Club club = Club.builder().internalName("Harrow Borough").build();
        assertThat(club.getShortestName()).isEqualTo("Harrow Borough");
    }

    @Test
    void shouldGetShortNameAsShortest() {
        Club club = Club.builder().internalName("Harrow Borough")
                .shortName("Harrow").build();
        assertThat(club.getShortestName()).isEqualTo("Harrow");
    }

    @Test
    void shouldGetAcronymAsShortest() {
        Club club = Club.builder().internalName("Harrow Borough")
                .shortName("Harrow")
                .tla("HAR")
                .build();
        assertThat(club.getShortestName()).isEqualTo("HAR");
    }

    @Test
    void shouldReturnNullAsCurrentNameIfNoNamesSet() {
        Club club = Club.builder().build();

        assertThat(club.getCurrentName()).isNull();
    }

    @Test
    void shouldReturnNullAsCurrentNameIfNoNamesSetWithNoEndDate() {
        ClubName name = ClubName.builder().endSeason(2010).build();
        Club club = Club.builder().names(newHashSet(name)).build();

        assertThat(club.getCurrentName()).isNull();
    }

    @Test
    void shouldReturnCurrentName() {
        ClubName name1 = ClubName.builder().endSeason(2010).build();
        ClubName name2 = ClubName.builder().endSeason(-1).build();
        Club club = Club.builder().names(newHashSet(name1, name2)).build();

        assertThat(club.getCurrentName()).isEqualTo(name2);
    }
}
