/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import java.util.ArrayList;
import java.util.List;

public class FixtureListMonth {
    private String month;
    private String year;
    private List<Fixture> fixtures = new ArrayList<>();

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public List<Fixture> getFixtures() {
        return new ArrayList<>(fixtures);
    }

    public void setFixtures(List<Fixture> fixtures) {
        this.fixtures = new ArrayList<>(fixtures);
    }
}
