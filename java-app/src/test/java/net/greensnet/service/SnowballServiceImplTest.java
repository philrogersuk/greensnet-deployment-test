/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.dao.SnowballDrawRepository;
import net.greensnet.domain.SnowballDraw;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional(transactionManager = "transactionManager")
@Commit
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class SnowballServiceImplTest {


    public static final String DRAW_NAME_JUNE_2000 = "Jun 2000";
    public static final int NUM_ENTRIES_200 = 200;
    public static final int NUM_ENTRIES_201 = 201;
    public static final float PRIZE_FUND_17_50 = 17.5F;
    public static final String DRAW_NAME_JULY_2000 = "Jul 2000";
    public static final String DRAW_NAME_AUGUST_2000 = "Aug 2000";
    public static final String DRAW_NAME_SEPTEMBER_2000 = "Sep 2000";
    public static final int FIRST_PRIZE_WINNER = 22;
    public static final float FIRST_PRIZE = 12F;
    public static final int SECOND_PRIZE_WINNER = 14;
    public static final float SECOND_PRIZE = 5.2F;
    public static final float TRUST_CONTRIBUTION = 11.89F;
    public static final float SNOWBALL_CONTRIBUTION = 123.45F;
    @Autowired
    private SnowballDrawRepository repository;
    private SnowballService service;

    @BeforeEach
    public void setUp() {
        service = new SnowballServiceImpl(repository);
    }

    @Transactional
    @Test
    public void testWriteAndRetrieveItem() {
        service.createDraw(LocalDateTime.now(), DRAW_NAME_JUNE_2000, NUM_ENTRIES_200, PRIZE_FUND_17_50,
                FIRST_PRIZE_WINNER, FIRST_PRIZE, SECOND_PRIZE_WINNER, SECOND_PRIZE,
                TRUST_CONTRIBUTION, SNOWBALL_CONTRIBUTION);
        SnowballDraw draw = service.getDraw(1L);
        assertEquals(DRAW_NAME_JUNE_2000, draw.getName());
        assertEquals(NUM_ENTRIES_200, draw.getNumEntries());
    }

    @Transactional
    @Test
    public void testClubNotExists() {
        assertNull(service.getDraw(1L));
    }

    @Transactional
    @Test
    public void testGetAll() {
        service.createDraw(LocalDateTime.now(), DRAW_NAME_JUNE_2000, NUM_ENTRIES_200, PRIZE_FUND_17_50,
                FIRST_PRIZE_WINNER, FIRST_PRIZE, SECOND_PRIZE_WINNER, SECOND_PRIZE,
                TRUST_CONTRIBUTION, SNOWBALL_CONTRIBUTION);
        service.createDraw(LocalDateTime.now(), DRAW_NAME_JULY_2000, NUM_ENTRIES_200, PRIZE_FUND_17_50,
                FIRST_PRIZE_WINNER, FIRST_PRIZE, SECOND_PRIZE_WINNER, SECOND_PRIZE,
                TRUST_CONTRIBUTION, SNOWBALL_CONTRIBUTION);
        service.createDraw(LocalDateTime.now(), DRAW_NAME_AUGUST_2000, NUM_ENTRIES_200, PRIZE_FUND_17_50,
                FIRST_PRIZE_WINNER, FIRST_PRIZE, SECOND_PRIZE_WINNER, SECOND_PRIZE,
                TRUST_CONTRIBUTION, SNOWBALL_CONTRIBUTION);
        service.createDraw(LocalDateTime.now(), DRAW_NAME_SEPTEMBER_2000, NUM_ENTRIES_200, PRIZE_FUND_17_50,
                FIRST_PRIZE_WINNER, FIRST_PRIZE, SECOND_PRIZE_WINNER, SECOND_PRIZE,
                TRUST_CONTRIBUTION, SNOWBALL_CONTRIBUTION);
        List<SnowballDraw> items = service.getDraws();
        assertEquals(4, items.size());
    }

    @Transactional
    @Test
    public void testGetAllEmpty() {
        List<SnowballDraw> items = service.getDraws();
        assertEquals(0, items.size());
    }

    @Transactional
    @Test
    public void testRemoveDraw() {
        service.createDraw(LocalDateTime.now(), DRAW_NAME_JUNE_2000, NUM_ENTRIES_200, PRIZE_FUND_17_50,
                FIRST_PRIZE_WINNER, FIRST_PRIZE, SECOND_PRIZE_WINNER, SECOND_PRIZE,
                TRUST_CONTRIBUTION, SNOWBALL_CONTRIBUTION);
        service.createDraw(LocalDateTime.now(), DRAW_NAME_JULY_2000, NUM_ENTRIES_200, PRIZE_FUND_17_50,
                FIRST_PRIZE_WINNER, FIRST_PRIZE, SECOND_PRIZE_WINNER, SECOND_PRIZE,
                TRUST_CONTRIBUTION, SNOWBALL_CONTRIBUTION);
        service.createDraw(LocalDateTime.now(), DRAW_NAME_AUGUST_2000, NUM_ENTRIES_200, PRIZE_FUND_17_50,
                FIRST_PRIZE_WINNER, FIRST_PRIZE, SECOND_PRIZE_WINNER, SECOND_PRIZE,
                TRUST_CONTRIBUTION, SNOWBALL_CONTRIBUTION);
        service.createDraw(LocalDateTime.now(), DRAW_NAME_SEPTEMBER_2000, NUM_ENTRIES_200, PRIZE_FUND_17_50,
                FIRST_PRIZE_WINNER, FIRST_PRIZE, SECOND_PRIZE_WINNER, SECOND_PRIZE,
                TRUST_CONTRIBUTION, SNOWBALL_CONTRIBUTION);
        assertTrue(service.deleteDraw(2L));
        assertTrue(service.deleteDraw(3L));
        List<SnowballDraw> items = service.getDraws();
        assertEquals(2, items.size());
    }

    @Transactional
    @Test
    public void testUpdateAndRetrieveDraw() {
        service.createDraw(LocalDateTime.now(), DRAW_NAME_JUNE_2000, NUM_ENTRIES_200, PRIZE_FUND_17_50,
                FIRST_PRIZE_WINNER, FIRST_PRIZE, SECOND_PRIZE_WINNER, SECOND_PRIZE,
                TRUST_CONTRIBUTION, SNOWBALL_CONTRIBUTION);
        LocalDateTime future = LocalDateTime.now().plusSeconds(1000);
        assertTrue(service.editDraw(1L, future, DRAW_NAME_JULY_2000, NUM_ENTRIES_201, PRIZE_FUND_17_50,
                FIRST_PRIZE_WINNER, FIRST_PRIZE, SECOND_PRIZE_WINNER, SECOND_PRIZE,
                TRUST_CONTRIBUTION, SNOWBALL_CONTRIBUTION));

        SnowballDraw draw = service.getDraw(1L);
        assertEquals(DRAW_NAME_JULY_2000, draw.getName());
        assertEquals(201, draw.getNumEntries());
        assertEquals(future, draw.getDate());
    }

    @Transactional
    @Test
    public void testUpdateDrawDoesntExist() {
        assertFalse(service.editDraw(1L, null, DRAW_NAME_JULY_2000, NUM_ENTRIES_200, PRIZE_FUND_17_50,
                FIRST_PRIZE_WINNER, FIRST_PRIZE, SECOND_PRIZE_WINNER, SECOND_PRIZE,
                TRUST_CONTRIBUTION, SNOWBALL_CONTRIBUTION));
    }
}
