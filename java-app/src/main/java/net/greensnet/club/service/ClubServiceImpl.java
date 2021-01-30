/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.club.service;

import com.google.common.collect.Lists;
import net.greensnet.club.dao.ClubEntity;
import net.greensnet.club.dao.ClubNameEntity;
import net.greensnet.club.dao.ClubRepository;
import net.greensnet.club.dao.OldClubNameRepository;
import net.greensnet.club.domain.Club;
import net.greensnet.club.domain.ClubEntityToDomainConverter;
import net.greensnet.club.domain.ClubName;
import net.greensnet.club.event.producer.ClubUpdateProducer;
import net.greensnet.exceptions.NotFoundException;
import net.greensnet.service.FixtureService;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 *
 * @author Phil
 */
@Service
public class ClubServiceImpl implements ClubService {

    private final ClubRepository clubRepository;
    private final ClubEntityToDomainConverter clubConverter;
    private final OldClubNameRepository oldClubNameRepository;
    private final DateHelper dateHelper;
    private final ClubUpdateProducer updateNotifier;
    private final FixtureService fixtureService;

    public static final long HENDON_ID = 1;

    @Autowired
    public ClubServiceImpl(ClubRepository clubRepository,
                           ClubEntityToDomainConverter clubConverter,
                           OldClubNameRepository oldClubNameRepository,
                           DateHelper dateHelper,
                           ClubUpdateProducer updateNotifier,
                           FixtureService fixtureService) {
        this.clubRepository = clubRepository;
        this.clubConverter = clubConverter;
        this.oldClubNameRepository = oldClubNameRepository;
        this.dateHelper = dateHelper;
        this.updateNotifier = updateNotifier;
        this.fixtureService = fixtureService;
    }

    @Override
    public String getHendonTeamName(int season) {
        ClubEntity club = getClubEntity(HENDON_ID);
        return club.getNameForSeason(season);
    }

    @Override
    @Deprecated
    public ClubEntity getClubEntity(Long id) {
        Optional<ClubEntity> optional = clubRepository.findById(id);
        return optional.orElseThrow(NotFoundException::new);
    }

    @Override
    public Club getClub(long id) {
        Optional<ClubEntity> optional = clubRepository.findById(id);
        return optional.map(clubConverter::convert)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public List<Club> getAllClubs() {
        Iterable<ClubEntity> clubs = clubRepository.findAll();
        return StreamSupport.stream(clubs.spliterator(), false)
                .map(clubConverter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<Club> getClubsForCurrentSeason() {
        Set<Long> clubs = fixtureService.getOpponentIdsForSeason(dateHelper.getCurrentSeason());
        clubs.add(HENDON_ID);
        return StreamSupport.stream(clubRepository.findAllById(clubs).spliterator(), false)
                .map(clubConverter::convert)
                .sorted()
                .collect(toList());
    }

    @Override
    public void createClub(Club club) {
        ClubEntity entity = ClubEntity.builder()
                .directionsByBus(club.getDirectionsByBus())
                .directionsByCar(club.getDirectionsByCar())
                .directionsByTrain(club.getDirectionsByTrain())
                .directionsByTube(club.getDirectionsByTube())
                .name(club.getInternalName())
                .shortName(club.getShortName())
                .tla(club.getTla())
                .postcode(club.getPostcode())
                .yearFounded(club.getYearFounded())
                .build();
        ClubEntity saved = clubRepository.save(entity);
        updateNotifier.publishCreate(clubConverter.convert(saved));
    }

    @Override
    public void editClub(long id,
                         Club newClubDetails) {
        ClubEntity clubEntity = clubRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        clubEntity.setName(newClubDetails.getInternalName());
        clubEntity.setShortName(newClubDetails.getShortName());
        clubEntity.setTla(newClubDetails.getTla());
        clubEntity.setYearFounded(newClubDetails.getYearFounded());
        clubEntity.setDirectionsByCar(newClubDetails.getDirectionsByCar());
        clubEntity.setDirectionsByTrain(newClubDetails.getDirectionsByTrain());
        clubEntity.setDirectionsByTube(newClubDetails.getDirectionsByTube());
        clubEntity.setDirectionsByBus(newClubDetails.getDirectionsByBus());
        clubEntity.setPostcode(newClubDetails.getPostcode());
        clubRepository.save(clubEntity);
        updateNotifier.publishUpdate(clubConverter.convert(clubEntity));
    }

    @Override
    public List<Club> getClubsByInitial(char letter) {
        return clubRepository.findByNameStartingWithOrderByName(Character.toUpperCase(letter)).stream()
                .map(clubConverter::convert)
                .collect(toList());
    }

    @Override
    public void addOldName(Long clubId, ClubName newName) {
        ClubEntity club = getClubEntity(clubId);
        ClubNameEntity name = new ClubNameEntity();
        name.setClubId(club.getId());
        name.setName(newName.getName());
        name.setStartSeason(newName.getStartSeason());
        name.setEndSeason(newName.getEndSeason());
        club.getOldNames().add(name);
        ClubEntity saved = clubRepository.save(club);
        updateNotifier.publishUpdate(clubConverter.convert(saved));
    }

    @Override
    public List<ClubNameEntity> getAllOldClubNames() {
        Iterable<ClubNameEntity> names = oldClubNameRepository.findAll();
        return Lists.newArrayList(names);
    }

    @Override
    public void removeOldName(long id) {
        ClubNameEntity oldName = oldClubNameRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        oldClubNameRepository.delete(oldName);

        ClubEntity clubToDeleteFrom = getClubEntity(oldName.getClubId());
        updateNotifier.publishUpdate(clubConverter.convert(clubToDeleteFrom));
    }
}
