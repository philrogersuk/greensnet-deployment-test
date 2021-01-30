package net.greensnet.competition;

import net.greensnet.competition.dao.CompetitionEntity;
import net.greensnet.competition.dao.CompetitionRepository;
import net.greensnet.competition.domain.Competition;
import net.greensnet.competition.domain.CompetitionEntityToDomainConverter;
import net.greensnet.exceptions.NotFoundException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Lists.newArrayList;
import static net.greensnet.competition.domain.CompetitionType.CUP;
import static net.greensnet.competition.domain.CompetitionType.LEAGUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class CompetitionServiceImplTest {

    @Mock
    private CompetitionRepository competitionRepository;

    private CompetitionEntityToDomainConverter competitionEntityToDomainConverter;

    private CompetitionService competitionService;

    @BeforeEach
    public void setup() {
        competitionEntityToDomainConverter = new CompetitionEntityToDomainConverter();
        competitionService = new CompetitionServiceImpl(competitionRepository,
                competitionEntityToDomainConverter);
    }

    @Test
    public void shouldGetCompetitionById() {
        CompetitionEntity entity = CompetitionEntity.builder()
                .id(1L)
                .name("name")
                .shortCode("FAC")
                .type(CUP)
                .build();
        Competition expected = Competition.builder()
                .id(1L)
                .name("name")
                .shortCode("FAC")
                .type(CUP)
                .build();

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(entity));

        Competition actual = competitionService.getCompetition(1L);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenCompetitionDoesntExist() {
        when(competitionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> competitionService.getCompetition(1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void shouldCreateCompetition() {
        CompetitionEntity entity = CompetitionEntity.builder()
                .name("name")
                .shortCode("FAC")
                .type(CUP)
                .build();

        competitionService.createCompetition("name", "FAC", CUP);

        verify(competitionRepository).save(entity);
    }

    @Test
    public void shouldGetAllCompetitions() {
        CompetitionEntity entity = CompetitionEntity.builder()
                .id(19L)
                .name("name")
                .shortCode("FAC")
                .type(CUP)
                .build();
        List<Competition> expected = newArrayList(Competition.builder()
                .id(19L)
                .name("name")
                .shortCode("FAC")
                .type(CUP)
                .build());

        when(competitionRepository.findAll()).thenReturn(Lists.newArrayList(entity));

        List<Competition> actual = competitionService.getAllCompetitions();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldGetLeagueCompetitions() {
        CompetitionEntity entity = CompetitionEntity.builder()
                .id(19L)
                .name("name")
                .shortCode("FAC")
                .type(CUP)
                .build();
        List<Competition> expected = newArrayList(Competition.builder()
                .id(19L)
                .name("name")
                .shortCode("FAC")
                .type(CUP)
                .build());

        when(competitionRepository.findByType(LEAGUE)).thenReturn(Lists.newArrayList(entity));

        List<Competition> actual = competitionService.getLeagueCompetitions();

        assertThat(actual).isEqualTo(expected);
    }
}
