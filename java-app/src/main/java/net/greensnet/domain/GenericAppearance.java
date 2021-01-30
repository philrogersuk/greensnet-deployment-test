/*
 * Copyright (c) 2016, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;


import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode(exclude="fixture")
@MappedSuperclass
public abstract class GenericAppearance implements Comparable<GenericAppearance> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="fixture_id")
    private Fixture fixture;
    private int shirtNumber;
    private boolean substitute;
    private int yellowCardMinute;
    private int redCardMinute;
    private int substitutionMinute;
    private int playerReplaced;

    public int compareTo(@NotNull GenericAppearance other) {
        if (this.isSubstitute() && !other.isSubstitute()) {
            return 1;
        }
        if (!this.isSubstitute() && other.isSubstitute()) {
            return -1;
        }
        return Integer.compare(this.getShirtNumber(), other.getShirtNumber());
    }

    public abstract String getDisplayName();
}
