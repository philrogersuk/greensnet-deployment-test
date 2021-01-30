/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DateHelperTest {

	private DateHelper dateHelper = new DateHelper();

	@Test
	public void testParseStandardDateFormat() {
		LocalDate date = dateHelper.parseStandardDateFormat("2000-12-11");
		assertEquals(11, date.getDayOfMonth());
		assertEquals(12 , date.getMonthValue());
		assertEquals(2000, date.getYear());
	}

	@Test
	public void testParseStandardDateFormatInvalidDate() {
		assertNull(dateHelper.parseStandardDateFormat("2000-13-11"));
		assertNull(dateHelper.parseStandardDateFormat("2000-12-35"));
	}

	@Test
	public void testParseStandardDateFormatNull() {
		assertNull(dateHelper.parseStandardDateFormat(null));
	}

	@Test
	public void testParseStandardDateFormatEmpty() {
		assertNull(dateHelper.parseStandardDateFormat(""));
	}

	@Test
	public void testParseStandardDateFormatInvalidFormat() {
		assertNull(dateHelper.parseStandardDateFormat("11:12:2015"));
	}

	@Test
	public void testParseStandardDateTimeFormat() {
		LocalDateTime date = dateHelper.parseStandardDateTimeFormat("2000-12-11 01:02:03");
		assertEquals(11, date.getDayOfMonth());
		assertEquals(12, date.getMonthValue());
		assertEquals(2000, date.getYear());
		assertEquals(1, date.getHour());
		assertEquals(2, date.getMinute());
		assertEquals(3, date.getSecond());
	}

	@Test
	public void testParseStandardDateTimeFormatInvalidDate() {
		assertNull(dateHelper.parseStandardDateTimeFormat("2000-13-11 11:11:11"));
		assertNull(dateHelper.parseStandardDateTimeFormat("2000-12-35 11:11:11"));
		assertNull(dateHelper.parseStandardDateTimeFormat("2000-12-11 44:11:11"));
		assertNull(dateHelper.parseStandardDateTimeFormat("2000-12-11 11:99:11"));
		assertNull(dateHelper.parseStandardDateTimeFormat("2000-12-11 11:11:99"));
	}

	@Test
	public void testParseStandardDateTimeFormatNull() {
		assertNull(dateHelper.parseStandardDateTimeFormat(null));
	}

	@Test
	public void testParseStandardDateTimeFormatEmpty() {
		assertNull(dateHelper.parseStandardDateTimeFormat(""));
	}

	@Test
	public void testParseStandardDateTimeFormatInvalidFormat() {
		assertNull(dateHelper.parseStandardDateTimeFormat("11:12:2015 11-12-2010"));
	}

	@Test
	public void testGetMonthArray() {
		String[] months = dateHelper.getMonthArray();
		assertEquals("January", months[0]);
		assertEquals("June", months[5]);
		assertEquals("December", months[11]);
		assertEquals(12, months.length);
	}
}