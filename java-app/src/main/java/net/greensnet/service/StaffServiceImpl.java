/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import net.greensnet.dao.*;
import net.greensnet.domain.*;
import net.greensnet.exceptions.ItemDeletedException;
import net.greensnet.exceptions.NotFoundException;
import net.greensnet.service.files.FileService;
import net.greensnet.service.files.ImageSize;
import net.greensnet.util.DateHelper;
import net.greensnet.util.PrimitiveUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static net.greensnet.service.files.ImageSize.Type.*;

@Service
public class StaffServiceImpl implements StaffService {

    private final FixtureRepository fixtureRepository;
    private final StaffRepository staffRepository;
    private final OldStaffNameRepository oldStaffNameRepository;
    private final StaffRoleRepository staffRoleRepository;
    private final TimeAtClubRepository timeAtClubRepository;
    private final OppositionAppearanceRepository oppositionAppearanceRepository;
    private final FixtureAppearanceRepository fixtureAppearanceRepository;
    private final StaffUpdateRepository staffUpdateRepository;
    private final FileService fileService;
    private final DateHelper dateHelper;

    @Autowired
    public StaffServiceImpl(StaffRepository staffRepository,
                            StaffUpdateRepository staffUpdateRepository,
                            OldStaffNameRepository oldStaffNameRepository,
                            StaffRoleRepository staffRoleRepository,
                            TimeAtClubRepository timeAtClubRepository,
                            OppositionAppearanceRepository oppositionAppearanceRepository,
                            FixtureAppearanceRepository fixtureAppearanceRepository,
                            FixtureRepository fixtureRepository,
                            FileService fileService,
                            DateHelper dateHelper) {
        this.staffRepository = staffRepository;
        this.staffUpdateRepository = staffUpdateRepository;
        this.oldStaffNameRepository = oldStaffNameRepository;
        this.staffRoleRepository = staffRoleRepository;
        this.timeAtClubRepository = timeAtClubRepository;
        this.oppositionAppearanceRepository = oppositionAppearanceRepository;
        this.fixtureAppearanceRepository = fixtureAppearanceRepository;
        this.fixtureRepository = fixtureRepository;
        this.fileService = fileService;
        this.dateHelper = dateHelper;
    }

    @Override
    public void createStaffUpdate(long staffMemberId, LocalDate datePublished, String occupation, String homeLocation,
                                  String stillInFootball, String whereJoinedFrom, String otherPreviousClubs, String wherePlayedAfter,
                                  String favouriteMemories, String bestMatch, String bestGoal, String bestPlayer, String bestManager,
                                  String favouriteGround, String leastFavGround) {
        StaffUpdate staffUpdate = new StaffUpdate();
        populateStaffUpdate(staffMemberId, occupation, homeLocation, stillInFootball, whereJoinedFrom,
                otherPreviousClubs, wherePlayedAfter, favouriteMemories, bestMatch, bestGoal, bestPlayer, bestManager,
                leastFavGround, staffUpdate);
        staffUpdate.setDatePublished(datePublished);
        staffUpdateRepository.save(staffUpdate);
    }

    @Override
    public boolean editStaffUpdate(long staffUpdateId, long staffMemberId, String occupation, String homeLocation,
                                String stillInFootball, String whereJoinedFrom, String otherPreviousClubs, String wherePlayedAfter,
                                String favouriteMemories, String bestMatch, String bestGoal, String bestPlayer, String bestManager,
                                String favouriteGround, String leastFavGround) {
        Optional<StaffUpdate> item = staffUpdateRepository.findById(staffUpdateId);
        if (item.isPresent()) {
            populateStaffUpdate(staffMemberId, occupation, homeLocation, stillInFootball, whereJoinedFrom,
                    otherPreviousClubs, wherePlayedAfter, favouriteMemories, bestMatch, bestGoal, bestPlayer, bestManager,
                    leastFavGround, item.get());
            staffUpdateRepository.save(item.get());
            return true;
        }
        return false;
    }

    private void populateStaffUpdate(long staffMemberId, String occupation, String homeLocation, String stillInFootball,
                                     String whereJoinedFrom, String otherPreviousClubs, String wherePlayedAfter, String favouriteMemories,
                                     String bestMatch, String bestGoal, String bestPlayer, String bestManager, String leastFavGround,
                                     StaffUpdate staffUpdate) {
        staffUpdate.setStaffMember(getStaffMember(staffMemberId));
        staffUpdate.setOccupation(occupation);
        staffUpdate.setHomeLocation(homeLocation);
        staffUpdate.setStillInFootball(stillInFootball);
        staffUpdate.setWhereJoinedFrom(whereJoinedFrom);
        staffUpdate.setOtherPreviousClubs(otherPreviousClubs);
        staffUpdate.setWherePlayedAfter(wherePlayedAfter);
        staffUpdate.setFavouriteMemories(favouriteMemories);
        staffUpdate.setBestMatch(bestMatch);
        staffUpdate.setBestGoal(bestGoal);
        staffUpdate.setBestPlayer(bestPlayer);
        staffUpdate.setBestManager(bestManager);
        staffUpdate.setLeastFavGround(leastFavGround);
    }

    @Override
    public StaffUpdate getStaffUpdate(long id) {
        Optional<StaffUpdate> update = staffUpdateRepository.findById(id);
        return update.orElse(null);
    }

    @Override
    public StaffUpdate getStaffUpdateByStaffId(long id) {
        StaffMember staffMember = getStaffMember(id);
        if (!Objects.isNull(staffMember)) {
            Optional<StaffUpdate> update = staffUpdateRepository.findByStaffMember(staffMember);
            if (update.isPresent()) {
                return update.get();
            }
        }
        return null;
    }

    @Override
    public boolean createStaffMember(Map<String, String> playerInfo) {
        StaffMember staffMember = new StaffMember();
        updateBasicStaffInfo(playerInfo, staffMember);
        staffRepository.save(staffMember);
        return true;
    }

    private void handleUpload(MultipartFile fileStream, StaffMember staffMember) {
        try {
            if (null != fileStream && fileStream.getInputStream().available() > 0) {

                staffMember.setHasImage(true);
                String filename = "penpics/" + staffMember.getId() + '-' + staffMember.getFirstName() + '-' + staffMember.getLastName() + "." + Files.getFileExtension(fileStream.getOriginalFilename());
                List<ImageSize> sizes = Lists.newArrayList(new ImageSize(WIDTH, 700), new ImageSize(ORIGINAL));
                fileService.uploadImage(fileStream, filename, sizes);
                staffMember.setPublicUrl(fileService.getUrl(new ImageSize(WIDTH, 700).renameFile(filename)).orElse(null));
            } else {
                staffMember.setHasImage(false);
                staffMember.setPublicUrl(null);
            }
        } catch (IOException e) {
            //LOGGER.info(e);
        }
    }

    private List<TimeAtClub> retrieveTimeAtClubPeriods(Map<String, String> playerInfo, StaffMember staffMember) {
        int count = Integer.parseInt(playerInfo.get("count"));
        List<TimeAtClub> periodsAtClub = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            periodsAtClub.add(getTimeAtClubInfo(i, playerInfo, staffMember));
        }
        return periodsAtClub;
    }

    private void updateBasicStaffInfo(Map<String, String> playerInfo, StaffMember staffMember) {
        staffMember.setFirstName(playerInfo.get("firstname"));
        staffMember.setLastName(playerInfo.get("lastname"));
        staffMember.setProfile(playerInfo.get("profile"));
        staffMember.setFullKit(playerInfo.get("fullkit"));
        staffMember.setAwayKit(playerInfo.get("awaykit"));
        staffMember.setFullKitTwo(playerInfo.get("fullkit2"));
        staffMember.setAwayKitTwo(playerInfo.get("awaykit2"));
        staffMember.setShirt(playerInfo.get("shirt"));
        staffMember.setShorts(playerInfo.get("shorts"));
        staffMember.setBoots(playerInfo.get("boots"));
        staffMember.setTracksuit(playerInfo.get("tracksuit"));
        staffMember.setSocks(playerInfo.get("socks"));
        staffMember.setSocksTwo(playerInfo.get("socks2"));
        staffMember.setCorporate(playerInfo.get("corporate"));
        staffMember.setAtClub(PrimitiveUtils.parseBooleanSafe(playerInfo.get("current")));
        staffMember.setDateOfBirth(dateHelper.parseStandardDateFormat(playerInfo.get("dob")));
    }

    @Override
    public Fixture getDebutById(long playerId) {
        List<Fixture> appearances = fixtureRepository.getCompetitiveAppearances(playerId);
        if (appearances.isEmpty()) {
            return null;
        }
        return appearances.get(0);
    }

    @Override
    public StaffMember getStaffMemberById(long playerId, boolean getTimeAtClub) {
        Optional<StaffMember> staffMember = staffRepository.findById(playerId);
        if (staffMember.isPresent() && staffMember.get().isDeleted()) {
            throw new ItemDeletedException();
        }
        return staffMember.orElse(null);
    }

    @Override
    public List<TimeAtClub> getStaffPeriods(StaffMember person) {
        return timeAtClubRepository.findStaffPeriodsByStaffMember(person);
    }

    @Override
    public List<String> getStaffRoleValues() {
        return StaffRole.getStaffRoleValues();
    }

    private StaffRole getStaffRole(String name) {
        Optional<StaffRole> role = staffRoleRepository.findFirstByName(name);
        return role.orElse(null);
    }

    private TimeAtClub getTimeAtClubInfo(int i, Map<String, String> playerInfo,
                                         final StaffMember staffMember) {
        TimeAtClub item = new TimeAtClub();
        return updateTimeAtClubInfo(item, i, playerInfo, staffMember);
    }

    private TimeAtClub updateTimeAtClubInfo(TimeAtClub item, int i, Map<String, String> playerInfo,
                                         final StaffMember staffMember) {
        item.setStaffRole(getStaffRole(playerInfo.get("role" + i)));
        item.setStaffMember(staffMember);
        item.setStartDate(dateHelper.parseStandardDateFormat(playerInfo.get("startDate" + i)));
        item.setEndDate(dateHelper.parseStandardDateFormat(playerInfo.get("endDate" + i)));
        item.setStartSeason(PrimitiveUtils.parsePositiveIntSafe(playerInfo.get("firstSeason" + i)));
        item.setEndSeason(PrimitiveUtils.parsePositiveIntSafe(playerInfo.get("lastSeason" + i)));
        item.setOnLoan(PrimitiveUtils.parseBooleanSafe(playerInfo.get("onLoan" + i)));
        item.setTrialOnly(PrimitiveUtils.parseBooleanSafe(playerInfo.get("isTrial" + i)));
        return item;
    }

    @Override
    public StaffMember getStaffMember(long id) {
        Optional<StaffMember> staffMember = staffRepository.findById(id);
        if (staffMember.isPresent() && staffMember.get().isDeleted()) {
            throw new ItemDeletedException();
        }
        return staffMember.orElse(null);
    }

    @Override
    public List<StaffMember> getAllStaffMembers() {
        return staffRepository.findAllByOrderByLastNameAsc().stream()
                .filter(staff -> !staff.isDeleted())
                .collect(toList());
    }

    @Override
    public List<StaffMember> getCurrentSquadList() {
        return staffRepository.getCurrentStaff();
    }

    @Override
    public StaffMember getStaffMemberByFixtureAppearanceId(long id) {
        Optional<FixtureAppearance> fixtureAppearance = fixtureAppearanceRepository.findById(id);
        return fixtureAppearance.map(FixtureAppearance::getPlayer).orElse(null);
    }

    @Override
    public OppositionAppearance getOppositionAppearance(long id) {
        Optional<OppositionAppearance> appearance = oppositionAppearanceRepository.findById(id);
        return appearance.orElse(null);
    }

    @Override
    public List<StaffMember> getPlayersForSeason(int season, boolean includeTrialist, boolean includeManagement) {
        if (includeTrialist && includeManagement) {
            return staffRepository.findAllStaffForSeason(season);
        } else if (includeTrialist) {
            return staffRepository.findAllPlayersForSeason(season);
        } else if (includeManagement) {
            return staffRepository.findAllStaffForSeasonExcludingTrialists(season);
        }
        return staffRepository.findAllPlayersForSeasonExcludingTrialists(season);
    }

    @Override
    public List<StaffUpdate> getStaffMembersWithUpdate() {
        return Lists.newArrayList(staffUpdateRepository.findAll());
    }

    @Override
    public List<StaffMember> getStaffMembersByInitial(char letter) {
        List<StaffMember> staff = staffRepository.findDistinctByLastNameStartsWithOrderByLastNameAscFirstNameAsc( letter).stream()
                .filter(member -> !member.isDeleted())
                .collect(toList());

        List<OldStaffName> oldNames = getStaffMembersByOldNameInitial(letter);

        //TODO: Fix this.
        for (OldStaffName name : oldNames) {
            /*StaffMember player = name.getStaffMember();
            player.setFirstName(name.getFirstName());
            player.setLastName(name.getLastName());
            staff.add(player);*/
        }

        Collections.sort(staff);
        return staff;
    }

    private List<OldStaffName> getStaffMembersByOldNameInitial(char letter) {
        return oldStaffNameRepository.findAllByLastNameStartsWithOrderByLastNameAscFirstNameAsc(letter);
    }

    @Override
    public void editStaffMember(Map<String, String> playerInfo) {
        StaffMember staffMember = getStaffMemberById(Long.valueOf(playerInfo.get("id")), true);
        updateBasicStaffInfo(playerInfo, staffMember);
        staffRepository.save(staffMember);
    }

    @Override
    public void updatePenpic(long id, MultipartFile penpic) {
        StaffMember player = staffRepository.findById(id).orElseThrow(NotFoundException::new);
        handleUpload(penpic, player);
        staffRepository.save(player);
    }

    @Override
    public void updateCorporateSponsorImage(long id, MultipartFile sponsorImage) {
        StaffMember player = staffRepository.findById(id).orElseThrow(NotFoundException::new);
        handleSponsorUpload(sponsorImage, player);
        staffRepository.save(player);
    }

    @Override
    public void deleteCorporateSponsorImage(long id) {
        StaffMember player = staffRepository.findById(id).orElseThrow(NotFoundException::new);
        handleSponsorUpload(null, player);
        staffRepository.save(player);
    }

    private void handleSponsorUpload(MultipartFile fileStream, StaffMember staffMember) {
        try {
            if (null != fileStream && fileStream.getInputStream().available() > 0) {
                String filename = "sponsors/players/" + staffMember.getId() + '-' + staffMember.getFirstName() + '-' + staffMember.getLastName() + "." + Files.getFileExtension(fileStream.getOriginalFilename());
                List<ImageSize> sizes = Lists.newArrayList(new ImageSize(HEIGHT, 40), new ImageSize(ORIGINAL));
                fileService.uploadImage(fileStream, filename, sizes);
                staffMember.setCorporateSponsorImage(fileService.getUrl(new ImageSize(HEIGHT, 40).renameFile(filename)).orElse(null));
            } else {
                staffMember.setCorporateSponsorImage(null);
            }
        } catch (IOException e) {
            //LOGGER.info(e);
        }
    }

    @Override
    public void createTimeAtClub(long staffId,
                                 String role,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 int startSeason,
                                 int endSeason,
                                 boolean onLoan,
                                 boolean onTrial) {
        TimeAtClub period = TimeAtClub.builder()
                .staffRole(getStaffRole(role))
                .staffMember(getStaffMemberById(staffId, false))
                .startDate(startDate)
                .endDate(endDate)
                .startSeason(startSeason)
                .endSeason(endSeason)
                .onLoan(onLoan)
                .trialOnly(onTrial)
                .build();
        timeAtClubRepository.save(period);
    }

    @Override
    public void updateTimeAtClub(long timeAtClubId,
                                 long staffId,
                                 String role,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 int startSeason,
                                 int endSeason,
                                 boolean onLoan,
                                 boolean onTrial) {
        TimeAtClub period = TimeAtClub.builder()
                .id(timeAtClubId)
                .staffRole(getStaffRole(role))
                .staffMember(getStaffMemberById(staffId, false))
                .startDate(startDate)
                .endDate(endDate)
                .startSeason(startSeason)
                .endSeason(endSeason)
                .onLoan(onLoan)
                .trialOnly(onTrial)
                .build();
        timeAtClubRepository.save(period);
    }

    @Override
    public void deleteTimeAtClub(long id) {
        timeAtClubRepository.deleteById(id);
    }
}
