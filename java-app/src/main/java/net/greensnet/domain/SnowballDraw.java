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
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "SNOWBALLDRAW")
    public class SnowballDraw {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;
    private String name;
    private int numEntries;
    private float fund;
    private int firstPrizeWinner;
    private float firstPrize;
    private int secondPrizeWinner;
    private float secondPrize;
    private float trustContribution;
    private float snowballContribution;

    public boolean isSnowballDraw() {
        return name.contains("Snowball");
    }
}
