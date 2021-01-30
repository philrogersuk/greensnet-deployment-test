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

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "LEAGUETABLENOTE")
public class LeagueTableNoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int season;
    @Column(name = "competition_id")
    private long competitionId;
    @ManyToOne
    @JoinColumn(name = "club_id")
    private ClubEntity club;
    private int pointsDeducted;
    private String deductionReason;
}
