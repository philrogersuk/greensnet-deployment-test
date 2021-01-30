/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author Phil
 */
@Component
public class DateHelper {

    public LocalDateTime getLocalDateTimeOfNow() {
        return LocalDateTime.now(ZoneId.of("Europe/London"));
    }

    private DateTimeFormatter getStandardDateTimeFormat() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    private DateTimeFormatter getStandardDateFormat() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

    public  LocalDateTime parseStandardDateTimeFormat(String input) {
        if (null == input || "".equals(input)) {
            return null;
        }
        try {
            return LocalDateTime.parse(input, getStandardDateTimeFormat());
        } catch (DateTimeParseException e) {
            return null;
        }

    }

    public LocalDate parseStandardDateFormat(String input) {
        if (input != null) {
            try {
                return LocalDate.parse(input, getStandardDateFormat());
            } catch (DateTimeParseException  e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public int[] getSeasonArray() {
        int currentSeason = getCurrentSeason() + 1;
        int[] seasons = new int[currentSeason - 1907];
        for (int i = 0; i < seasons.length; i++) {
            seasons[i] = currentSeason - i;
        }
        return seasons;
    }

    public String[] getMonthArray() {
        return new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    }

    public int getCurrentSeason() {
        Calendar calendar = GregorianCalendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        if (calendar.get(Calendar.MONTH) < Calendar.JUNE) {
            year--;
        }
        return year;
    }

    public int getCurrentYear() {
        Calendar calendar = GregorianCalendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    public int getCurrentMonth() {
        Calendar calendar = GregorianCalendar.getInstance();
        return calendar.get(Calendar.MONTH);
    }

    public int[][] to2DSeasonArray(List<Integer> seasons) {
        int firstSeason = seasons.get(0);
        int lastSeason = seasons.get(seasons.size() - 1);
        int firstDecade = (firstSeason / 10) * 10;
        int lastDecade = (lastSeason / 10) * 10;

        int[][] array = new int[(lastDecade - firstDecade) / 10 + 1][11];

        for (int i = 0; (firstDecade + (i * 10)) <= lastDecade; i = i + 1) {
            array[i][0] = firstDecade + (i * 10);
            for (int j = 0; j < 10; j++) {
                if (seasons.contains(firstDecade + (i * 10) + j)) {
                    array[i][j + 1] = (firstDecade + (i * 10)) % 100 + j;
                } else {
                    array[i][j + 1] = -1;
                }
            }
        }

        return array;
    }
}
