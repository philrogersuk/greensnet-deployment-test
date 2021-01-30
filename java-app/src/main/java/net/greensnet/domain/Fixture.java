/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.greensnet.competition.dao.CompetitionEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static net.greensnet.club.service.ClubServiceImpl.HENDON_ID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="FIXTURE")
public class Fixture {

    private static final String OWN_GOAL = "Own Goal";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "season")
    private int season;
    private LocalDateTime kickOff;
    @Column(name = "hendonScore")
    private int hendonScore;
    @Column(name = "oppositionScore")
    private int oppositionScore;
    @Column(name = "hendonScore90")
    private int hendonScore90;
    @Column(name = "oppositionScore90")
    private int oppositionScore90;
    @Column(name = "hendonPenalties")
    private int hendonPenalties;
    @Column(name = "oppositionPenalties")
    private int oppositionPenalties;
    @ManyToOne
    @JoinColumn(name = "competition_id")
    private CompetitionEntity competition;
    @ManyToOne
    @JoinColumn(name = "venue")
    private FixtureVenue venue;
    @Column(name = "alt_venue_id")
    private long alternativeVenue;
    @Column(name = "alt_venue_name")
    private String alternativeVenueName;
    @Column(name = "opposition_id")
    private long opposition;
    @Column(name = "opposition_name")
    private String oppositionName;
    @Column(name = "abandonedMinute")
    private int abandonedMinute;
    @Column(name = "abandonedReason")
    private String abandonedReason;
    @Column(name = "notPlayed")
    private boolean notPlayed;
    private int attendance;
    @Column(length = 10000)
    private String report;
    private String author;
    @Column(name = "programmeSponsor")
    private String programmeSponsor;
    @Column(name = "matchballSponsor")
    private String matchballSponsor;
    @Column(name = "matchSponsor")
    private String matchSponsor;
    @Column(name = "hfctvSponsor")
    private String hfctvSponsor;
    private String mascot;
    @Column(name = "highlightsId")
    private String highlightsId;
    @Column(name = "youtubeId")
    private String youtubeId;
    @Column(name = "ticketURL")
    private String ticketURL;
    @OneToMany(mappedBy = "fixture")
    private Set<FixtureGoal> hendonGoals = new HashSet<>();
    @OneToMany(mappedBy = "fixture")
    private Set<OppositionGoal> oppositionGoals = new HashSet<>();
    @OneToMany(mappedBy = "fixture")
    private Set<FixtureAppearance> hendonTeam = new HashSet<>();
    @OneToMany(mappedBy = "fixture")
    private Set<OppositionAppearance> oppositionTeam = new HashSet<>();
    @Transient
    private String homeCrest;
    @Transient
    private String awayCrest;


    public char getVenueAsInitial() {
        return venue.getVenue().charAt(0);
    }

    public String getVenueName() {
        if (alternativeVenueName != null) {
            return alternativeVenueName;
        }
        return getHomeTeamName();
    }

    public String getReportAsHtml() {
        String storyWithParas = Arrays.stream(report.split("\\r\\n\\r\\n"))
                .map(sentence -> "<p>" + sentence + "</p>")
                .collect(Collectors.joining(""));
        return Arrays.stream(storyWithParas.split("\\r\\n"))
                .map(sentence -> sentence.endsWith("</p>") ? sentence : sentence + "<br/>")
                .collect(Collectors.joining(""));
    }

    public String getHomeTeamScorersAsString() {
        if (venue.getFirstChar() == 'A') {
            return getOppositionGoalsAsString();
        }
        return getHendonGoalsAsString();
    }

    public String getAwayTeamScorersAsString() {
        if (venue.getFirstChar() == 'A') {
            return getHendonGoalsAsString();
        }
        return getOppositionGoalsAsString();
    }

    public long getHomeTeamId() {
        if (venue.getFirstChar() == 'A') {
            return opposition;
        }
        return HENDON_ID;
    }

    public long getActualVenueId() {
        if (alternativeVenue != -1) {
            return alternativeVenue;
        }
        if (venue.getFirstChar() == 'A') {
            return opposition;
        }
        return HENDON_ID;
    }

    public long getAwayTeamId() {
        if (venue.getFirstChar() == 'A') {
            return HENDON_ID;
        }
        return opposition;
    }


    public String getHomeTeamName() {
        if (venue.getFirstChar() == 'A') {
            return getOppositionName();
        }
        return "Hendon";
    }

    public String getAwayTeamName() {
        if (venue.getFirstChar() == 'A') {
            return "Hendon";
        }
        return getOppositionName();
    }

    public String getHomeTeamScore() {
        if (-1 != abandonedMinute) {
            return "A";
        }

        if (-1 == getHendonScore()) {
            return "L";
        }

        if (venue.getFirstChar() == 'A') {
            return Integer.toString(getOppositionScore());
        }
        return Integer.toString(getHendonScore());
    }

    public String getAwayTeamScore() {
        if (-1 != abandonedMinute) {
            return "A";
        }

        if (-1 == getHendonScore()) {
            return "L";
        }

        if (venue.getFirstChar() == 'A') {
            return Integer.toString(getHendonScore());
        }
        return Integer.toString(getOppositionScore());
    }

    public String getHendonGoalsAsString() {
        if (null == hendonGoals) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        int count;
        List<FixtureGoal> goals = new ArrayList<>(hendonGoals);
        sortHendonGoals(goals);

        for (FixtureGoal goal : goals) {
            if (goal.isOwnGoal()) {
                handleOwnGoals(builder, new ArrayList<>(goals));
                continue;
            }
            if (-1 == builder.indexOf(getHendonScorerName(goal))) {
                builder.append(getHendonScorerName(goal));
                count = countNumberGoals(goals, goal);
                appendNumGoalsAndCommaSuffix(builder, count);
            }
        }

        handleUnknown(builder, goals.size(), getHendonScore());

        if (getHendonScore() > 0) {
            return builder.substring(0, builder.length() - 2);
        }
        return builder.toString();
    }

    private static int countNumberGoals(List<FixtureGoal> goals, FixtureGoal goal) {
        int count;
        count = 0;
        for (FixtureGoal goal2 : goals) {
            if (goal.getScorer().getId().equals(goal2.getScorer().getId())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the name of the scorer of the given goal in the form "J Bloggs".
     */
    private String getHendonScorerName(FixtureGoal goal) {
        return goal.getScorer().getFirstNameForSeason(getSeason()).charAt(0) + " "
                + goal.getScorer().getLastNameForSeason(getSeason());
    }

    public String getOppositionGoalsAsString() {
        if (null == oppositionGoals) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        int count;
        List<OppositionGoal> goals = new ArrayList<>(oppositionGoals);
        sortOppositionGoals(goals);

        for (OppositionGoal goal : goals) {
            if (goal.isOwnGoal()) {
                handleOppositionOwnGoals(builder, new ArrayList<>(goals));
                continue;
            }
            if (-1 == builder.indexOf(getOppositionScorerName(goal))) {
                builder.append(getOppositionScorerName(goal));
                count = countNumberScorers(goals, goal);
                appendNumGoalsAndCommaSuffix(builder, count);
            }
        }
        handleUnknown(builder, goals.size(), getOppositionScore());
        if (getOppositionScore() > 0) {
            return builder.substring(0, builder.length() - 2);
        }
        return builder.toString();
    }

    private static int countNumberScorers(List<OppositionGoal> goals, OppositionGoal goal) {
        int count;
        count = 0;
        for (OppositionGoal goal2 : goals) {
            if (goal.getFirstName().equals(goal2.getFirstName())
                    && goal.getLastName().equals(goal2.getLastName())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Returns the name of the scorer of the given goal in the form "J Bloggs".
     */
    private static String getOppositionScorerName(OppositionGoal goal) {
        return goal.getFirstName().charAt(0) + " " + goal.getLastName();
    }

    private static void handleOwnGoals(StringBuilder builder, List<FixtureGoal> goals) {
        if (-1 == builder.indexOf(OWN_GOAL)) {
            builder.append(OWN_GOAL);
            int count = countOwnGoals(goals);
            appendNumGoalsAndCommaSuffix(builder, count);
        }
    }
    private static void handleOppositionOwnGoals(StringBuilder builder, List<OppositionGoal> goals) {
        if (-1 == builder.indexOf(OWN_GOAL)) {
            builder.append(OWN_GOAL);
            int count = countOppositionOwnGoals(goals);
            appendNumGoalsAndCommaSuffix(builder, count);
        }
    }

    private static int countOwnGoals(List<FixtureGoal> goals) {
        int count = 0;
        for (FixtureGoal goal : goals) {
            if (goal.isOwnGoal()) {
                count++;
            }
        }
        return count;
    }

    private static int countOppositionOwnGoals(List<OppositionGoal> goals) {
        int count = 0;
        for (OppositionGoal goal : goals) {
            if (goal.isOwnGoal()) {
                count++;
            }
        }
        return count;
    }

    private static void handleUnknown(StringBuilder builder, int numKnown, int score) {
        if (score > numKnown) {
            builder.append("Unknown");
            int diff = score - numKnown;
            appendNumGoalsAndCommaSuffix(builder, diff);
        }
    }

    private static void appendNumGoalsAndCommaSuffix(StringBuilder builder, int count) {
        if (count > 1) {
            builder.append(" ");
            builder.append(count);
        }
        builder.append(", ");
    }

    public boolean getHasSponsors() {
        if (null != mascot && !"".equals(mascot)) {
            return true;
        }
        if (null != matchballSponsor && !"".equals(matchballSponsor)) {
            return true;
        }
        return null != matchSponsor && !"".equals(matchSponsor);
    }

    private static void sortHendonGoals(List<FixtureGoal> hendonGoals) {
        FixtureGoal temp;
        FixtureGoal item1;
        FixtureGoal item2;
        for (int i = hendonGoals.size(); i > 0; i--) {
            for (int j = 1; j < i; j++) {
                item1 = hendonGoals.get(j - 1);
                item2 = hendonGoals.get(j);
                if (item1.compareToForGoalList(item2) < 0) {
                    temp = hendonGoals.get(j - 1);
                    hendonGoals.set(j - 1, hendonGoals.get(j));
                    hendonGoals.set(j, temp);
                }
            }
        }
    }

    private static void sortOppositionGoals(List<OppositionGoal> oppositionTeam) {
        OppositionGoal temp;
        OppositionGoal item1;
        OppositionGoal item2;
        for (int i = oppositionTeam.size(); i > 0; i--) {
            for (int j = 1; j < i; j++) {
                item1 = oppositionTeam.get(j - 1);
                item2 = oppositionTeam.get(j);
                if (item1.compareToForGoalList(item2) < 0) {
                    temp = oppositionTeam.get(j - 1);
                    oppositionTeam.set(j - 1, oppositionTeam.get(j));
                    oppositionTeam.set(j, temp);
                }
            }
        }
    }
}
