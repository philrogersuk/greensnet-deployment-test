package net.greensnet.competition.league.domain;

import net.greensnet.competition.league.dao.LeagueTableNoteEntity;
import org.springframework.stereotype.Component;

@Component
public class LeagueTableNoteEntityToDomainConverter {

    public LeagueTableNote convert(LeagueTableNoteEntity entity) {
        return LeagueTableNote.builder()
                .club(entity.getClub())
                .competitionId(entity.getCompetitionId())
                .deductionReason(entity.getDeductionReason())
                .id(entity.getId())
                .pointsDeducted(entity.getPointsDeducted())
                .season(entity.getSeason())
                .build();
    }
}
