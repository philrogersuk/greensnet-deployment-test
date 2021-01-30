/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.club.dao.ClubRepository;
import net.greensnet.club.dao.OldClubNameRepository;
import net.greensnet.club.domain.Club;
import net.greensnet.club.domain.ClubEntityToDomainConverter;
import net.greensnet.club.event.producer.ClubUpdateProducer;
import net.greensnet.club.service.ClubService;
import net.greensnet.club.service.ClubServiceImpl;
import net.greensnet.competition.CompetitionService;
import net.greensnet.competition.CompetitionServiceImpl;
import net.greensnet.competition.dao.CompetitionRepository;
import net.greensnet.competition.domain.CompetitionEntityToDomainConverter;
import net.greensnet.dao.*;
import net.greensnet.domain.*;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static net.greensnet.competition.domain.CompetitionType.LEAGUE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Disabled
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional(transactionManager = "transactionManager")
@Commit
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class FixtureServiceImplTest {

    public static final String HOME_VENUE = "HOME";
    public static final String AWAY_VENUE = "AWAY";
    public static final String MCCANN = "McCann";
    public static final String DAVE = "Dave";
    public static final String JONES = "Jones";
    public static final String ERIC = "Eric";
    public static final String CANTONA = "Cantona";
    public static final String RANDOM_STRING = "asdfa";
    @Autowired
    private StaffRepository staffRepository;
    @Mock
    private TwitterService twitterService;
    private CompetitionService competitionService;
    private ClubService clubService;
    @Autowired
    private FixtureRepository fixtureRepository;
    @Autowired
    private FixtureVenueRepository fixtureVenueRepository;
    @Autowired
    private FixtureAppearanceRepository fixtureAppearanceRepository;
    @Autowired
    private OppositionAppearanceRepository oppositionAppearanceRepository;
    @Autowired
    private FixtureGoalRepository fixtureGoalRepository;
    @Autowired
    private OppositionGoalRepository oppositionGoalRepository;
    @Autowired private ClubRepository clubRepository;
    @Autowired private OldClubNameRepository oldClubNameRepository;
    @Autowired private CompetitionRepository competitionRepository;
    @Autowired private DateHelper dateHelper;
    @Autowired private ClubEntityToDomainConverter clubConverter;
    @Autowired private ClubUpdateProducer clubUpdateNotifier;
    private FixtureService manager;

    @BeforeEach
    public void setUp() {
        clubService = new ClubServiceImpl(clubRepository, clubConverter, oldClubNameRepository, dateHelper, clubUpdateNotifier, null);
        competitionService = new CompetitionServiceImpl(competitionRepository, new CompetitionEntityToDomainConverter());
        manager = new FixtureServiceImpl(competitionService, twitterService,
                fixtureRepository, fixtureVenueRepository, fixtureAppearanceRepository,
                oppositionAppearanceRepository, fixtureGoalRepository, oppositionGoalRepository);

        fixtureVenueRepository.save(FixtureVenue.builder().id(2L).venue(HOME_VENUE).build());
        fixtureVenueRepository.save(FixtureVenue.builder().id(3L).venue(AWAY_VENUE).build());
        //4L
        competitionService.createCompetition("League", "Competition", LEAGUE);
        //5L
        Club club = Club.builder().internalName("Harrow").build();
    }

    @Transactional
    @Test
    public void testWriteAndRetrieveItem() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, -1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        Fixture item = manager.getFixtureById(6L, false, false);
        assertEquals(2000, item.getSeason());
        assertEquals(-1, item.getAttendance());
        assertNull(item.getReport());
    }

    @Transactional
    @Test
    public void testWriteAndRetrieveNoAppsAndGoals() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, -1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        Fixture item = manager.getFixtureById(6L, true, true);
        assertEquals(2000, item.getSeason());
        assertEquals(-1, item.getAttendance());
        assertNull(item.getReport());
    }

    @Transactional
    @Test
    public void testUpdateFixture() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMonth = now.plusMonths(1);
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        FixtureVenue awayVenue = manager.getVenueFromString(AWAY_VENUE);
        manager.createFixture(2000, 4L, venue, -1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, "", null, null,
                null, null, null, null, null, null, null);
        manager.updateFixture(6L, 2001, 4L, awayVenue, -1, nextMonth, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, 234,
                null, null, null, null, null, null, null, null, null, null);
        Fixture item = manager.getFixtureById(6L, false, false);
        assertEquals(2001, item.getSeason());
        assertEquals(234, item.getAttendance());
        assertNull(item.getReport());
    }

    @Transactional
    @Test
    public void testUpdateFixtureHomeVideo() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);

        manager.createFixture(2000, 4L, venue, -1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, "", null, null, null);
        manager.updateFixture(6L, 2000, 4L, venue, 1, now, 5L, 1, 0, -1, -1, -1, -1, -1, null, false, 234,
                null, null, null, null, null, null, "asaaa", null, null, null);

        verify(twitterService, times(1)).tweet("Highlights: Hendon 1-0 Harrow", "http://www.hendonfc.net/Reports?id=6#highlights");
    }

    @Transactional
    @Test
    public void testUpdateFixtureAwayVideo() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(AWAY_VENUE);

        manager.createFixture(2000, 4L, venue, -1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, "", null, null, null);
        manager.updateFixture(6L, 2000, 4L, venue, 1, now, 5L, 1, 0, -1, -1, -1, -1, -1, null, false, 234,
                null, null, null, null, null, null, "asaaa", null, null, null);

        verify(twitterService, times(1)).tweet("Highlights: Harrow 0-1 Hendon", "http://www.hendonfc.net/Reports?id=6#highlights");
    }

    @Transactional
    @Test
    public void testUpdateFixtureSameVideo() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);

        manager.createFixture(2000, 4L, venue, -1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, "asaaa", null, null, null);
        manager.updateFixture(6L, 2000, 4L, venue, 1, now, 5L, 1, 0, -1, -1, -1, -1, -1, null, false, 234,
                null, null, null, null, null, null, "asaaa", null, null, null);

        verify(twitterService, times(0)).tweet(anyString(), anyString());
    }


    @Transactional
    @Test
    public void testUpdateFixtureNulledVideo() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(AWAY_VENUE);

        manager.createFixture(2000, 4L, venue, -1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, "asaaa", null, null, null);
        manager.updateFixture(6L, 2000, 4L, venue, -1, now, 5L, 1, 0, -1, -1, -1, -1, -1, null, false, 234,
                null, null, null, null, null, null, null, null, null, null);

        verify(twitterService, times(0)).tweet(anyString(), anyString());
    }

    @Transactional
    @Test
    public void testGetFixturesBySeasonNone() {
        assertTrue(manager.getFixturesBySeason(2000).isEmpty());
    }

    @Transactional
    @Test
    public void testGetFixturesBySeasonOne() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, -1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        List<Fixture> fixtures = manager.getFixturesBySeason(2000);
        assertEquals(1, fixtures.size());
    }

    @Transactional
    @Test
    public void testGetFixturesBySeasonMultiple() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        FixtureVenue venue2 = manager.getVenueFromString(AWAY_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, null, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue2, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue2, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        List<Fixture> fixtures = manager.getFixturesBySeason(2000);
        assertEquals(4, fixtures.size());
        assertNull(fixtures.get(3).getKickOff());
    }

    @Transactional
    @Test
    public void testGetFutureTicketedFixturesNone() {
        assertTrue(manager.getFutureTicketedFixtures().isEmpty());
    }

    @Transactional
    @Test
    public void testGetFutureTicketedFixturesOne() {
        LocalDateTime future = LocalDateTime.now().plusMinutes(10000);
        LocalDateTime future2 = LocalDateTime.now().plusMinutes(20000);
        LocalDateTime past = LocalDateTime.now().minusMinutes(20000);

        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, future, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, "www.a.com");
        manager.createFixture(2000, 4L, venue, 1, future2, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, future2, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, "");
        manager.createFixture(2000, 4L, venue, 1, past, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, "www.a.com");
        List<Fixture> fixtures = manager.getFutureTicketedFixtures();
        assertEquals(1, fixtures.size());
    }

    @Transactional
    @Test
    public void testGetFutureTicketedFixturesMultiple() {
        LocalDateTime future = LocalDateTime.now().plusMinutes(10000);
        LocalDateTime future2 = LocalDateTime.now().plusMinutes(20000);
        LocalDateTime future3 = LocalDateTime.now().plusMinutes(10000);
        FixtureVenue venue = manager.getVenueFromString("Home");
        FixtureVenue venue2 = manager.getVenueFromString("Away");
        manager.createFixture(2000, 4L, venue, 1, future, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, "a");
        manager.createFixture(2000, 4L, venue, 1, null, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, "a");
        manager.createFixture(2000, 4L, venue2, 1, future2, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, "a");
        manager.createFixture(2000, 4L, venue2, 1, future3, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, "a");
        List<Fixture> fixtures = manager.getFixturesBySeason(2000);
        assertEquals(4, fixtures.size());
        assertNull(fixtures.get(3).getKickOff());
    }

    @Transactional
    @Test
    public void testGetMonthlyFixturesBySeasonNone() {
        assertTrue(manager.getMonthlyFixturesBySeason(2000).isEmpty());
    }

    @Transactional
    @Test
    public void testGetMonthlyFixturesBySeasonOne() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        List<FixtureListMonth> fixtures = manager.getMonthlyFixturesBySeason(2000);
        assertEquals(1, fixtures.size());
        assertEquals(1, fixtures.get(0).getFixtures().size());
    }

    @Transactional
    @Test
    public void testGetMonthlyFixturesBySeasonMultiple() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMonth = LocalDateTime.now().plusMonths(1);
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, nextMonth, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null,
                null, null, null, null, null, null, null, null);
        List<FixtureListMonth> fixtures = manager.getMonthlyFixturesBySeason(2000);
        assertEquals(2, fixtures.size());
        assertEquals(2, fixtures.get(0).getFixtures().size());
        assertEquals(1, fixtures.get(1).getFixtures().size());
    }

    @Transactional
    @Test
    public void testGetMonthlyFixturesBySeasonOverYear() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextYear = LocalDateTime.now().plusYears(1);
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, nextYear, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null,
                null, null, null, null, null, null, null, null);
        List<FixtureListMonth> fixtures = manager.getMonthlyFixturesBySeason(2000);
        assertEquals(2, fixtures.size());
        assertEquals(2, fixtures.get(0).getFixtures().size());
        assertEquals(1, fixtures.get(1).getFixtures().size());
    }

    @Transactional
    @Test
    public void testGetMonthlyFixturesBySeasonIncludesTBA() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMonth = LocalDateTime.now().plusMonths(1);
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, nextMonth, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null,
                null, null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, null, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        List<FixtureListMonth> fixtures = manager.getMonthlyFixturesBySeason(2000);
        assertEquals(3, fixtures.size());
        assertEquals("Unknown", fixtures.get(2).getMonth());
        assertEquals(1, fixtures.get(2).getFixtures().size());
    }

    @Transactional
    @Test
    public void testGetHomeFixturesNone() {
        assertTrue(manager.getHomeFixturesBySeason(2000).isEmpty());
    }

    @Transactional
    @Test
    public void testGetHomeFixturesOne() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        List<Fixture> fixtures = manager.getHomeFixturesBySeason(2000);
        assertEquals(1, fixtures.size());
    }

    @Transactional
    @Test
    public void testGetHomeFixturesMultiple() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        FixtureVenue venue2 = manager.getVenueFromString(AWAY_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue2, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue2, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        List<Fixture> fixtures = manager.getHomeFixturesBySeason(2000);
        assertEquals(2, fixtures.size());
    }

    @Transactional
    @Test
    public void testGetHomeFixturesTBALast() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, null, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        List<Fixture> fixtures = manager.getHomeFixturesBySeason(2000);
        assertEquals(4, fixtures.size());
        assertNull(fixtures.get(3).getKickOff());
    }

    @Transactional
    @Test
    public void testGetLastFixture() {
        LocalDateTime future = LocalDateTime.now().plusMonths(1);
        LocalDateTime past = LocalDateTime.now().minusMonths(1);
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        FixtureVenue venue2 = manager.getVenueFromString(AWAY_VENUE);
        manager.createFixture(2000, 4L, venue, 1, past, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue2, 1, future, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null,
                null, null, null, null, null, null, null, null);
        Fixture fixture = manager.getLastFixture();
        assertEquals(HOME_VENUE, fixture.getVenue().getVenue());
    }

    @Transactional
    @Test
    public void testGetLastHighlightsYoutube() {
        LocalDateTime past = LocalDateTime.now().minusMonths(1);
        LocalDateTime past2 = LocalDateTime.now().minusMonths(2);
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        FixtureVenue venue2 = manager.getVenueFromString(AWAY_VENUE);
        manager.createFixture(2000, 4L, venue, 1, past, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, "id", null, null);
        manager.createFixture(2000, 4L, venue2, 1, past2, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null,
                null, null, null, null, null, null, null, null);
        Fixture fixture = manager.getLastHighlights();
        assertNotNull(fixture);
    }

    @Transactional
    @Test
    public void testGetLastHighlightsHFCTV() {
        LocalDateTime past = LocalDateTime.now().minusMonths(1);
        LocalDateTime past2 = LocalDateTime.now().minusMonths(2);
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        FixtureVenue venue2 = manager.getVenueFromString(AWAY_VENUE);
        manager.createFixture(2000, 4L, venue, 1, past, 5L, 1, 1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, "id", null, null);
        manager.createFixture(2000, 4L, venue2, 1, past2, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null,
                null, null, null, null, null, null, null, null);
        Fixture fixture = manager.getLastHighlights();
        assertEquals(1, fixture.getHendonScore());
    }

    @Transactional
    @Test
    public void testGetNextFixture() {
        LocalDateTime future = LocalDateTime.now().plusMonths(1);
        LocalDateTime past = LocalDateTime.now().minusMonths(1);
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        FixtureVenue venue2 = manager.getVenueFromString(AWAY_VENUE);
        manager.createFixture(2000, 4L, venue, 1, past, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue2, 1, future, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null,
                null, null, null, null, null, null, null, null);
        Fixture fixture = manager.getNextFixture();
        assertEquals(AWAY_VENUE, fixture.getVenue().getVenue());
    }

    @Transactional
    @Test
    public void testGetNextFixtureNone() {
        LocalDateTime past = LocalDateTime.now().minusMonths(1);
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, past, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, past, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        assertNull(manager.getNextFixture());
    }

    @Transactional
    @Test
    public void testGetLastFixtureNone() {
        LocalDateTime future = LocalDateTime.now().plusMonths(1);
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, future, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null,
                null, null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, future, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null,
                null, null, null, null, null, null, null, null);
        assertNull(manager.getLastFixture());
    }

    @Transactional
    @Test
    public void testGetValidSeasonsNone() {
        assertTrue(manager.getValidSeasons().isEmpty());
    }

    @Transactional
    @Test
    public void testGetValidSeasonsOne() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        List<Integer> seasons = manager.getValidSeasons();
        assertEquals(1, seasons.size());
        assertEquals(2000, seasons.get(0).intValue());
    }

    @Transactional
    @Test
    public void testGetValidSeasonsMultiple() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2009, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.createFixture(2035, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        List<Integer> seasons = manager.getValidSeasons();
        assertEquals(3, seasons.size());
        assertEquals(2000, seasons.get(0).intValue());
        assertEquals(2009, seasons.get(1).intValue());
        assertEquals(2035, seasons.get(2).intValue());
    }

    @Transactional
    @Test
    public void testSetTeamsNone() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        List<FixtureAppearance> homeApps = new ArrayList<>();
        List<OppositionAppearance> awayApps = new ArrayList<>();
        manager.setTeams(6L, homeApps, awayApps);

        assertTrue(manager.getHendonTeam(6L).isEmpty());
        assertTrue(manager.getOppositionTeam(6L).isEmpty());
    }

    @Transactional
    @Test
    public void testSetTeamsNull() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.setTeams(6L, null, null);

        assertTrue(manager.getHendonTeam(6L).isEmpty());
        assertTrue(manager.getOppositionTeam(6L).isEmpty());
    }

    @Transactional
    @Test
    public void testSetTeamsNeverSet() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);

        assertTrue(manager.getHendonTeam(6L).isEmpty());
        assertTrue(manager.getOppositionTeam(6L).isEmpty());
    }

    @Transactional
    @Test
    public void testSetTeamsOneEach() {
        staffRepository.save(createStaffMember());
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);

        FixtureAppearance app = new FixtureAppearance();
        StaffMember player = staffRepository.findById(6L).get();
        app.setShirtNumber(1);
        app.setPlayer(player);
        app.setSubstitute(false);
        List<FixtureAppearance> homeApps = new ArrayList<>();
        homeApps.add(app);
        OppositionAppearance app2 = new OppositionAppearance();
        app2.setFirstName(DAVE);
        app2.setLastName(JONES);
        List<OppositionAppearance> awayApps = new ArrayList<>();
        awayApps.add(app2);
        manager.setTeams(7L, homeApps, awayApps);

        homeApps = manager.getHendonTeam(7L);
        awayApps = manager.getOppositionTeam(7L);

        assertEquals(1, homeApps.get(0).getShirtNumber());
        assertFalse(homeApps.get(0).isSubstitute());
        assertEquals(player, homeApps.get(0).getPlayer());
        assertEquals(DAVE, awayApps.get(0).getFirstName());
        assertEquals(JONES, awayApps.get(0).getLastName());
    }

    @Transactional
    @Test
    public void testSetTeamsDeleteToEmpty() {
        staffRepository.save(createStaffMember());
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);

        FixtureAppearance app = new FixtureAppearance();
        StaffMember player = staffRepository.findById(6L).get();
        app.setShirtNumber(1);
        app.setPlayer(player);
        app.setSubstitute(false);
        List<FixtureAppearance> homeApps = new ArrayList<>();
        homeApps.add(app);
        OppositionAppearance app2 = new OppositionAppearance();
        app2.setFirstName(DAVE);
        app2.setLastName(JONES);
        List<OppositionAppearance> awayApps = new ArrayList<>();
        awayApps.add(app2);
        manager.setTeams(7L, homeApps, awayApps);

        homeApps = new ArrayList<>();
        awayApps = new ArrayList<>();
        manager.setTeams(7L, homeApps, awayApps);

        assertTrue(manager.getHendonTeam(7L).isEmpty());
        assertTrue(manager.getOppositionTeam(7L).isEmpty());
    }

    @Transactional
    @Test
    public void testSetTeamsDeleteOneLeft() {
        staffRepository.save(createStaffMember());
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);

        FixtureAppearance app = new FixtureAppearance();
        StaffMember player = staffRepository.findById(6L).get();
        app.setShirtNumber(1);
        app.setPlayer(player);
        app.setSubstitute(false);
        List<FixtureAppearance> homeApps = new ArrayList<>();
        homeApps.add(app);
        app = new FixtureAppearance();
        app.setShirtNumber(2);
        app.setPlayer(player);
        app.setSubstitute(true);
        homeApps.add(app);
        OppositionAppearance app2 = new OppositionAppearance();
        app2.setFirstName(DAVE);
        app2.setLastName(JONES);
        List<OppositionAppearance> awayApps = new ArrayList<>();
        awayApps.add(app2);
        app2 = new OppositionAppearance();
        app2.setFirstName(ERIC);
        app2.setLastName(CANTONA);
        awayApps.add(app2);
        manager.setTeams(7L, homeApps, awayApps);

        Fixture f = manager.getFixtureById(7L, true, false);

        homeApps.remove(1);
        awayApps.remove(1);
        manager.setTeams(7L, homeApps, awayApps);

        homeApps = manager.getHendonTeam(7L);
        awayApps = manager.getOppositionTeam(7L);

        assertEquals(1, homeApps.get(0).getShirtNumber());
        assertFalse(homeApps.get(0).isSubstitute());
        assertEquals(player, homeApps.get(0).getPlayer());
        assertEquals(DAVE, awayApps.get(0).getFirstName());
        assertEquals(JONES, awayApps.get(0).getLastName());
    }

    @Transactional
    @Test
    public void testSetGoalsNone() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        List<FixtureGoal> homeApps = new ArrayList<>();
        List<OppositionGoal> awayApps = new ArrayList<>();
        manager.setScorers(6L, homeApps, awayApps);

        assertTrue(manager.getHendonScorers(6L).isEmpty());
        assertTrue(manager.getOppositionScorers(6L).isEmpty());
    }

    @Transactional
    @Test
    public void testSetGoalsNull() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);
        manager.setScorers(6L, null, null);

        assertTrue(manager.getHendonScorers(6L).isEmpty());
        assertTrue(manager.getOppositionScorers(6L).isEmpty());
    }

    @Transactional
    @Test
    public void testSetGoalsNeverSet() {
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);

        assertTrue(manager.getHendonScorers(6L).isEmpty());
        assertTrue(manager.getOppositionScorers(6L).isEmpty());
    }

    @Transactional
    @Test
    public void testSetGoalsOneEach() {
        staffRepository.save(createStaffMember());
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);

        StaffMember player = staffRepository.findById(6L).get();

        FixtureGoal goal = new FixtureGoal();
        goal.setScorer(player);
        goal.setMinuteScored(75);
        List<FixtureGoal> homeGoals = new ArrayList<>();
        homeGoals.add(goal);
        OppositionGoal goal2 = new OppositionGoal();
        goal2.setFirstName(DAVE);
        goal2.setLastName(JONES);
        List<OppositionGoal> awayGoals = new ArrayList<>();
        awayGoals.add(goal2);
        manager.setScorers(7L, homeGoals, awayGoals);

        homeGoals = manager.getHendonScorers(7L);
        awayGoals = manager.getOppositionScorers(7L);

        assertEquals(75, homeGoals.get(0).getMinuteScored());
        assertEquals(player, homeGoals.get(0).getScorer());
        assertEquals(DAVE, awayGoals.get(0).getFirstName());
        assertEquals(JONES, awayGoals.get(0).getLastName());
    }

    @Transactional
    @Test
    public void testSetGoalsDeleteToEmpty() {
        staffRepository.save(createStaffMember());
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString(HOME_VENUE);
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);

        StaffMember player = staffRepository.findById(6L).get();

        FixtureGoal goal = new FixtureGoal();
        goal.setScorer(player);
        goal.setMinuteScored(75);
        List<FixtureGoal> homeGoals = new ArrayList<>();
        homeGoals.add(goal);
        OppositionGoal goal2 = new OppositionGoal();
        goal2.setFirstName(DAVE);
        goal2.setLastName(JONES);
        List<OppositionGoal> awayGoals = new ArrayList<>();
        awayGoals.add(goal2);
        manager.setScorers(7L, homeGoals, awayGoals);

        homeGoals = new ArrayList<>();
        awayGoals = new ArrayList<>();
        manager.setScorers(7L, homeGoals, awayGoals);

        assertTrue(manager.getHendonTeam(7L).isEmpty());
        assertTrue(manager.getOppositionTeam(7L).isEmpty());
    }

    @Transactional
    @Test
    public void testSetGoalsDeleteOneLeft() {
        staffRepository.save(createStaffMember());
        LocalDateTime now = LocalDateTime.now();
        FixtureVenue venue = manager.getVenueFromString("Home");
        manager.createFixture(2000, 4L, venue, 1, now, 5L, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
                null, null, null, null, null, null, null);

        StaffMember player = staffRepository.findById(6L).get();

        FixtureGoal goal = new FixtureGoal();
        goal.setScorer(player);
        goal.setMinuteScored(75);
        List<FixtureGoal> homeGoals = new ArrayList<>();
        homeGoals.add(goal);
        goal = new FixtureGoal();
        goal.setScorer(player);
        goal.setMinuteScored(85);
        homeGoals.add(goal);
        OppositionGoal goal2 = new OppositionGoal();
        goal2.setFirstName(DAVE);
        goal2.setLastName(JONES);
        List<OppositionGoal> awayGoals = new ArrayList<>();
        awayGoals.add(goal2);
        goal2 = new OppositionGoal();
        goal2.setFirstName(ERIC);
        goal2.setLastName(CANTONA);
        awayGoals.add(goal2);
        manager.setScorers(7L, homeGoals, awayGoals);

        homeGoals.remove(1);
        awayGoals.remove(1);
        manager.setScorers(7L, homeGoals, awayGoals);

        homeGoals = manager.getHendonScorers(7L);
        awayGoals = manager.getOppositionScorers(7L);

        assertEquals(75, homeGoals.get(0).getMinuteScored());
        assertEquals(player, homeGoals.get(0).getScorer());
        assertEquals(DAVE, awayGoals.get(0).getFirstName());
        assertEquals(JONES, awayGoals.get(0).getLastName());
    }

    @Test
    public void testTweetHighlights() {

        //TODO: Add better cases here (populate fixture with data)
        assertFalse(((FixtureServiceImpl)manager).tweetHighlights(null, null, Fixture.builder().build()));
        assertFalse(((FixtureServiceImpl)manager).tweetHighlights(null, "", Fixture.builder().build()));
        assertTrue(((FixtureServiceImpl)manager).tweetHighlights(null, RANDOM_STRING, Fixture.builder().build()));
        assertFalse(((FixtureServiceImpl)manager).tweetHighlights("", null, Fixture.builder().build()));
        assertFalse(((FixtureServiceImpl)manager).tweetHighlights("", "", Fixture.builder().build()));
        assertTrue(((FixtureServiceImpl)manager).tweetHighlights("", RANDOM_STRING, Fixture.builder().build()));
        assertTrue(((FixtureServiceImpl)manager).tweetHighlights(RANDOM_STRING, null, Fixture.builder().build()));
        assertTrue(((FixtureServiceImpl)manager).tweetHighlights(RANDOM_STRING, "", Fixture.builder().build()));
        assertTrue(((FixtureServiceImpl)manager).tweetHighlights(RANDOM_STRING, RANDOM_STRING, Fixture.builder().build()));
    }

    private StaffMember createStaffMember() {
        return StaffMember.builder().lastName(MCCANN).build();
    }
}
