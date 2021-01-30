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
@Table(name = "TEAMNEWS")
public class TeamNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String headline;
    @Column(length = 7000)
    private String item;
    private int season;
    @ManyToOne
    @JoinColumn(name = "team")
    private TeamType team;
    private LocalDateTime timeOfRelease;

}
