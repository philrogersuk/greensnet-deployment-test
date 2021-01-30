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
import java.time.LocalDateTime;

/**
 * @author Phil
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TEAMFIXTURE")
public class TeamFixture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "season")
    private int season;
    private LocalDateTime kickOff;
    @Column(name = "hendonScore")
    private int hendonScore;
    @Column(name = "oppositionScore")
    private int oppositionScore;
    @Column(name = "hendonScore90")
    private int hendonScore90;
    @Column(name = "oppositionScore90")
    private int oppositionScore90;
    @Column(name = "hendonPenalties")
    private int hendonPenalties;
    @Column(name = "oppositionPenalties")
    private int oppositionPenalties;

    @ManyToOne
    @JoinColumn(name = "team")
    private TeamType team;
    private String competition;
    private char venue;
    private String opposition;
    private String hendonGoals;

    public String getVenueAsString() {
        return String.valueOf(venue);
    }

    public boolean isAway() {
        return venue == 'A';
    }
}
