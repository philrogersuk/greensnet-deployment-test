/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.club.service;

import net.greensnet.club.dao.ClubEntity;
import net.greensnet.club.dao.ClubNameEntity;
import net.greensnet.club.domain.Club;
import net.greensnet.club.domain.ClubName;

import java.util.List;

public interface ClubService {

    String getHendonTeamName(int season);

    @Deprecated
    ClubEntity getClubEntity(Long id);

    Club getClub(long id);

    List<Club> getAllClubs();

    List<Club> getClubsForCurrentSeason();

    List<Club> getClubsByInitial(char letter);

    void createClub(Club club);

    void editClub(long clubId,
                     Club newClubDetails);

    void addOldName(Long clubId,
                    ClubName newName);

    List<ClubNameEntity> getAllOldClubNames();

    void removeOldName(long id);
}
