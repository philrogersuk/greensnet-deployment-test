/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Disabled
public class GridServiceImplTest {

    @Test
    public void testFindRowByClubNotFound() {
        assertNull(GridServiceImpl.findRowByClub(new ArrayList<>(), 1L));
    }

    @Test
    public void testFindIndexByClubNotFound() {
        assertEquals(-1, GridServiceImpl.findIndexByClub(new ArrayList<>(), 1L));
    }
}
