/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.greensnet.dao.*;
import net.greensnet.domain.*;
import net.greensnet.service.files.FileService;
import net.greensnet.util.DateHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional(transactionManager = "transactionManager")
@Commit
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class StaffServiceImplTest {

    @Autowired
    private FixtureRepository fixtureRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private OldStaffNameRepository oldStaffNameRepository;
    @Autowired
    private StaffRoleRepository staffRoleRepository;
    @Autowired
    private TimeAtClubRepository timeAtClubRepository;
    @Autowired
    private OppositionAppearanceRepository oppositionAppearanceRepository;
    @Autowired
    private FixtureAppearanceRepository fixtureAppearanceRepository;
    @Autowired
    private StaffUpdateRepository staffUpdateRepository;
    @Autowired DateHelper dateHelper;
    @Mock
    private FileService fileService;
    private StaffService staffService;
    private StaffRole playerRole;
    private StaffRole assistantRole;
    private StaffRole managerRole;

    @BeforeEach
    public void setUp() {
        staffService = new StaffServiceImpl(staffRepository, staffUpdateRepository,
                oldStaffNameRepository, staffRoleRepository, timeAtClubRepository,
                oppositionAppearanceRepository, fixtureAppearanceRepository,
                fixtureRepository, fileService, dateHelper);
        playerRole = StaffRole.builder().name("Player").build();
        assistantRole = StaffRole.builder().name("Assistant Manager").build();
        managerRole = StaffRole.builder().name("Manager").build();
        staffRoleRepository.save(playerRole);
        staffRoleRepository.save(managerRole);
        staffRoleRepository.save(assistantRole);
    }

    @Test
    public void testWriteAndRetrieveItem() {
        staffRepository.save(createStaffMember("Gary", "McCann", true, 4L));

        StaffMember item = staffService.getStaffMember(4L);
        assertEquals("Wrong first name", "Gary", item.getFirstName());
        assertEquals("Wrong last name", "McCann", item.getLastName());
        assertTrue(item.isAtClub());
    }

    @Test
    public void testWriteAndRetrieveStaffUpdate() {
        staffRepository.save(createStaffMember("Gary", "McCann", true, 4L));
        staffService.createStaffUpdate(4L, LocalDate.now(), "occupation", "location", "", "", "", "", "", "", "", "", "", "", "");
        StaffUpdate item = staffService.getStaffUpdate(5L);
        assertEquals("Wrong occupation", "occupation", item.getOccupation());
        assertEquals("Wrong location", "location", item.getHomeLocation());
    }

    @Test
    public void testWriteAndRetrieveStaffUpdateByStaffId() {
        staffRepository.save(createStaffMember("Gary", "McCann", true, 4L));
        staffService.createStaffUpdate(4L, LocalDate.now(), "occupation", "location", "", "", "", "", "", "", "", "", "", "", "");
        StaffUpdate item = staffService.getStaffUpdateByStaffId(4L);
        assertEquals("Wrong occupation", "occupation", item.getOccupation());
        assertEquals("Wrong location", "location", item.getHomeLocation());
    }

    @Test
    public void testEditStaffUpdate() {
        staffRepository.save(createStaffMember("Gary", "McCann", true, 1L));
        staffService.createStaffUpdate(1L, LocalDate.now(), "occupation", "location", "", "", "", "", "", "", "", "", "", "", "");
        staffService.editStaffUpdate(5L, 1L, "newoccupation", "newlocation", "", "", "", "", "", "", "", "", "", "", "");
        StaffUpdate item = staffService.getStaffUpdate(5L);
        assertEquals("Wrong occupation", "newoccupation", item.getOccupation());
        assertEquals("Wrong location", "newlocation", item.getHomeLocation());
    }

    @Test
    public void testGetStaffMembersWithUpdate() {
        staffRepository.save(createStaffMember("Gary", "McCann", true, 1L));
        staffRepository.save(createStaffMember("Freddie", "Hyatt", true, 2L));
        staffService.createStaffUpdate(1L, LocalDate.now(), "occupation", "location", "", "", "", "", "", "", "", "", "", "", "");
        List<StaffUpdate> items = staffService.getStaffMembersWithUpdate();
        assertEquals(items.size(), 1);
    }

 /*   @Test
    public void testGetStaffMembersWithUpdateNone() {
        List<StaffUpdate> items = staffService.getStaffMembersWithUpdate();
        assertTrue("Expected an empty list", items.isEmpty());
    }

    @Test
    public void testGetStaffRoleValues() {
        List<String> list = staffService.getStaffRoleValues();
        assertFalse("Expected a non-empty list", list.isEmpty());
        assertTrue("Player should be in list", list.contains("Player"));
        assertTrue("Manager should be in list", list.contains("Manager"));
    }

    @Test
    public void testGetAllStaffMembersNone() {
        List<StaffMember> list = staffService.getAllStaffMembers();
        assertTrue(list.isEmpty());
    }

    @Test
    public void testGetAllStaffMembersOne() {
        staffRepository.save(createStaffMember("Gary", "McCann", true, 1L));
        List<StaffMember> list = staffService.getAllStaffMembers();
        assertEquals("Expected one item", 1, list.size());
    }

    @Test
    public void testGetAllStaffMembersMultiple() {
        staffRepository.save(createStaffMember("Gary", "McCann", true, 1L));
        staffRepository.save(createStaffMember("Freddie", "Hyatt", true, 2L));
        staffRepository.save(createStaffMember("Jason", "McKoy", true, 3L));

        List<StaffMember> list = staffService.getAllStaffMembers();
        assertEquals("Expected three items", 3, list.size());
    }

    @Test
    public void testGetStaffMembersByInitial() {
        staffRepository.save(createStaffMember("Gary", "McCann", true, 1L));
        staffRepository.save(createStaffMember("Freddie", "Hyatt", true, 2L));
        staffRepository.save(createStaffMember("Jason", "McKoy", true, 3L));

        List<StaffMember> list = staffService.getStaffMembersByInitial('M');
        assertEquals("Expected two items", 2, list.size());
        list = staffService.getStaffMembersByInitial('H');
        assertEquals("Expected one item", 1, list.size());
    }

    @Test
    @Disabled("Fix the retrieval of staff members by old name")
    public void testGetStaffMembersByInitialOldName() {
        OldStaffName oldName = OldStaffName.builder().lastName("Vargas").build();
        StaffMember a = staffRepository.save(createStaffMember(oldName));
        oldStaffNameRepository.save(oldName);

        List<StaffMember> list = staffService.getStaffMembersByInitial('C');
        assertEquals("Expected one item", 1, list.size());
        list = staffService.getStaffMembersByInitial('V');
        assertEquals("Expected one item", 1, list.size());
    }

    @Test
    public void testGetStaffMembersByInitialNoStaff() {
        List<StaffMember> list = staffService.getStaffMembersByInitial('Z');
        assertTrue("Expected empty list", list.isEmpty());
    }

    @Test
    public void testGetStaffMembersBySeasonNone() {
        TimeAtClub timePeriod = createTimeAtClubObject(2011, 2011, true, playerRole);
        staffRepository.save(
                createStaffMember(timePeriod, "Unknown", "Trialist", false, 4L));
        timeAtClubRepository.saveAll(Lists.newArrayList(timePeriod));
        List<StaffMember> list = staffService.getPlayersForSeason(2011, false, false);
        assertTrue("Expected empty list", list.isEmpty());
    }

    @Test
    public void testGetStaffMembersBySeasonMultiple() {
        StaffRole role = staffRoleRepository.findFirstByName("Assistant Manager").orElse(null);
        TimeAtClub timePeriod = createTimeAtClubObject(2011, 2011, false, role);
        StaffMember staff = createStaffMember(timePeriod, "Freddie", "Hyatt", true, 4L);
        staffRepository.save(staff);
        timeAtClubRepository.save(timePeriod);
        staffRepository.save(staff);

        List<StaffMember> list = staffService.getPlayersForSeason(2011, false, true);

        assertEquals("Expected one item", 1, list.size());
    }

    @Test
    public void testGetStaffMembersBySeasonOne() {
        StaffRole role = staffRoleRepository.findFirstByName("Assistant Manager").orElse(null);
        TimeAtClub timePeriod = createTimeAtClubObject(2001, 2001, false, null);
        StaffMember staff = createStaffMember(timePeriod, "Freddie", "Hyatt", true, 4L);
        staffRepository.save(staff);
        timeAtClubRepository.save(timePeriod);
        staffRepository.save(staff);


        TimeAtClub timePeriod2 = createTimeAtClubObject(2001, 2001, true, role);
        StaffMember staff2 = createStaffMember(timePeriod2, "Unknown", "Trialist", false, 6L);
        staffRepository.save(staff2);
        timeAtClubRepository.save(timePeriod2);
        staffRepository.save(staff2);

        List<StaffMember> list = staffService.getPlayersForSeason(2011, true, true);
        assertEquals("Expected two items", 2, list.size());
    }

    @Test
    public void testGetStaffMembersBySeasonTrialist() {
        TimeAtClub timePeriod = createTimeAtClubObject(2011, 2011, true, playerRole);
        staffRepository.save(createStaffMember(timePeriod, "Unknown", "Trialist", false, 4L));
        timeAtClubRepository.saveAll(Lists.newArrayList(timePeriod));
        List<StaffMember> list = staffService.getPlayersForSeason(2011, false, false);
        assertTrue("Expected empty list", list.isEmpty());
    }

    @Test
    public void testGetCurrentStaff() {
        staffRepository.save(createStaffMember("Gary", "McCann", true, 4L));
        staffRepository.save(createStaffMember("Freddie", "Hyatt", false, 5L));
        staffRepository.save(createStaffMember("Jason", "McKoy", false, 6L));
        List<StaffMember> list = staffService.getCurrentSquadList();
        StaffMember a = staffService.getStaffMember(4L);
        assertEquals("Expected one item", 1, list.size());
    }

    @Test
    public void testEditStaffWithoutInitialOrEditedTimePeriods() {
        staffRepository.save(createStaffMember("Gary", "McCann", true, 4L));

        Hashtable<String, String> playerInfo = new Hashtable<>();
        playerInfo.put("id", "4");
        playerInfo.put("firstname", "Gerry");
        playerInfo.put("lastname", "Hill");
        playerInfo.put("current", "false");
        playerInfo.put("count", "0");
        staffService.editStaffMember(playerInfo);

        StaffMember item = staffService.getStaffMember(4L);
        assertEquals("Wrong first name", "Gerry", item.getFirstName());
        assertEquals("Wrong last name", "Hill", item.getLastName());
        assertFalse("Expected player currently not at club", item.isAtClub());
        assertEquals("Wrong number of items", 0, item.getTimeAtClub().size());
    }

    @Test
    public void testEditStaffWithoutInitialTimePeriodsButAddedLater() {
        staffRepository.save(createStaffMember("Gary", "McCann", true, 4L));

        Map<String, String> playerInfo = Maps.newHashMap();
        playerInfo.put("id", "4");
        playerInfo.put("firstname", "Gerry");
        playerInfo.put("lastname", "Hill");
        playerInfo.put("current", "false");
        playerInfo.put("count", "1");
        playerInfo.put("role0", "Player");
        playerInfo.put("firstSeason0", "2010");
        playerInfo.put("lastSeason0", "2012");
        playerInfo.put("startDate0", "2010-11-11");
        playerInfo.put("endDate0", "2012-11-11");
        playerInfo.put("onLoan0", "false");
        playerInfo.put("isTrial0", "false");
        staffService.editStaffMember(playerInfo);

        StaffMember item = staffService.getStaffMemberById(4, true);
        assertEquals("Wrong first name", "Gerry", item.getFirstName());
        assertEquals("Wrong last name", "Hill", item.getLastName());
        assertFalse("Expected player currently not at club", item.isAtClub());
        assertEquals("Wrong number of items", 1, item.getTimeAtClub().size());
        assertEquals("Wrong startDate", LocalDate.of(2010,11, 11),
                item.getTimeAtClub().get(0).getStartDate());
    }

    @Test
    public void testEditStaffAdditionalRole() {
        TimeAtClub timePeriod = createTimeAtClubObject(2011, 2011, false, assistantRole);
        staffRepository.save(createStaffMember(timePeriod, "Freddie", "Hyatt", true, 4L));

        Hashtable<String, String> playerInfo = new Hashtable<>();
        playerInfo.put("id", "4");
        playerInfo.put("firstname", "Gary");
        playerInfo.put("lastname", "McCann");
        playerInfo.put("current", "true");
        playerInfo.put("count", "4");
        playerInfo.put("role0", "Player");
        playerInfo.put("firstSeason0", "2010");
        playerInfo.put("lastSeason0", "2012");
        playerInfo.put("startDate0", "2010-11-11");
        playerInfo.put("endDate0", "2012-11-11");
        playerInfo.put("onLoan0", "false");
        playerInfo.put("isTrial0", "false");
        playerInfo.put("role1", "Manager");
        playerInfo.put("firstSeason1", "aaa");
        playerInfo.put("lastSeason1", "aaa");
        playerInfo.put("startDate1", "2010:11:11");
        playerInfo.put("endDate1", "2012:11:11");
        playerInfo.put("onLoan1", "true");
        playerInfo.put("isTrial1", "true");
        playerInfo.put("role2", "Coach");
        playerInfo.put("firstSeason3", "2000");
        playerInfo.put("lastSeason3", "2002");
        playerInfo.put("startDate3", "2000-11-11");
        playerInfo.put("endDate3", "2002-11-11");
        playerInfo.put("onLoan3", "false");
        playerInfo.put("role3", "Player");
        staffService.editStaffMember(playerInfo);

        StaffMember item = staffService.getStaffMemberById(4, true);
        assertEquals("Wrong first name", "Gary", item.getFirstName());
        assertEquals("Wrong last name", "McCann", item.getLastName());
        assertTrue("Expected player currently at club", item.isAtClub());
        assertEquals("Wrong number of items", 4, item.getTimeAtClub().size());
    }

    @Test
    public void testEditStaffAdditionalRoleNoneInitial() {
        staffRepository.save(createStaffMember("Gary", "McCann", true, 4L));

        Hashtable<String, String> playerInfo = new Hashtable<>();
        playerInfo.put("id", "4");
        playerInfo.put("firstname", "Gary");
        playerInfo.put("lastname", "McCann");
        playerInfo.put("current", "true");
        playerInfo.put("count", "4");
        playerInfo.put("role0", "Player");
        playerInfo.put("firstSeason0", "2010");
        playerInfo.put("lastSeason0", "2012");
        playerInfo.put("startDate0", "2010-11-11");
        playerInfo.put("endDate0", "2012-11-11");
        playerInfo.put("onLoan0", "false");
        playerInfo.put("isTrial0", "false");
        playerInfo.put("role1", "Manager");
        playerInfo.put("firstSeason1", "aaa");
        playerInfo.put("lastSeason1", "aaa");
        playerInfo.put("startDate1", "2010:11:11");
        playerInfo.put("endDate1", "2012:11:11");
        playerInfo.put("onLoan1", "true");
        playerInfo.put("isTrial1", "true");
        playerInfo.put("role2", "Coach");
        playerInfo.put("firstSeason3", "2000");
        playerInfo.put("lastSeason3", "2002");
        playerInfo.put("startDate3", "2000-11-11");
        playerInfo.put("endDate3", "2002-11-11");
        playerInfo.put("onLoan3", "false");
        playerInfo.put("role3", "Player");
        staffService.editStaffMember(playerInfo);

        StaffMember item = staffService.getStaffMemberById(4, true);
        assertEquals("Wrong first name", "Gary", item.getFirstName());
        assertEquals("Wrong last name", "McCann", item.getLastName());
        assertTrue("Expected player currently at club", item.isAtClub());
        assertEquals("Wrong number of items", 4, item.getTimeAtClub().size());
    }

    @Test
    public void testEditStaffRemoveRole() {
        StaffRole role = staffRoleRepository.findFirstByName("Assistant Manager").orElse(null);
        TimeAtClub timePeriod = createTimeAtClubObject(2011, 2011, false, role);
        TimeAtClub timePeriod2 = createTimeAtClubObject(2000, 2002, false, playerRole);
        TimeAtClub timePeriod3 = createTimeAtClubObject(2009, 2011, false, playerRole);
        staffRepository.save(createStaffMember(Lists.newArrayList(timePeriod, timePeriod2, timePeriod3),
                "Freddie", "Hyatt", true, 4L));

        Hashtable<String, String> playerInfo = new Hashtable<>();
        playerInfo.put("id", "4");
        playerInfo.put("firstname", "Gary");
        playerInfo.put("lastname", "McCann");
        playerInfo.put("current", "true");
        playerInfo.put("count", "2");
        playerInfo.put("role0", "Player");
        playerInfo.put("firstSeason0", "2010");
        playerInfo.put("lastSeason0", "2012");
        playerInfo.put("startDate0", "2010-11-11");
        playerInfo.put("endDate0", "2012-11-11");
        playerInfo.put("onLoan0", "false");
        playerInfo.put("isTrial0", "false");
        playerInfo.put("role1", "Manager");
        playerInfo.put("firstSeason1", "aaa");
        playerInfo.put("lastSeason1", "aaa");
        playerInfo.put("startDate1", "2010:11:11");
        playerInfo.put("endDate1", "2012:11:11");
        playerInfo.put("onLoan1", "true");
        playerInfo.put("isTrial1", "true");
        staffService.editStaffMember(playerInfo);

        StaffMember item = staffService.getStaffMemberById(4, true);
        assertEquals("Wrong first name", "Gary", item.getFirstName());
        assertEquals("Wrong last name", "McCann", item.getLastName());
        assertTrue("Expected player currently at club", item.isAtClub());
        assertEquals("Wrong number of items", 2, item.getTimeAtClub().size());
    }

    @Test
    public void testEditStaffSameRoles() {
        TimeAtClub timePeriod = createTimeAtClubObject(2010, 2012, false, playerRole);
        TimeAtClub timePeriod2 = createTimeAtClubObject(2000, 2002, false, assistantRole);
        TimeAtClub timePeriod3 = createTimeAtClubObject(2009, 2011, false, assistantRole);
        staffRepository.save(createStaffMember(Lists.newArrayList(timePeriod, timePeriod2, timePeriod3),
                "McCann", "Gary", true, 4L));

        Hashtable<String, String> playerInfo = new Hashtable<>();
        playerInfo.put("id", "4");
        playerInfo.put("firstname", "Gary");
        playerInfo.put("lastname", "McNeil");
        playerInfo.put("current", "true");
        playerInfo.put("count", "3");
        playerInfo.put("role0", "Player");
        playerInfo.put("firstSeason0", "2010");
        playerInfo.put("lastSeason0", "2012");
        playerInfo.put("onLoan0", "false");
        playerInfo.put("isTrial0", "false");
        playerInfo.put("role1", "Manager");
        playerInfo.put("firstSeason1", "aaa");
        playerInfo.put("lastSeason1", "aaa");
        playerInfo.put("startDate1", "2010:11:11");
        playerInfo.put("endDate1", "2012:11:11");
        playerInfo.put("onLoan1", "true");
        playerInfo.put("isTrial1", "true");
        playerInfo.put("role2", "Coach");
        staffService.editStaffMember(playerInfo);

        StaffMember item = staffService.getStaffMemberById(4, true);
        assertEquals("Wrong first name", "Gary", item.getFirstName());
        assertEquals("Wrong last name", "McNeil", item.getLastName());
        assertTrue("Expected player currently at club", item.isAtClub());
        assertEquals("Wrong number of items", 3, item.getTimeAtClub().size());
    }

    @Test
    public void testGetStaffPeriods() {
        TimeAtClub timePeriod = createTimeAtClubObject(2011, 2011, false, assistantRole);
        TimeAtClub timePeriod2 = createTimeAtClubObject(2000, 2002, false, playerRole);
        TimeAtClub timePeriod3 = createTimeAtClubObject(2009, 2011, false, playerRole);
        staffRepository.save(createStaffMember(Lists.newArrayList(timePeriod, timePeriod2, timePeriod3),
                "Freddie", "Hyatt", true, 4L));
        timeAtClubRepository.saveAll(Lists.newArrayList(timePeriod, timePeriod2, timePeriod3));
        StaffMember item = staffService.getStaffMemberById(4L, true);
        List<TimeAtClub> items = staffService.getStaffPeriods(item);
        assertEquals("Wrong number of items", 1, items.size());
    }
*/

    private TimeAtClub createTimeAtClubObject(int startSeason,
                                              int endSeason,
                                              boolean onTrialOnly,
                                              StaffRole playerRole) {
        return TimeAtClub.builder()
                .startSeason(startSeason)
                .endSeason(endSeason)
                .trialOnly(onTrialOnly)
                .staffRole(playerRole)
                .build();
    }

    private StaffMember createStaffMember(String firstName,
                                          String lastName,
                                          boolean isAtClub,
                                          long id) {
        return StaffMember.builder().firstName(firstName)
                .lastName(lastName)
                .isAtClub(isAtClub)
                .id(id)
                .build();
    }

    private StaffMember createStaffMember(OldStaffName oldName) {
        return StaffMember.builder().firstName("Craig")
                .lastName("Carby")
                .isAtClub(true)
                .id(4L)
                .oldNames(Sets.newHashSet(oldName))
                .build();
    }

    private StaffMember createStaffMember(TimeAtClub timePeriod,
                                          String firstName,
                                          String lastName,
                                          boolean isAtClub,
                                          long id) {
        return StaffMember.builder().firstName(firstName)
                .lastName(lastName)
                .isAtClub(isAtClub)
                .id(id)
                .timeAtClub(Lists.newArrayList(timePeriod))
                .build();
    }

    private StaffMember createStaffMember(List<TimeAtClub> timePeriods,
                                          String firstName,
                                          String lastName,
                                          boolean isAtClub,
                                          long id) {
        return StaffMember.builder().firstName(firstName)
                .lastName(lastName)
                .isAtClub(isAtClub)
                .id(id)
                .timeAtClub(timePeriods)
                .build();
    }

}
