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
import java.time.LocalDate;

/**
 * Entity class TimeAtClub
 *
 * @author Phil
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "TIMEATCLUB")
public class TimeAtClub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int startSeason;
    private int endSeason;
    private LocalDate startDate;
    private LocalDate endDate;
    @ManyToOne
    @JoinColumn(name = "staffrole")
    private StaffRole staffRole;
    @ManyToOne
    @JoinColumn(name = "staffmember_id")
    private StaffMember staffMember;
    private boolean trialOnly;
    private boolean onLoan;

}
