/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league.domain;

import lombok.*;
import net.greensnet.club.dao.CrestEntity;

import java.time.LocalDate;
import java.util.*;

import static java.util.Objects.nonNull;

/**
 *
 * @author Phil
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeagueTable {

    private LocalDate lastFixture;
    private int season;
    private String competitionName;
    private List<LeagueTableRow> rows;
    private List<LeagueTableNote> notes;

    public int getHendonPosition() {
        int rowNum = 1;
        for (LeagueTableRow row : rows) {
            if (row.isHendon()) {
                return rowNum;
            }
            rowNum++;
        }
        return -1;
    }

    public boolean hasPointsDeduction() {
        if(notes.isEmpty()) {
            return false;
        }
        return notes.stream().anyMatch(note -> note.getPointsDeducted() > 0);
    }


    public LeagueTable(final List<LeagueEntry> entries,
                        final Map<Long, CrestEntity> crests,
                        final LocalDate lastFixtureDate,
                        final String competitionName,
                        final int season) {
        this.rows = new ArrayList<>();
        this.notes = new ArrayList<>();
        this.lastFixture = lastFixtureDate;
        this.competitionName = competitionName;
        this.season = season;

        for (LeagueEntry entry: entries) {
            LeagueTableRow row = LeagueTableRow.builder().build();
            row.setClubId(entry.getClubId());
            row.setClubName(entry.getClubName());
            row.setCrest(crests.get(entry.getClubId()));
            rows.add(row);
        }
    }

    public void processFixtures(List<LeagueFixture> fixtures) {
        for (LeagueFixture fixture : fixtures) {
            LeagueTableRow homeTeam = findRowByClub(fixture.getHomeClub().getId());
            LeagueTableRow awayTeam = findRowByClub(fixture.getAwayClub().getId());
            if (Objects.isNull(homeTeam) || Objects.isNull(awayTeam)) {
                continue;
            }
            processFixture(fixture, homeTeam, awayTeam);
        }
    }

    protected LeagueTableRow findRowByClub(long clubId) {
        for (LeagueTableRow row : rows) {
            if (row.getClubId() == clubId) {
                return row;
            }
        }
        return null;
    }

    //todo - belongs on leagueTableRow
    protected static void processFixture(LeagueFixture fixture, LeagueTableRow homeTeam, LeagueTableRow awayTeam) {
        homeTeam.setPlayed(homeTeam.getPlayed() + 1);
        homeTeam.setScored(homeTeam.getScored() + fixture.getHomeGoals());
        homeTeam.setConceeded(homeTeam.getConceeded() + fixture.getAwayGoals());

        awayTeam.setPlayed(awayTeam.getPlayed() + 1);
        awayTeam.setScored(awayTeam.getScored() + fixture.getAwayGoals());
        awayTeam.setConceeded(awayTeam.getConceeded() + fixture.getHomeGoals());

        if (0 < fixture.getHomeGoals() - fixture.getAwayGoals()) {
            homeTeam.setWon(homeTeam.getWon() + 1);
            awayTeam.setLost(awayTeam.getLost() + 1);
        } else if (0 == fixture.getHomeGoals() - fixture.getAwayGoals()) {
            homeTeam.setDrawn(homeTeam.getDrawn() + 1);
            awayTeam.setDrawn(awayTeam.getDrawn() + 1);
        } else {
            homeTeam.setLost(homeTeam.getLost() + 1);
            awayTeam.setWon(awayTeam.getWon() + 1);
        }
    }

    public void processNotes(List<LeagueTableNote> notes) {
        for (LeagueTableNote note : notes) {
            LeagueTableRow row = findRowByClub(note.getClub().getId());
            if (nonNull(row)) {
                row.incrementDeducted(note.getPointsDeducted());
            }
        }
        this.notes = notes;
    }

    public void sortTable() {
        List<LeagueTableRow> list = Collections.synchronizedList(rows);
        Collections.sort(list);
        rows = list;
    }
}
