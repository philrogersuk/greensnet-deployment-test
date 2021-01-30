package net.greensnet.club.domain;

import net.greensnet.club.dao.ClubNameEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class ClubNameEntityToDomainConverter implements Converter<ClubNameEntity, ClubName> {

    @Override
    public ClubName convert(ClubNameEntity source) {
        return ClubName.builder()
                .endSeason(source.getEndSeason())
                .startSeason(source.getStartSeason())
                .name(source.getName())
                .id(source.getId())
                .build();
    }
}
