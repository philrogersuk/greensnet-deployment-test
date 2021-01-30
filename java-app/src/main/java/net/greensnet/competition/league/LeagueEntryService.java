package net.greensnet.competition.league;

import net.greensnet.club.domain.Club;
import net.greensnet.competition.league.domain.LeagueEntry;

import java.util.List;

public interface LeagueEntryService {
    List<Long> getCompetitionsEnteredByClub(int season, long clubId);

    void createLeagueEntry(LeagueEntry leagueEntry);

    List<LeagueEntry> getLeagueEntries(int season, long competitionId);

    List<Integer> getSeasonsWithKnownEntryForClub(long clubId);

    List<LeagueEntry> getAllEntries();

    void updateClubNames(Club club);
}
