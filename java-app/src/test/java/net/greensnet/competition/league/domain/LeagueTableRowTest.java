package net.greensnet.competition.league.domain;

import net.greensnet.club.dao.CrestEntity;
import org.junit.jupiter.api.Test;

import static net.greensnet.competition.league.domain.LeagueTableRow.UNKNOWN_CREST;
import static org.assertj.core.api.Assertions.assertThat;

public class LeagueTableRowTest {

    @Test
    public void shouldIncrementDeducted() {
        LeagueTableRow row = LeagueTableRow.builder().deducted(5).build();

        row.incrementDeducted(3);

        assertThat(row.getDeducted()).isEqualTo(8);
    }

    @Test
    public void shouldCalculatePointsWon3Drawn1() {
        LeagueTableRow row = LeagueTableRow.builder()
                .drawn(1)
                .won(3)
                .build();

        assertThat(row.getPoints()).isEqualTo(10);
    }

    @Test
    public void shouldCalculatePointsWon8Drawn0() {
        LeagueTableRow row = LeagueTableRow.builder()
                .won(8)
                .build();

        assertThat(row.getPoints()).isEqualTo(24);
    }

    @Test
    public void shouldCalculatePointsWon0Drawn4() {
        LeagueTableRow row = LeagueTableRow.builder()
                .drawn(4)
                .build();

        assertThat(row.getPoints()).isEqualTo(4);
    }

    @Test
    public void shouldCalculateGoalDifference() {
        LeagueTableRow row = LeagueTableRow.builder()
                .scored(47)
                .conceeded(16)
                .build();

        assertThat(row.getGoalDifference()).isEqualTo(31);
    }

    @Test
    public void shouldCalculateNegativeGoalDifference() {
        LeagueTableRow row = LeagueTableRow.builder()
                .scored(47)
                .conceeded(58)
                .build();

        assertThat(row.getGoalDifference()).isEqualTo(-11);
    }

    @Test
    public void shouldCalculateZeroGoalDifference() {
        LeagueTableRow row = LeagueTableRow.builder()
                .scored(46)
                .conceeded(46)
                .build();

        assertThat(row.getGoalDifference()).isEqualTo(0);
    }

    @Test
    public void shouldBeHendon() {
        LeagueTableRow row = LeagueTableRow.builder()
                .clubId(1L)
                .build();

        assertThat(row.isHendon()).isTrue();
    }

    @Test
    public void shouldBeNotHendon() {
        LeagueTableRow row = LeagueTableRow.builder()
                .clubId(2L)
                .build();

        assertThat(row.isHendon()).isFalse();
    }

    @Test
    public void shouldGetUnknownCrest() {
        LeagueTableRow row = LeagueTableRow.builder()
                .crest(null)
                .build();

        assertThat(row.getCrestUrl()).isEqualTo(UNKNOWN_CREST);
    }

    @Test
    public void shouldGetCrest() {
        LeagueTableRow row = LeagueTableRow.builder()
                .crest(CrestEntity.builder()
                        .awsUrl("1-hendon_h100.png")
                        .build())
                .build();

        assertThat(row.getCrestUrl()).isEqualTo("1-hendon_h40.png");
    }

    @Test
    public void testCompareToPoints() {
        LeagueTableRow row = LeagueTableRow.builder().build();
        row.setWon(1);
        LeagueTableRow other = LeagueTableRow.builder().build();
        other.setDrawn(2);
        assertThat(row.compareTo(other)).isEqualTo(-1);
        row.setWon(0);
        assertThat(row.compareTo(other)).isEqualTo(1);
    }

    @Test
    public void testCompareToGD() {
        LeagueTableRow row = LeagueTableRow.builder().build();
        row.setScored(1);
        LeagueTableRow other = LeagueTableRow.builder().build();
        assertThat(row.compareTo(other)).isEqualTo(-1);
        row.setConceeded(3);
        assertThat(row.compareTo(other)).isEqualTo(1);
    }

    @Test
    public void testCompareToScored() {
        LeagueTableRow row = LeagueTableRow.builder().build();
        row.setScored(2);
        row.setConceeded(1);
        LeagueTableRow other = LeagueTableRow.builder().build();
        other.setScored(1);
        assertThat(row.compareTo(other)).isEqualTo(-1);
        other.setScored(3);
        other.setConceeded(2);
        assertThat(row.compareTo(other)).isEqualTo(1);
    }

    @Test
    public void testCompareToNames() {
        LeagueTableRow row = LeagueTableRow.builder().build();
        row.setClubName("Aaaa");
        LeagueTableRow other = LeagueTableRow.builder().build();
        other.setClubName("Dddd");
        assertThat(row.compareTo(other)).isLessThan(0);
        row.setClubName("Eeeee");
        assertThat(row.compareTo(other)).isGreaterThan(0);
    }

}
