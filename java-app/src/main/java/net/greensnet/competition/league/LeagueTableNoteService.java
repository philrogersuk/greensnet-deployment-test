package net.greensnet.competition.league;

import net.greensnet.competition.league.domain.LeagueTableNote;

import java.util.List;

public interface LeagueTableNoteService {
    void createLeagueTableNote(int season, Long competitionId, Long clubId, int points, String reason);

    LeagueTableNote getLeagueTableNote(long id);

    List<LeagueTableNote> getLeagueTableNotes(int season, long competitionId);

    void editLeagueTableNote(Long id, Long clubId, int points, String reason);

    void removeLeagueNote(Long id);
}
