/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.dao.SeatRepository;
import net.greensnet.dao.SeatTypeRepository;
import net.greensnet.dao.SponsorRepository;
import net.greensnet.domain.Seat;
import net.greensnet.domain.SeatType;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional(transactionManager = "transactionManager")
@Commit
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SponsorServiceImplTest {


	@Autowired
	private SeatRepository seatRepository;
	@Autowired
	private SeatTypeRepository seatTypeRepository;
	@Mock
	private FixtureService fixService;
	@Mock
	private StaffService staffService;
	@Mock
	private SponsorRepository sponsorRepository;
	@Mock
	private FileService fileService;
	@Autowired
	private DateHelper dateHelper;

	private SponsorService service;

	@BeforeEach
	public void setUp() {
		service = new SponsorServiceImpl(staffService, fixService, seatRepository, seatTypeRepository, sponsorRepository, fileService, dateHelper);

		seatTypeRepository.save(SeatType.builder().type("GREEN").build());
		seatTypeRepository.save(SeatType.builder().type("WHITE").build());
		seatTypeRepository.save(SeatType.builder().type("NONE").build());
	}

	@Transactional
	@Test
	public void testWriteAndRetrieveSeat() {
		SeatType type = service.getSeatTypeById(1L);
		assertTrue(service.createSeat("Dave Moraine", type, 0, 1));
		Seat item = service.getSeatById(4L);
		assertEquals("Wrong sponsor", "Dave Moraine", item.getSponsor());
		assertEquals(0, item.getRow());
		assertEquals(1, item.getColumn());
	}

	@Transactional
	@Test
	public void testEditSeat() {
		SeatType type = service.getSeatTypeById(1L);
		assertTrue(service.createSeat("Dave Moraine", type, 0, 1));
		assertTrue(service.editSeat(4L, "Eric Bishop", type));
		Seat item = service.getSeatById(4L);
		assertEquals("Wrong sponsor", "Eric Bishop", item.getSponsor());
		assertEquals(0, item.getRow());
		assertEquals(1, item.getColumn());
	}

	@Transactional
	@Test
	public void testRemoveSeat() {
		SeatType type = service.getSeatTypeById(1L);
		assertTrue(service.createSeat("Dave Moraine", type, 0, 1));
		assertTrue(service.deleteSeatById(4L));
		assertNull(service.getSeatById(4L));
	}

	@Transactional
	@Test
	public void testGetAllSeatTypes() {
		List<SeatType> types = service.getAllSeatTypes();
		assertEquals(3, types.size());
		assertEquals("Wrong type", "GREEN", types.get(0).getType());
		assertEquals("Wrong type", "WHITE", types.get(1).getType());
		assertEquals("Wrong type", "NONE", types.get(2).getType());
	}


	@Transactional
	@Test
	public void testEditSeatDoesntExist() {
		assertFalse(service.editSeat(4L, "Eric Bishop", null));
	}

	@Transactional
	@Test
	public void testGetSeatsInGrid() {
		SeatType green = service.getSeatTypeById(0L);
		SeatType white = service.getSeatTypeById(1L);
		SeatType none = service.getSeatTypeById(2L);
		assertTrue(service.createSeat("Dave Moraine", green, 0, 0));
		assertTrue(service.createSeat("Ted Maul", white, 0, 1));
		assertTrue(service.createSeat("Janine Walsh", none, 1, 0));
		assertTrue(service.createSeat("Eric Ericson", green, 1, 1));
		Seat[][] seats = service.getSeatsInGrid();
		assertEquals(2, seats.length);
		assertEquals( 2, seats[0].length);
		assertEquals("Wrong name", "Eric Ericson", seats[1][1].getSponsor());
		assertEquals("Wrong type", "WHITE", seats[1][0].getType().getType());
	}

	@Transactional
	@Test
	public void testGetSeatsInGridEmptyGrid() {
		Seat[][] seats = service.getSeatsInGrid();
		assertEquals( new Seat[0][0], seats);
	}

/*	@Transactional
	@Test
	public void testGetMascots() {
		setupFixtures();
		List<Fixture> fixtures = service.getMascots();
		assertEquals(2, fixtures.size());
	}

	@Transactional
	@Test
	public void testGetMatchSponsors() {
		setupFixtures();
		List<Fixture> fixtures = service.getMatchSponsors();
		assertEquals(2, fixtures.size());
	}

	@Transactional
	@Test
	public void testGetMatchballSponsors() {
		setupFixtures();
		List<Fixture> fixtures = service.getMatchballSponsors();
		assertEquals(2, fixtures.size());
	}

	@Transactional
	@Test
	public void testGetProgrameSponsors() {
		setupFixtures();
		List<Fixture> fixtures = service.getProgrammeSponsors();
		assertEquals(2, fixtures.size());
	}

	@Transactional
	@Test
	public void testGetPlayerSponsors() {
		helper.createStaffRoles();
		helper.createGaryMcCannWithTimePeriods();
		helper.createFreddieHyattWithTimePeriods();
		helper.createJasonMcKoyWithTimePeriods();
		List<StaffMember> list = service.getPlayerSponsors();

		assertEquals("Expected one item", 1, list.size());


	}

	private void setupFixtures() {
		helper.createCompetitionTypes();
		helper.createFixtureVenues();
		helper.createOneSampleCompetition();
		helper.createOneSampleClub();
		Date now = new Date();
		FixtureVenue venue = fixService.getVenueFromString("Home");
		FixtureVenue venue2 = fixService.getVenueFromString("Away");
		FixtureVenue venue3 = fixService.getVenueFromString("Neutral");
		fixService.createFixture(DateUtils.getCurrentSeason(), 1, venue, 1, now, 1, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
				null, null, null, null, null, null, null);
		fixService.createFixture(DateUtils.getCurrentSeason(), 1, venue, 1, now, 1, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
				null, null, null, null, null, null, null);
		fixService.createFixture(DateUtils.getCurrentSeason(), 1, venue2, 1, now, 1, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
				null, null, null, null, null, null, null);
		fixService.createFixture(DateUtils.getCurrentSeason(), 1, venue3, 1, now, 1, -1, -1, -1, -1, -1, -1, -1, null, false, -1, null, null, null,
				null, null, null, null, null, null, null);
	}
	*/
}
