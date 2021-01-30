/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SnowballDrawTest {
    @Test
    public void testIsSnowballDraw() {
        SnowballDraw draw = new SnowballDraw();
        draw.setName("Snowball 2018");
        assertThat(draw.isSnowballDraw(), is(true));
    }

    @Test
    public void testIsNotSnowballDraw() {
        SnowballDraw draw = new SnowballDraw();
        draw.setName("Dec 2018");
        assertThat(draw.isSnowballDraw(), is(false));
    }
}
