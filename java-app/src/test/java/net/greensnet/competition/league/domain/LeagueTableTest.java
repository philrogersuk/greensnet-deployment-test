/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league.domain;

import net.greensnet.club.dao.CrestEntity;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author Phil
 */
class LeagueTableTest {

    @Test
    void shouldGetHendonPosition() {
        List<LeagueTableRow> rows = newArrayList(
                createLeagueTableRow(2L),
                createLeagueTableRow(1L)
        );

        LeagueTable table = LeagueTable.builder()
                .notes(Lists.emptyList())
                .rows(rows)
                .build();

        assertThat(table.getHendonPosition()).isEqualTo(2);
    }

    @Test
    void shouldGetHendonPositionNotFound() {
        LeagueTable table = LeagueTable.builder()
                .notes(Lists.emptyList())
                .rows(Lists.emptyList())
                .build();

        assertThat(table.getHendonPosition()).isEqualTo(-1);
    }

    @Test
    void shouldNotHavePointsDeductionWhenEmptyNotes() {
        LeagueTable table = createLeagueTable(Lists.emptyList());

        assertThat(table.hasPointsDeduction()).isFalse();
    }

    @Test
    void shouldNotHavePointsDeductionWhenNoDeductions() {
        LeagueTable table = LeagueTable.builder()
                .notes(Lists.newArrayList(
                        createLeagueTableNote(0),
                        createLeagueTableNote(0)
                        ))
                .rows(Lists.emptyList())
                .build();

        assertThat(table.hasPointsDeduction()).isFalse();
    }

    @Test
    void shouldHavePointsDeductionWhenAnyPointsDeducted() {
        LeagueTable table = LeagueTable.builder()
                .notes(Lists.newArrayList(
                        createLeagueTableNote(0),
                        createLeagueTableNote(1)
                ))
                .rows(Lists.emptyList())
                .build();

        assertThat(table.hasPointsDeduction()).isTrue();
    }

    @Test
    void shouldInitialiseLeagueTableOnConstruction() {
        List<LeagueEntry> entries = newArrayList(LeagueEntry.builder().clubId(1L).clubName("Hendon").build(),
                LeagueEntry.builder().clubId(2L).clubName("Wealdstone").build(),
                LeagueEntry.builder().clubId(3L).clubName("Harrow Borough").build(),
                LeagueEntry.builder().clubId(4L).clubName("Wingate & Finchley").build());
        Map<Long, CrestEntity> crests = Map.of(1L, CrestEntity.builder().id(101L).build(),
                2L, CrestEntity.builder().id(102L).build());
        LocalDate lastFixtureDate = LocalDate.now();
        String competitionName = "Competition Name";
        int season = 2000;

        LeagueTable table = new LeagueTable(entries, crests, lastFixtureDate, competitionName, season);

        assertThat(table.getCompetitionName()).isEqualTo(competitionName);
        assertThat(table.getLastFixture()).isEqualTo(lastFixtureDate);
        assertThat(table.getSeason()).isEqualTo(season);

        assertThat(table.getRows()).containsExactly(
                LeagueTableRow.builder().clubName("Hendon").clubId(1).crest(CrestEntity.builder().id(101L).build()).build(),
                LeagueTableRow.builder().clubName("Wealdstone").clubId(2).crest(CrestEntity.builder().id(102L).build()).build(),
                LeagueTableRow.builder().clubName("Harrow Borough").clubId(3).build(),
                LeagueTableRow.builder().clubName("Wingate & Finchley").clubId(4).build()
                );
    }

    @Test
    void shouldFindLeagueTableRowById() {
        LeagueTableRow expected = LeagueTableRow.builder().clubName("Wealdstone").clubId(2).build();

        List<LeagueTableRow> rows = newArrayList(
                LeagueTableRow.builder().clubName("Hendon").clubId(1).build(),
                LeagueTableRow.builder().clubName("Wealdstone").clubId(2).build(),
                LeagueTableRow.builder().clubName("Harrow Borough").clubId(3).build(),
                LeagueTableRow.builder().clubName("Wingate & Finchley").clubId(4).build());

        LeagueTable table = LeagueTable.builder()
                .rows(rows)
                .build();

        LeagueTableRow actual = table.findRowByClub(2);

        assertThat(actual).isEqualTo(expected);
    }


        private LeagueTable createLeagueTable(List<LeagueTableRow> objects) {
        return LeagueTable.builder()
                .notes(Lists.emptyList())
                .rows(objects)
                .build();
    }

    private LeagueTableNote createLeagueTableNote(int pointsDeducted) {
        return LeagueTableNote.builder()
                .pointsDeducted(pointsDeducted)
                .build();
    }

    private LeagueTableRow createLeagueTableRow(long clubId) {
        return LeagueTableRow.builder()
                .clubId(clubId)
                .build();
    }
}
