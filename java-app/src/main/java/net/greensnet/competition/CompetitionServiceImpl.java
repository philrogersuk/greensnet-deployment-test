/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition;

import net.greensnet.competition.dao.CompetitionEntity;
import net.greensnet.competition.dao.CompetitionRepository;
import net.greensnet.competition.domain.Competition;
import net.greensnet.competition.domain.CompetitionEntityToDomainConverter;
import net.greensnet.competition.domain.CompetitionType;
import net.greensnet.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 *
 * @author Phil
 */
@Service
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final CompetitionEntityToDomainConverter competitionEntityToDomainConverter;

    @Autowired
    public CompetitionServiceImpl(CompetitionRepository competitionRepository,
                                  CompetitionEntityToDomainConverter competitionEntityToDomainConverter) {
        this.competitionRepository = competitionRepository;
        this.competitionEntityToDomainConverter = competitionEntityToDomainConverter;
    }

    @Override
    public Competition getCompetition(long id) {
        Optional<CompetitionEntity> optional = competitionRepository.findById(id);
        return optional
                .map(competitionEntityToDomainConverter::convert)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public void createCompetition(String name, String shortCode, CompetitionType type) {
        CompetitionEntity item = new CompetitionEntity();
        item.setName(name);
        item.setShortCode(shortCode);
        item.setType(type);
        competitionRepository.save(item);
    }

    @Override
    public List<Competition> getAllCompetitions() {
        return StreamSupport.stream(competitionRepository.findAll().spliterator(), false)
                .map(competitionEntityToDomainConverter::convert)
                .collect(toList());
    }

    @Override
    public List<Competition> getLeagueCompetitions() {
        Iterable<CompetitionEntity> competitions = competitionRepository.findByType(CompetitionType.LEAGUE);
        return StreamSupport.stream(competitions.spliterator(), false)
                .map(competitionEntityToDomainConverter::convert)
                .collect(toList());
    }


    @Deprecated
    @Override
    public CompetitionEntity getCompetitionEntity(long id) {
        Optional<CompetitionEntity> optional = competitionRepository.findById(id);
        return optional.orElseThrow(NotFoundException::new);
    }
}
