/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import lombok.*;

import javax.persistence.Table;
import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EqualsAndHashCode(exclude="fixture")
@Table(name = "OPPOSITIONGOAL")
public class OppositionGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="fixture_id")
    private Fixture fixture;
    private int minuteScored;
    private boolean penalty;
    private boolean ownGoal;
    private String firstName;
    private String lastName;

    public int compareToForGoalList(OppositionGoal other) {
        if (this.getMinuteScored() > other.getMinuteScored()) {
            return -1;
        } else if (this.getMinuteScored() < other.getMinuteScored()) {
            return 1;
        }
        return 0;
    }
}
