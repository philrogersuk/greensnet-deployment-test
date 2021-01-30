/*
 * Copyright (c) 2014, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "STAFFUPDATE")
public class StaffUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "staffmember_id")
    private StaffMember staffMember;
    private LocalDate datePublished;
    private String occupation;
    private String homeLocation;
    private String stillInFootball;
    private String whereJoinedFrom;
    private String otherPreviousClubs;
    private String wherePlayedAfter;
    private String favouriteMemories;
    private String bestMatch;
    private String bestGoal;
    private String bestPlayer;
    private String bestManager;
    private String favouriteGround;
    private String leastFavGround;
}
