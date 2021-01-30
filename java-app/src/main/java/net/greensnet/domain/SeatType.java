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
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "SeatType")
public class SeatType {

    public static final String GREEN = "Green";
    public static final String WHITE = "White";
    public static final String NONE = "None";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SeatType")
    private String type;

    public static List<String> getSeatTypeValues() {
        ArrayList<String> list = new ArrayList<>();
        list.add(GREEN);
        list.add(WHITE);
        list.add(NONE);
        return list;
    }
}
