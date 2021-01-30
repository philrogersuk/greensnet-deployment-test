package net.greensnet.domain;

import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class FixtureTest {

    @Test
    public void testGetSetHendonTeam() {
        Set<FixtureAppearance> apps = Sets.newHashSet(FixtureAppearance.builder().build());
        Fixture fixture = new Fixture();
        fixture.setHendonTeam(apps);
        assertThat(fixture.getHendonTeam(), equalTo(apps));
    }

    @Test
    public void testGetSetOppositionTeam() {
        Set<OppositionAppearance> apps = Sets.newHashSet(OppositionAppearance.builder().build());
        Fixture fixture = new Fixture();
        fixture.setOppositionTeam(apps);
        assertThat(fixture.getOppositionTeam(), equalTo(apps));
    }

    @Test
    public void testGetSetHendonGoals() {
        Set<FixtureGoal> goals = Sets.newHashSet(FixtureGoal.builder().build());
        Fixture fixture = new Fixture();
        fixture.setHendonGoals(goals);
        assertThat(fixture.getHendonGoals(), equalTo(goals));
    }

    @Test
    public void testGetSetOppositionGoals() {
        Set<OppositionGoal> goals = Sets.newHashSet(OppositionGoal.builder().build());
        Fixture fixture = new Fixture();
        fixture.setOppositionGoals(goals);
        assertThat(fixture.getOppositionGoals(), equalTo(goals));
    }

    @Test
    public void shouldReturnEmptyStringWhenHendonGoalsNullOrEmpty() {
        Fixture fixture = new Fixture();
        fixture.setHendonGoals(null);
        assertThat(fixture.getHendonGoalsAsString(), equalTo(""));
        fixture.setHendonGoals(new HashSet<>());
        assertThat(fixture.getHendonGoalsAsString(), equalTo(""));
    }

    @Test
    public void shouldReturnScorerOnlyWhenSingleScorer() {
        StaffMember scorer = getPlayer();
        FixtureGoal goal = FixtureGoal.builder().scorer(scorer).build();
        Fixture fixture = new Fixture();
        fixture.setHendonScore(1);
        fixture.setHendonGoals(Sets.newHashSet(goal));
        assertThat(fixture.getHendonGoalsAsString(), equalTo("N Muir"));
    }

    @Test
    public void shouldReturnScorerOnlyWhenSingleScorerWithPenalty() {
        StaffMember scorer = getPlayer();
        FixtureGoal goal = FixtureGoal.builder().scorer(scorer).penalty(true).build();
        Fixture fixture = new Fixture();
        fixture.setHendonScore(1);
        fixture.setHendonGoals(Sets.newHashSet(goal));
        assertThat(fixture.getHendonGoalsAsString(), equalTo("N Muir"));
    }

    @Test
    public void shouldReturnScorerOnlyOwnGoal() {
        FixtureGoal goal = FixtureGoal.builder().ownGoal(true).build();
        Fixture fixture = new Fixture();
        fixture.setHendonScore(1);
        fixture.setHendonGoals(Sets.newHashSet(goal));
        assertThat(fixture.getHendonGoalsAsString(), equalTo("Own Goal"));
    }

    @Test
    public void shouldReturnUnknownIfScorerNotSetAndNotOwnGoal() {
        Fixture fixture = new Fixture();
        fixture.setHendonScore(1);
        fixture.setHendonGoals(Sets.newHashSet());
        assertThat(fixture.getHendonGoalsAsString(), equalTo("Unknown"));
    }

    @Test
    public void shouldReturnScorerAndNumGoalsWhenScoredTwice() {
        StaffMember scorer = getPlayer();
        FixtureGoal goal = FixtureGoal.builder().scorer(scorer).id(1L).build();
        FixtureGoal goal2 = FixtureGoal.builder().scorer(scorer).id(2L).build();
        Fixture fixture = new Fixture();
        fixture.setHendonScore(2);
        fixture.setHendonGoals(Sets.newHashSet(goal, goal2));
        assertThat(fixture.getHendonGoalsAsString(), equalTo("N Muir 2"));
    }

    @Test
    public void shouldReturnAllScorersInCorrectOrder() {
        StaffMember scorer = getStaffMemberWithId(1L, "Muir", "Niko");
        FixtureGoal goal = FixtureGoal.builder().scorer(scorer).minuteScored(10).id(1L).build();
        FixtureGoal goal2 = FixtureGoal.builder().scorer(scorer).minuteScored(40).id(2L).build();
        StaffMember scorer2 = getStaffMemberWithId(2L, "Binns", "Dale");
        FixtureGoal goal3 = FixtureGoal.builder().scorer(scorer2).minuteScored(6).id(3L).build();
        FixtureGoal goal4 = FixtureGoal.builder().scorer(scorer2).minuteScored(90).id(4L).build();
        Fixture fixture = new Fixture();
        fixture.setHendonScore(6);
        fixture.setHendonGoals(Sets.newHashSet(goal, goal2, goal3, goal4));
        assertThat(fixture.getHendonGoalsAsString(), equalTo("D Binns 2, N Muir 2, Unknown 2"));
    }

    @Test
    public void shouldReturnEmptyStringWhenOppositionGoalsNullOrEmpty() {
        Fixture fixture = new Fixture();
        fixture.setOppositionGoals(null);
        assertThat(fixture.getOppositionGoalsAsString(), equalTo(""));
        fixture.setOppositionGoals(new HashSet<>());
        assertThat(fixture.getOppositionGoalsAsString(), equalTo(""));
    }

    @Test
    public void shouldReturnOppositionScorerOnlyWhenOppositionSingleScorer() {
        OppositionGoal goal = OppositionGoal.builder().firstName("Niko").lastName("Muir").build();
        Fixture fixture = new Fixture();
        fixture.setOppositionScore(1);
        fixture.setOppositionGoals(Sets.newHashSet(goal));
        assertThat(fixture.getOppositionGoalsAsString(), equalTo("N Muir"));
    }

    @Test
    public void shouldReturnOppositionScorerOnlyWhenSingleScorerWithPenalty() {
        OppositionGoal goal = OppositionGoal.builder().firstName("Niko").lastName("Muir").penalty(true).build();
        Fixture fixture = new Fixture();
        fixture.setOppositionScore(1);
        fixture.setOppositionGoals(Sets.newHashSet(goal));
        assertThat(fixture.getOppositionGoalsAsString(), equalTo("N Muir"));
    }

    @Test
    public void shouldReturnOppositionScorerOnlyOwnGoal() {
        OppositionGoal goal = OppositionGoal.builder().ownGoal(true).build();
        Fixture fixture = new Fixture();
        fixture.setOppositionScore(1);
        fixture.setOppositionGoals(Sets.newHashSet(goal));
        assertThat(fixture.getOppositionGoalsAsString(), equalTo("Own Goal"));
    }

    @Test
    public void shouldReturnUnknownIfOppositionScorerNotSetAndNotOwnGoal() {
        Fixture fixture = new Fixture();
        fixture.setOppositionScore(1);
        fixture.setOppositionGoals(Sets.newHashSet());
        assertThat(fixture.getOppositionGoalsAsString(), equalTo("Unknown"));
    }

    @Test
    public void shouldReturnScorerAndNumGoalsWhenOppositionScoredTwice() {
        OppositionGoal goal = OppositionGoal.builder().firstName("Niko").lastName("Muir").id(1L).build();
        OppositionGoal goal2 = OppositionGoal.builder().firstName("Niko").lastName("Muir").id(2L).build();
        Fixture fixture = new Fixture();
        fixture.setOppositionScore(2);
        fixture.setOppositionGoals(Sets.newHashSet(goal, goal2));
        assertThat(fixture.getOppositionGoalsAsString(), equalTo("N Muir 2"));
    }

    @Test
    public void shouldReturnAllOppositionScorersInCorrectOrder() {
        OppositionGoal goal = OppositionGoal.builder().firstName("Niko").lastName("Muir").minuteScored(10).id(1L).build();
        OppositionGoal goal2 = OppositionGoal.builder().firstName("Niko").lastName("Muir").minuteScored(40).id(2L).build();
        OppositionGoal goal3 = OppositionGoal.builder().firstName("Dale").lastName("Binns").minuteScored(6).id(3L).build();
        OppositionGoal goal4 = OppositionGoal.builder().firstName("Dale").lastName("Binns").minuteScored(90).id(4L).build();
        Fixture fixture = new Fixture();
        fixture.setOppositionScore(6);
        fixture.setOppositionGoals(Sets.newHashSet(goal, goal2, goal3, goal4));
        assertThat(fixture.getOppositionGoalsAsString(), equalTo("D Binns 2, N Muir 2, Unknown 2"));
    }

    private StaffMember getStaffMemberWithId(long id, String firstName, String lastName) {
        return StaffMember.builder().id(id).lastName(firstName).firstName(lastName).build();
    }

    private StaffMember getPlayer() {
        return StaffMember.builder().id(1L).lastName("Muir").firstName("Niko").build();
    }

}
