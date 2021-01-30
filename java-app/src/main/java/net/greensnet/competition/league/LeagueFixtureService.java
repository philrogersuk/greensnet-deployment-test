package net.greensnet.competition.league;

import net.greensnet.competition.league.domain.LeagueFixture;

import java.time.LocalDate;
import java.util.List;

public interface LeagueFixtureService {

    void createLeagueFixture(Long homeId, Long awayId, int season, Long competitionId, int homeGoals, int awayGoals,
                             int attendance, LocalDate date);

    void editLeagueFixture(Long id, Long homeId, Long awayId, int season, Long competitionId, int homeGoals,
                              int awayGoals, int attendance, LocalDate date);

    List<LeagueFixture> getFixtures(Long competition, int season);

    LeagueFixture getLeagueFixture(Long id);

    void removeLeagueFixture(Long id);

    LeagueFixture getMostRecentFixture(Long competition, int season);
}
