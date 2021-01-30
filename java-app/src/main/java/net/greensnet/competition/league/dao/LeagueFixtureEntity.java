/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.competition.league.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.greensnet.club.dao.ClubEntity;
import net.greensnet.competition.dao.CompetitionEntity;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "LEAGUEFIXTURE")
public class LeagueFixtureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int season;
    @ManyToOne
    @JoinColumn(name = "competition_id")
    private CompetitionEntity competition;
    @ManyToOne
    @JoinColumn(name = "homeClub_id")
    private ClubEntity homeClub;
    @ManyToOne
    @JoinColumn(name = "awayClub_id")
    private ClubEntity awayClub;
    private int homeGoals;
    private int awayGoals;
    private int attendance;
    private LocalDate fixtureDate;

}
