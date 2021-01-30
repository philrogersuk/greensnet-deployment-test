package net.greensnet.competition;

import com.google.common.collect.Lists;
import net.greensnet.competition.domain.Competition;
import net.greensnet.competition.domain.CompetitionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.util.List;

import static net.greensnet.competition.CompetitionAdminController.*;
import static net.greensnet.competition.domain.CompetitionType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class CompetitionAdminControllerTest {

    @Mock
    private CompetitionService competitionService;

    private CompetitionAdminController controller;
    private Model model;

    @BeforeEach
    public void setup() {
        controller = new CompetitionAdminController(competitionService);
        model = new ConcurrentModel();
    }

    @Test
    public void shouldGetExistingCompetitions() {
        List<Competition> expected = shouldReturnAllCompetitions();

        String pageName = controller.showCompetitions(model);

        assertThat(pageName).isEqualTo("th_admin/competition");
        assertThat(model.getAttribute(COMPETITION_TYPES))
                .isEqualTo(new CompetitionType[] {LEAGUE, CUP, FRIENDLY});
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Competitions");
        assertThat(model.getAttribute(COMPETITIONS)).isEqualTo(expected);
    }

    @Test
    public void shouldCreateNewCompetition() {
        List<Competition> expected = shouldReturnAllCompetitions();

        String pageName = controller.createCompetition(model, "FA Cup", "FAC", CUP);

        assertThat(pageName).isEqualTo("th_admin/competition");
        verify(competitionService).createCompetition("FA Cup", "FAC", CUP);
        assertThat(model.getAttribute(COMPETITION_TYPES))
                .isEqualTo(new CompetitionType[] {LEAGUE, CUP, FRIENDLY});
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Competitions");
        assertThat(model.getAttribute(COMPETITIONS)).isEqualTo(expected);
    }

    private List<Competition> shouldReturnAllCompetitions() {
        List<Competition> expected = Lists.newArrayList(
                Competition.builder()
                        .id(1L)
                        .name("FA Cup")
                        .shortCode("FAC")
                        .type(CUP)
                        .build()
        );
        when(competitionService.getAllCompetitions()).thenReturn(expected);
        return expected;
    }
}
