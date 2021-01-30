/*
 * Copyright (c) 2016, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Table;
import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Seat")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sponsor;
    private int row;
    @Column(name="col")
    private int column;
    @ManyToOne
    @JoinColumn(name = "SeatType")
    private SeatType type;

}
