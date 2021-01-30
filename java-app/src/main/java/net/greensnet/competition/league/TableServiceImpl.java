/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league;

import net.greensnet.club.dao.CrestEntity;
import net.greensnet.club.service.ClubServiceImpl;
import net.greensnet.club.service.CrestService;
import net.greensnet.competition.CompetitionService;
import net.greensnet.competition.league.domain.LeagueEntry;
import net.greensnet.competition.league.domain.LeagueFixture;
import net.greensnet.competition.league.domain.LeagueTable;
import net.greensnet.competition.league.domain.LeagueTableNote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
public class TableServiceImpl implements TableService {

    private final CompetitionService competitionService;
    private final CrestService crestService;
    private final LeagueTableNoteService leagueTableNoteService;
    private final LeagueEntryService leagueEntryService;
    private final LeagueFixtureService leagueFixtureService;

    @Autowired
    public TableServiceImpl(CompetitionService competitionService,
                            CrestService crestService,
                            LeagueTableNoteService leagueTableNoteService,
                            LeagueEntryService leagueEntryService,
                            LeagueFixtureService leagueFixtureService) {
        this.competitionService = competitionService;
        this.crestService = crestService;
        this.leagueTableNoteService = leagueTableNoteService;
        this.leagueEntryService = leagueEntryService;
        this.leagueFixtureService = leagueFixtureService;
    }

    public List<LeagueTable> getTables(int season) {
        List<LeagueTable> tables = new ArrayList<>();
        List<Long> competitions = leagueEntryService.getCompetitionsEnteredByClub(season, ClubServiceImpl.HENDON_ID);
        for (long competition : competitions) {
            tables.add(getTable(competition, season));
        }
        return tables;
    }

    public LeagueTable getTable(long competition, int season) {
        LeagueFixture lastFixture = leagueFixtureService.getMostRecentFixture(competition, season);
        String competitionName = competitionService.getCompetition(competition).getName();
        LeagueTable table = initialiseTable(competition, season, lastFixture, competitionName);
        processFixtures(table, competition, season);
        processNotes(table, competition, season);
        table.sortTable();
        return table;
    }

    private LeagueTable initialiseTable(final long competition,
                                        final int season,
                                        final LeagueFixture lastFixture,
                                        final String competitionName) {
        List<LeagueEntry> entries = leagueEntryService.getLeagueEntries(season, competition);
        Map<Long, CrestEntity> crests = crestService.getByClubs(entries.stream()
                .map(LeagueEntry::getClubId)
                .collect(Collectors.toSet()));

        LocalDate lastFixtureDate = null;
        if (nonNull(lastFixture)) {
            lastFixtureDate = lastFixture.getFixtureDate();
        }

        return new LeagueTable(entries, crests, lastFixtureDate, competitionName, season);
    }

    private void processFixtures(LeagueTable table, long competition, int season) {
        List<LeagueFixture> fixtures = leagueFixtureService.getFixtures(competition, season);
        table.processFixtures(fixtures);
    }

    private void processNotes(LeagueTable table, long competition, int season) {
        List<LeagueTableNote> notes = leagueTableNoteService.getLeagueTableNotes(season, competition);
        table.processNotes(notes);
    }

    @Override
    public List<Integer> getValidSeasons() {
        return leagueEntryService.getSeasonsWithKnownEntryForClub(ClubServiceImpl.HENDON_ID);
    }
}
