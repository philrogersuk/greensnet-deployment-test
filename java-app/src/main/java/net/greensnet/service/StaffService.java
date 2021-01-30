/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StaffService {

    void createStaffUpdate(long staffMemberId, LocalDate datePublished, String occupation, String homeLocation,
                           String stillInFootball, String whereJoinedFrom, String otherPreviousClubs, String wherePlayedAfter,
                           String favouriteMemories, String bestMatch, String bestGoal, String bestPlayer, String bestManager,
                           String favouriteGround, String leastFavGround);

    boolean editStaffUpdate(long staffUpdateId, long staffMemberId, String occupation, String homeLocation,
                         String stillInFootball, String whereJoinedFrom, String otherPreviousClubs, String wherePlayedAfter,
                         String favouriteMemories, String bestMatch, String bestGoal, String bestPlayer, String bestManager,
                         String favouriteGround, String leastFavGround);

    StaffUpdate getStaffUpdate(long id);

    StaffUpdate getStaffUpdateByStaffId(long id);

    boolean createStaffMember(Map<String, String> playerInfo);

    Fixture getDebutById(long playerId);

    StaffMember getStaffMemberById(long playerId, boolean getTimeAtClub);

    /**
     * Gets all time periods worked by staff member where they were NOT a Player.
     *
     */
    List<TimeAtClub> getStaffPeriods(StaffMember person);

    List<String> getStaffRoleValues();

    StaffMember getStaffMember(long id);

    List<StaffMember> getAllStaffMembers();

    List<StaffMember> getCurrentSquadList();

    StaffMember getStaffMemberByFixtureAppearanceId(long playerId);

    OppositionAppearance getOppositionAppearance(long id);

    List<StaffMember> getPlayersForSeason(int season, boolean includeTrialist, boolean includeManagement);

    List<StaffUpdate> getStaffMembersWithUpdate();

    List<StaffMember> getStaffMembersByInitial(char letter);

    void editStaffMember(Map<String, String> playerInfo);

    void updatePenpic(long id, MultipartFile penpic);

    void updateCorporateSponsorImage(long id, MultipartFile sponsorImage);

    void deleteCorporateSponsorImage(long id);

    void createTimeAtClub(long staffId,
                          String role,
                          LocalDate startDate,
                          LocalDate endDate,
                          int startSeason,
                          int endSeason,
                          boolean onLoan,
                          boolean onTrial);

    void updateTimeAtClub(long timeAtClubId,
                          long staffId,
                          String role,
                          LocalDate startDate,
                          LocalDate endDate,
                          int startSeason,
                          int endSeason,
                          boolean onLoan,
                          boolean onTrial);

    void deleteTimeAtClub(long id);
}
