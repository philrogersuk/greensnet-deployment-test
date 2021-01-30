package net.greensnet.club.domain;

import net.greensnet.club.dao.ClubEntity;
import net.greensnet.club.dao.ClubNameEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Component
public class ClubEntityToDomainConverter implements Converter<ClubEntity, Club> {
    private final ClubNameEntityToDomainConverter oldNamesConverter;

    @Autowired
    public ClubEntityToDomainConverter(ClubNameEntityToDomainConverter oldNamesConverter) {
        this.oldNamesConverter = oldNamesConverter;
    }


    @Override
    public Club convert(ClubEntity source) {
        return Club.builder()
                .names(convertNames(source.getOldNames()))
                .postcode(source.getPostcode())
                .directionsByBus(source.getDirectionsByBus())
                .directionsByCar(source.getDirectionsByCar())
                .directionsByTrain(source.getDirectionsByTrain())
                .directionsByTube(source.getDirectionsByTube())
                .id(source.getId())
                .internalName(source.getName())
                .shortName(source.getShortName())
                .tla(source.getTla())
                .yearFounded(source.getYearFounded())
                .build();
    }

    private Set<ClubName> convertNames(Set<ClubNameEntity> oldNames) {
        return oldNames.stream()
                .map(oldNamesConverter::convert)
                .collect(toSet());
    }
}
