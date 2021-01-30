package net.greensnet.competition.league;

import net.greensnet.club.dao.ClubEntity;
import net.greensnet.club.service.ClubService;
import net.greensnet.competition.league.dao.LeagueTableNoteEntity;
import net.greensnet.competition.league.dao.LeagueTableNoteRepository;
import net.greensnet.competition.league.domain.LeagueTableNote;
import net.greensnet.competition.league.domain.LeagueTableNoteEntityToDomainConverter;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LeagueTableNoteServiceTest {

    @Mock
    private ClubService clubService;
    @Mock
    private LeagueTableNoteRepository repository;

    private LeagueTableNoteService service;
    public static final long COMPETITION_ID = 1L;
    public static final ClubEntity CLUB = ClubEntity.builder().id(4L).build();
    public static final String REASON = "reason";
    public static final int POINTS_DEDUCTED = 3;
    public static final int SEASON = 2003;
    public static final long ID = 2938L;

    @BeforeEach
    public void setup() {
        LeagueTableNoteEntityToDomainConverter converter = new LeagueTableNoteEntityToDomainConverter();
        service = new LeagueTableNoteServiceImpl(clubService,
                repository,
                converter);
    }

    @Test
    public void shouldCreateLeagueTableNote() {
        LeagueTableNoteEntity expected = LeagueTableNoteEntity.builder()
                .club(CLUB)
                .competitionId(COMPETITION_ID)
                .deductionReason(REASON)
                .pointsDeducted(POINTS_DEDUCTED)
                .season(SEASON)
                .build();

        when(clubService.getClubEntity(CLUB.getId())).thenReturn(CLUB);

        service.createLeagueTableNote(SEASON, COMPETITION_ID, CLUB.getId(), POINTS_DEDUCTED, REASON);

        verify(repository).save(expected);
    }

    @Test
    public void getLeagueTableNotes() {
        LeagueTableNoteEntity entity = LeagueTableNoteEntity.builder()
                .club(CLUB)
                .competitionId(COMPETITION_ID)
                .deductionReason(REASON)
                .pointsDeducted(POINTS_DEDUCTED)
                .season(SEASON)
                .id(ID)
                .build();
        LeagueTableNote note = LeagueTableNote.builder()
                .club(CLUB)
                .competitionId(COMPETITION_ID)
                .deductionReason(REASON)
                .pointsDeducted(POINTS_DEDUCTED)
                .season(SEASON)
                .id(ID)
                .build();
        List<LeagueTableNote> expected = newArrayList(note);

        when(repository.findByCompetitionIdAndSeason(COMPETITION_ID, SEASON)).thenReturn(
                Lists.newArrayList(entity));

        List<LeagueTableNote> actual = service.getLeagueTableNotes(SEASON, COMPETITION_ID);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getLeagueTableNote() {
        LeagueTableNoteEntity entity = LeagueTableNoteEntity.builder()
                .club(CLUB)
                .competitionId(COMPETITION_ID)
                .deductionReason(REASON)
                .pointsDeducted(POINTS_DEDUCTED)
                .season(SEASON)
                .id(ID)
                .build();
        LeagueTableNote expected = LeagueTableNote.builder()
                .club(CLUB)
                .competitionId(COMPETITION_ID)
                .deductionReason(REASON)
                .pointsDeducted(POINTS_DEDUCTED)
                .season(SEASON)
                .id(ID)
                .build();

        when(repository.findById(entity.getId())).thenReturn(Optional.of(entity));

        LeagueTableNote actual = service.getLeagueTableNote(entity.getId());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldDeleteLeagueTableNoteById() {
        service.removeLeagueNote(1L);

        verify(repository).deleteById(1L);
    }


    @Test
    public void shouldEditLeagueTableNote() {
        LeagueTableNoteEntity initial = LeagueTableNoteEntity.builder()
                .club(CLUB)
                .competitionId(COMPETITION_ID)
                .deductionReason("oldReason")
                .pointsDeducted(1)
                .season(SEASON)
                .build();
        LeagueTableNoteEntity expected = LeagueTableNoteEntity.builder()
                .club(CLUB)
                .competitionId(COMPETITION_ID)
                .deductionReason(REASON)
                .pointsDeducted(POINTS_DEDUCTED)
                .season(SEASON)
                .build();

        when(clubService.getClubEntity(CLUB.getId())).thenReturn(CLUB);
        when(repository.findById(ID)).thenReturn(Optional.of(initial));

        service.editLeagueTableNote(ID, CLUB.getId(), POINTS_DEDUCTED, REASON);

        verify(repository).save(expected);
    }

}
