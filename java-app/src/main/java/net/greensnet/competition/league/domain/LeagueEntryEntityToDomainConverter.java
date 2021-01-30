package net.greensnet.competition.league.domain;

import net.greensnet.competition.league.dao.LeagueEntryEntity;
import org.springframework.stereotype.Component;

@Component
public class LeagueEntryEntityToDomainConverter {

    public LeagueEntry convert(LeagueEntryEntity entity) {
        return LeagueEntry.builder()
                .id(entity.getId())
                .clubId(entity.getClubId())
                .clubName(entity.getClubName())
                .competitionId(entity.getCompetitionId())
                .season(entity.getSeason())
                .build();
    }
}
