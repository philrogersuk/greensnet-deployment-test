package net.greensnet.competition.league.domain;

import net.greensnet.competition.dao.CompetitionEntity;
import net.greensnet.competition.domain.Competition;
import net.greensnet.competition.domain.CompetitionEntityToDomainConverter;
import net.greensnet.competition.league.dao.LeagueFixtureEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeagueFixtureEntityToDomainConverter {

    private CompetitionEntityToDomainConverter competitionConverter;

    @Autowired
    public LeagueFixtureEntityToDomainConverter(CompetitionEntityToDomainConverter competitionConverter) {
        this.competitionConverter = competitionConverter;
    }

    public LeagueFixture convert(LeagueFixtureEntity entity) {
        return LeagueFixture.builder()
                .attendance(entity.getAttendance())
                .awayClub(entity.getAwayClub())
                .homeClub(entity.getHomeClub())
                .awayGoals(entity.getAwayGoals())
                .homeGoals(entity.getHomeGoals())
                .competition(convertCompetition(entity.getCompetition()))
                .fixtureDate(entity.getFixtureDate())
                .id(entity.getId())
                .season(entity.getSeason())
                .build();
    }

    private Competition convertCompetition(CompetitionEntity competition) {
        return competitionConverter.convert(competition);
    }

}
