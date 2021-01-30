/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GridTest {

    private Grid grid;

    @BeforeEach
    public void setUp() {
        grid = Grid.builder().build();
    }

    @Test
    public void testGetAndSetGridRow() {
        ArrayList<GridRow> rows = new ArrayList<>();
        rows.add(new GridRow());
        grid.setRows(rows);
        assertEquals(1, grid.getRows().size());
    }
}
