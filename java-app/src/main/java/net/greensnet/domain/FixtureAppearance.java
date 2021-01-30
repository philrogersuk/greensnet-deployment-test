/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper=true, exclude = "player")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "FIXTUREAPPEARANCE")
public class FixtureAppearance extends GenericAppearance {

    @ManyToOne
    @JoinColumn(name="player_id")
    private StaffMember player;

    public String getDisplayName() {
        return player.getFirstName() + " " + player.getLastName();
    }
}
