/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "FIXTUREVENUE")
public class FixtureVenue {

    public static final int HOME_VENUE = 0;
    public static final int AWAY_VENUE = 1;
    public static final int NEUTRAL_VENUE = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String venue;

    public static List<FixtureVenue> getValues() {
        List<FixtureVenue> list = new ArrayList<>();
        FixtureVenue venue = new FixtureVenue();
        venue.setVenue("Home");
        venue.setId(0L);
        list.add(venue);
        venue = new FixtureVenue();
        venue.setVenue("Away");
        venue.setId(1L);
        list.add(venue);
        venue = new FixtureVenue();
        venue.setVenue("Neutral");
        venue.setId(2L);
        list.add(venue);
        return list;
    }

    public char getFirstChar() {
        if (null == venue) {
            return '\0';
        }
        return venue.charAt(0);
    }
}
