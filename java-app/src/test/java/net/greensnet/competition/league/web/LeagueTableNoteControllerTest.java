package net.greensnet.competition.league.web;

import net.greensnet.competition.CompetitionService;
import net.greensnet.competition.domain.Competition;
import net.greensnet.competition.league.LeagueEntryService;
import net.greensnet.competition.league.LeagueTableNoteService;
import net.greensnet.competition.league.domain.LeagueEntry;
import net.greensnet.competition.league.domain.LeagueTableNote;
import net.greensnet.util.DateHelper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static net.greensnet.competition.league.web.LeagueTableNoteController.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class LeagueTableNoteControllerTest {

    @Mock
    private CompetitionService competitionService;
    @Mock
    private LeagueTableNoteService leagueTableNoteService;
    @Mock
    private LeagueEntryService leagueEntryService;
    @Mock
    private DateHelper dateHelper;

    private Model model;
    private LeagueTableNoteController controller;

    @BeforeEach
    public void setup() {
        model = new ConcurrentModel();
        controller = new LeagueTableNoteController(competitionService,
                leagueTableNoteService,
                leagueEntryService,
                dateHelper);
    }

    @Test
    public void shouldListSeasons() {
        int[] seasons = new int[] {1999, 2000, 2001};
        List<Competition> competitions = Lists.newArrayList(
                Competition.builder().id(1L).build(),
                Competition.builder().id(2L).build());

        when(dateHelper.getSeasonArray()).thenReturn(seasons);
        when(competitionService.getLeagueCompetitions()).thenReturn(competitions);

        String page = controller.handleRequest(model, null, null, null, null, null, null, null);

        assertThat(page).isEqualTo("th_admin/leaguenote/select");
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("League Table Notes - Select Competition");
        assertThat(model.getAttribute(SEASON_LIST)).isEqualTo(seasons);
        assertThat(model.getAttribute(LEAGUE_LIST)).isEqualTo(competitions);
    }

    @Test
    public void shouldDeleteNoteById() {
        long noteId = 231L;
        int season = 2010;
        long competitionId = 3L;
        LeagueTableNote note = LeagueTableNote.builder()
                .season(season)
                .competitionId(competitionId).build();
        List<LeagueTableNote> notes = Lists.newArrayList();
        List<LeagueEntry> leagueEntries = Lists.newArrayList(LeagueEntry.builder().build());

        when(leagueTableNoteService.getLeagueTableNote(noteId)).thenReturn(note);
        when(leagueTableNoteService.getLeagueTableNotes(season, competitionId)).thenReturn(notes);
        when(leagueEntryService.getLeagueEntries(season, competitionId)).thenReturn(leagueEntries);

        String page = controller.deleteNote(model, noteId);

        verify(leagueTableNoteService).removeLeagueNote(noteId);

        assertThat(page).isEqualTo("th_admin/leaguenote/list");
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("League Table Notes - Season 2010/2011");
        assertThat(model.getAttribute(CLUB_LIST)).isEqualTo(leagueEntries);
        assertThat(model.getAttribute(LEAGUE_TABLE_NOTES)).isEqualTo(notes);
    }
}
