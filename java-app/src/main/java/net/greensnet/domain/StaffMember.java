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
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Entity class StaffMember
 *
 * @author Phil
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "STAFFMEMBER")
public class StaffMember implements Comparable<StaffMember> {

    private static final String LINE_BREAK = "<br />";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    @Column(length = 10000)
    private String profile;
    private LocalDate dateOfBirth;
    private boolean isAtClub;
    private boolean hasImage;
    private String publicUrl;
    @OneToMany(cascade= CascadeType.PERSIST, mappedBy = "staffMember")
    private List<TimeAtClub> timeAtClub;
    private String fullKit;
    private String awayKit;
    private String socks;
    private String fullKitTwo;
    private String awayKitTwo;
    private String socksTwo;
    private String corporate;
    private String corporateSponsorImage;
    private String shirt;
    private String shorts;
    private String tracksuit;
    private String boots;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "staffid", nullable=false)
    private Set<OldStaffName> oldNames;
    private boolean deleted;

    public boolean isOnLoan(int season) {
        for (TimeAtClub time : timeAtClub) {
            if (season >= time.getStartSeason() && (season <= time.getEndSeason() || time.getEndSeason() == -1)) {
                return time.isOnLoan();
            }
        }
        return false;
    }

    public void addTimePeriod(TimeAtClub period) {
        timeAtClub.add(period);
    }

    public void setTimePeriod(int index, TimeAtClub period) {
        timeAtClub.set(index, period);
    }

    public TimeAtClub removeTimePeriod(int index) {
        return timeAtClub.remove(index);
    }

    public boolean getIsPlayer() {
        for (TimeAtClub t : timeAtClub) {
            if (StaffRole.PLAYER_STAFF_ROLE.equals(t.getStaffRole().getName())) {
                return true;
            }
        }
        return false;
    }

    public String getDatesAtClub() {
        int min = 9999;
        int max = -1;
        for (TimeAtClub t : timeAtClub) {
            min = Math.min(min, t.getStartSeason());
            max = Math.max(max, t.getEndSeason());
            if (t.getEndSeason() == -1) {
                max = 9999;
            }
        }
        DecimalFormat fmt = new DecimalFormat("00");
        StringBuilder s = new StringBuilder(min + "/" + fmt.format((min + 1) % 100));
        if (max == 9999 || max == -1) {
            s.append(" to date");
        } else {
            s.append(" - ");
            s.append(max).append("/").append(fmt.format((max + 1) % 100));
        }
        return s.toString();
    }

    public String getRolesAtClub() {
        StringBuilder s = new StringBuilder();
        for (TimeAtClub t : timeAtClub) {
            if (-1 == s.indexOf(t.getStaffRole().getName())) {
                s.append(t.getStaffRole().getName()).append(LINE_BREAK);
            }
        }
        s.delete(s.length() - 6, s.length());
        return s.toString();
    }

    public String getCurrentRolesAtClub() {
        StringBuilder s = new StringBuilder();
        for (TimeAtClub t : timeAtClub) {
            if (null != t.getStaffRole() && null != t.getStaffRole().getName() && t.getEndSeason() == -1 && !t.isTrialOnly() && -1 == s.indexOf(t.getStaffRole().getName())) {
                s.append(t.getStaffRole().getName()).append(LINE_BREAK);
            }
        }
        s.delete(s.length() - Math.min(6, s.length()), s.length());
        return s.toString();
    }

    public String getAllRolesAtClub() {
        StringBuilder s = new StringBuilder();
        for (TimeAtClub t : timeAtClub) {
            if (Objects.nonNull(t)
                    && Objects.nonNull(t.getStaffRole())
                    && Objects.nonNull(t.getStaffRole().getName())
                    && -1 == s.indexOf(t.getStaffRole().getName())) {
                s.append(t.getStaffRole().getName()).append(LINE_BREAK);
            }
        }
        s.delete(s.length() - Math.min(6, s.length()), s.length());
        return s.toString();
    }

    public String getFirstNameForSeason(int season) {
        if (oldNames == null || oldNames.isEmpty()) {
            return firstName;
        }
        for (OldStaffName oldName : oldNames) {
            if (oldName.getStartSeason() <= season && oldName.getEndSeason() >= season) {
                return oldName.getFirstName();
            }
        }
        return firstName;
    }

    public String getLastNameForSeason(int season) {
        if (oldNames == null || oldNames.isEmpty()) {
            return lastName;
        }
        for (OldStaffName oldName : oldNames) {
            if (oldName.getStartSeason() <= season && oldName.getEndSeason() >= season) {
                return oldName.getLastName();
            }
        }
        return lastName;
    }

    public String getPreviousNames() {
        if (oldNames == null || oldNames.isEmpty()) {
            return null;
        }
        StringBuilder previousNames = new StringBuilder();
        for (OldStaffName oldName : oldNames) {
            previousNames.append(oldName.getFirstName()).append(" ").append(oldName.getLastName()).append(", ");
        }
        return previousNames.substring(0, previousNames.length() - 2);
    }

    private String getSortName() {
        return getLastName() + "|" + getFirstName();
    }

    @Override
    public int compareTo(StaffMember other) {
        return getSortName().compareTo(other.getSortName());
    }
}
