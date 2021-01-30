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

/**
 * @author Phil
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "StaffRole")
public class StaffRole {

    public static final String PLAYER_STAFF_ROLE = "Player";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "RoleName")
    private String name;

    public static List<String> getStaffRoleValues() {
        ArrayList<String> list = new ArrayList<>();
        list.add(PLAYER_STAFF_ROLE);
        list.add("Manager");
        list.add("Assistant Manager");
        list.add("Coach");
        list.add("Physiotherapist");
        list.add("Fitness Coach");
        list.add("Sports Therapist");
        return list;
    }
}
