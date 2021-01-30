package net.greensnet.competition.domain;

import net.greensnet.competition.dao.CompetitionEntity;
import org.springframework.stereotype.Component;

@Component
public class CompetitionEntityToDomainConverter {
    public Competition convert(CompetitionEntity entity) {
        return Competition.builder()
                .type(entity.getType())
                .name(entity.getName())
                .shortCode(entity.getShortCode())
                .id(entity.getId()).build();
    }
}
