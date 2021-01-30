/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PrimitiveUtilsTest {

    @Test
    public void testIsUtilityClass() {
        try {
            TestUtilities.testIsUtilityClassAndWellDefined(PrimitiveUtils.class);
        } catch (Exception e) {
            fail("Unexpected exception" + e.getMessage());
        }
    }

    @Test
    public void testParsePositiveIntSafe() {
        assertEquals(1, PrimitiveUtils.parsePositiveIntSafe("1"));
        assertEquals(99, PrimitiveUtils.parsePositiveIntSafe("99"));
        assertEquals(0, PrimitiveUtils.parsePositiveIntSafe("0"));
    }

    @Test
    public void testParsePositiveIntSafeNegative() {
        assertEquals(-1, PrimitiveUtils.parsePositiveIntSafe("-1"));
        assertEquals(-1, PrimitiveUtils.parsePositiveIntSafe("-99"));
        assertEquals(-1, PrimitiveUtils.parsePositiveIntSafe("-2310"));
    }

    @Test
    public void testParsePositiveIntSafeNull() {
        assertEquals(-1, PrimitiveUtils.parsePositiveIntSafe(null));
    }

    @Test
    public void testParsePositiveIntSafeEmpty() {
        assertEquals(-1, PrimitiveUtils.parsePositiveIntSafe(""));
    }

    @Test
    public void testParsePositiveIntSafeNotInt() {
        assertEquals(-1, PrimitiveUtils.parsePositiveIntSafe("asdrfw"));
    }

    @Test
    public void testParsePositiveLongSafe() {
        assertEquals(1, PrimitiveUtils.parsePositiveLongSafe("1"));
        assertEquals(99, PrimitiveUtils.parsePositiveLongSafe("99"));
        assertEquals(0, PrimitiveUtils.parsePositiveLongSafe("0"));
    }

    @Test
    public void testParsePositiveLongSafeNegative() {
        assertEquals(-1, PrimitiveUtils.parsePositiveLongSafe("-1"));
        assertEquals(-1, PrimitiveUtils.parsePositiveLongSafe("-99"));
        assertEquals(-1, PrimitiveUtils.parsePositiveLongSafe("-2310"));
    }

    @Test
    public void testParsePositiveLongSafeNull() {
        assertEquals(-1, PrimitiveUtils.parsePositiveLongSafe(null));
    }

    @Test
    public void testParsePositiveLongSafeEmpty() {
        assertEquals(-1, PrimitiveUtils.parsePositiveLongSafe(""));
    }

    @Test
    public void testParsePositiveLongSafeNotInt() {
        assertEquals(-1, PrimitiveUtils.parsePositiveLongSafe("asdrfw"));
    }

    @Test
    public void testParsePositiveFloatSafe() {
        assertEquals(1, PrimitiveUtils.parsePositiveFloatSafe("1"), 0.001);
        assertEquals(99, PrimitiveUtils.parsePositiveFloatSafe("99"), 0.001);
        assertEquals(0, PrimitiveUtils.parsePositiveFloatSafe("0"), 0.001);
        assertEquals(0.2374, PrimitiveUtils.parsePositiveFloatSafe("0.2374"), 0.001);
        assertEquals(199.382, PrimitiveUtils.parsePositiveFloatSafe("199.382"), 0.001);
        assertEquals(4812.3, PrimitiveUtils.parsePositiveFloatSafe("4812.3"), 0.001);
    }

    @Test
    public void testParsePositiveFloatSafeNegative() {
        assertEquals(-1, PrimitiveUtils.parsePositiveFloatSafe("-1"), 0.001);
        assertEquals(-1, PrimitiveUtils.parsePositiveFloatSafe("-99"), 0.001);
        assertEquals(-1, PrimitiveUtils.parsePositiveFloatSafe("-2310"), 0.001);
        assertEquals(-1, PrimitiveUtils.parsePositiveFloatSafe("-2310.384"), 0.001);
        assertEquals(-1, PrimitiveUtils.parsePositiveFloatSafe("-0.01"), 0.001);
        assertEquals(-1, PrimitiveUtils.parsePositiveFloatSafe("-3918.321"), 0.001);
    }

    @Test
    public void testParsePositiveFloatSafeNull() {
        assertEquals(-1f, PrimitiveUtils.parsePositiveFloatSafe(null), 0.001);
    }

    @Test
    public void testParsePositiveFloatSafeEmpty() {
        assertEquals(-1, PrimitiveUtils.parsePositiveFloatSafe(""), 0.001);
    }

    @Test
    public void testParsePositiveFloatSafeNotInt() {
        assertEquals(-1, PrimitiveUtils.parsePositiveFloatSafe("asdrfw"), 0.001);
    }

    @Test
    public void testParseBooleanSafeTrue() {
        assertTrue(PrimitiveUtils.parseBooleanSafe("true"));
        assertTrue(PrimitiveUtils.parseBooleanSafe("TRUE"));
        assertTrue(PrimitiveUtils.parseBooleanSafe("True"));
    }

    @Test
    public void testParseBooleanSafeFalse() {
        assertFalse(PrimitiveUtils.parseBooleanSafe("false"));
        assertFalse(PrimitiveUtils.parseBooleanSafe("FALSE"));
        assertFalse(PrimitiveUtils.parseBooleanSafe("False"));
    }

    @Test
    public void testParseBooleanSafeNull() {
        assertFalse(PrimitiveUtils.parseBooleanSafe(null));
    }

    @Test
    public void testParseBooleanSafeEmpty() {
        assertFalse(PrimitiveUtils.parseBooleanSafe(""));
    }

    @Test
    public void testParseBooleanSafeNotInt() {
        assertFalse(PrimitiveUtils.parseBooleanSafe("asdrfw"));
    }
}