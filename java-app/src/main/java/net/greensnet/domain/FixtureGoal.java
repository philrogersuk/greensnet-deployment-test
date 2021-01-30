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
@EqualsAndHashCode(exclude={"fixture", "scorer"})
@Table(name = "FIXTUREGOAL")
public class FixtureGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="fixture_id")
    private Fixture fixture;
    private int minuteScored;
    private boolean penalty;
    private boolean ownGoal;
    @ManyToOne
    @JoinColumn(name="scorer_id")
    private StaffMember scorer;

    public int compareToForGoalList(FixtureGoal other) {
        if (this.getMinuteScored() > other.getMinuteScored()) {
            return -1;
        } else if (this.getMinuteScored() < other.getMinuteScored()) {
            return 1;
        }
        return 0;
    }
}
