package net.greensnet.competition.league;

import net.greensnet.club.service.CrestService;
import net.greensnet.competition.CompetitionService;
import net.greensnet.competition.league.domain.LeagueTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static net.greensnet.club.service.ClubServiceImpl.HENDON_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TableServiceImplTest {

    @Mock
    private CompetitionService competitionService;
    @Mock
    private  CrestService crestService;
    @Mock
    private  LeagueTableNoteService leagueTableNoteService;
    @Mock
    private  LeagueEntryService leagueEntryService;
    @Mock
    private  LeagueFixtureService leagueFixtureService;

    private TableService service;

    @BeforeEach
    public void setup() {
        service = new TableServiceImpl(competitionService,
                crestService,
                leagueTableNoteService,
                leagueEntryService,
                leagueFixtureService);
    }

    @Test
    public void shouldReturnEmptyTablesListWhenHendonNotEntered() {
        int season = 2000;

        when(leagueEntryService.getCompetitionsEnteredByClub(season, HENDON_ID)).thenReturn(newArrayList());

        List<LeagueTable> actual = service.getTables(2000);

        assertThat(actual).isEmpty();
    }
}
