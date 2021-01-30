package net.greensnet.competition.league;

import com.google.common.collect.Lists;
import net.greensnet.club.dao.ClubEntity;
import net.greensnet.club.service.ClubService;
import net.greensnet.competition.CompetitionService;
import net.greensnet.competition.dao.CompetitionEntity;
import net.greensnet.competition.domain.Competition;
import net.greensnet.competition.domain.CompetitionEntityToDomainConverter;
import net.greensnet.competition.league.dao.LeagueFixtureEntity;
import net.greensnet.competition.league.dao.LeagueFixtureRepository;
import net.greensnet.competition.league.domain.LeagueFixture;
import net.greensnet.competition.league.domain.LeagueFixtureEntityToDomainConverter;
import net.greensnet.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LeagueFixtureServiceImplTest {

	@Mock
	private ClubService clubService;
	@Mock
	private LeagueFixtureRepository leagueFixtureRepository;
	@Mock
	private CompetitionService competitionService;
	private LeagueFixtureEntityToDomainConverter converter;

	private LeagueFixtureService leagueFixtureService;

	@BeforeEach
	public void setup() {
		converter = new LeagueFixtureEntityToDomainConverter(
				new CompetitionEntityToDomainConverter()
		);
		leagueFixtureService = new LeagueFixtureServiceImpl(leagueFixtureRepository,
				converter,
				competitionService,
				clubService);
	}

	@Test
	public void shouldCreateLeagueEntry() {
		int attendance = 328;
		ClubEntity awayClub = ClubEntity.builder().id(28L).build();
		ClubEntity homeClub = ClubEntity.builder().id(841L).build();
		int awayGoals = 2;
		int homeGoals = 1;
		CompetitionEntity competition = CompetitionEntity.builder().id(4L).build();
		LocalDate date = LocalDate.now();
		int season = 2000;

		LeagueFixtureEntity expected = LeagueFixtureEntity.builder()
				.attendance(attendance)
				.awayClub(awayClub)
				.homeClub(homeClub)
				.awayGoals(awayGoals)
				.homeGoals(homeGoals)
				.competition(competition)
				.fixtureDate(date)
				.id(null)
				.season(season)
				.build();

		when(clubService.getClubEntity(homeClub.getId())).thenReturn(homeClub);
		when(clubService.getClubEntity(awayClub.getId())).thenReturn(awayClub);
		when(competitionService.getCompetitionEntity(competition.getId())).thenReturn(competition);

		leagueFixtureService.createLeagueFixture(homeClub.getId(),
				awayClub.getId(),
				season,
				competition.getId(),
				homeGoals,
				awayGoals,
				attendance,
				date);

		verify(leagueFixtureRepository).save(expected);
	}

	@Test
	public void shouldEditLeagueEntry() {
		int attendance = 328;
		ClubEntity awayClub = ClubEntity.builder().id(28L).build();
		ClubEntity homeClub = ClubEntity.builder().id(841L).build();
		int awayGoals = 2;
		int homeGoals = 1;
		CompetitionEntity competition = CompetitionEntity.builder().id(4L).build();
		LocalDate date = LocalDate.now();
		int season = 2000;
		long fixtureId = 214L;

		LeagueFixtureEntity previous = LeagueFixtureEntity.builder()
				.id(fixtureId)
				.build();

		LeagueFixtureEntity expected = LeagueFixtureEntity.builder()
				.attendance(attendance)
				.awayClub(awayClub)
				.homeClub(homeClub)
				.awayGoals(awayGoals)
				.homeGoals(homeGoals)
				.competition(competition)
				.fixtureDate(date)
				.id(fixtureId)
				.season(season)
				.build();

		when(leagueFixtureRepository.findById(fixtureId)).thenReturn(Optional.of(previous));
		when(clubService.getClubEntity(homeClub.getId())).thenReturn(homeClub);
		when(clubService.getClubEntity(awayClub.getId())).thenReturn(awayClub);
		when(competitionService.getCompetitionEntity(competition.getId())).thenReturn(competition);

		leagueFixtureService.editLeagueFixture(fixtureId,
				homeClub.getId(),
				awayClub.getId(),
				season,
				competition.getId(),
				homeGoals,
				awayGoals,
				attendance,
				date);

		verify(leagueFixtureRepository).save(expected);
	}

	@Test
	public void shouldThrowNotFoundExceptionOnUpdateOfFixtureThatDoesntExist() {
		int attendance = 328;
		ClubEntity awayClub = ClubEntity.builder().id(28L).build();
		ClubEntity homeClub = ClubEntity.builder().id(841L).build();
		int awayGoals = 2;
		int homeGoals = 1;
		CompetitionEntity competition = CompetitionEntity.builder().id(4L).build();
		LocalDate date = LocalDate.now();
		int season = 2000;
		long fixtureId = 214L;

		when(leagueFixtureRepository.findById(fixtureId)).thenReturn(Optional.empty());

		assertThatThrownBy( () -> leagueFixtureService.editLeagueFixture(fixtureId,
				homeClub.getId(),
				awayClub.getId(),
				season,
				competition.getId(),
				homeGoals,
				awayGoals,
				attendance,
				date)).isInstanceOf(NotFoundException.class);
	}

	@Test
	public void shouldFindFixtureById() {
		int attendance = 328;
		ClubEntity awayClub = ClubEntity.builder().id(28L).build();
		ClubEntity homeClub = ClubEntity.builder().id(841L).build();
		int awayGoals = 2;
		int homeGoals = 1;
		CompetitionEntity competitionEntity = CompetitionEntity.builder().id(4L).build();
		Competition competition = Competition.builder().id(4L).build();
		LocalDate date = LocalDate.now();
		int season = 2000;
		long fixtureId = 214L;

		LeagueFixtureEntity entity = LeagueFixtureEntity.builder()
				.attendance(attendance)
				.awayClub(awayClub)
				.homeClub(homeClub)
				.awayGoals(awayGoals)
				.homeGoals(homeGoals)
				.competition(competitionEntity)
				.fixtureDate(date)
				.id(fixtureId)
				.season(season)
				.build();
		LeagueFixture expected = LeagueFixture.builder()
				.attendance(attendance)
				.awayClub(awayClub)
				.homeClub(homeClub)
				.awayGoals(awayGoals)
				.homeGoals(homeGoals)
				.competition(competition)
				.fixtureDate(date)
				.id(fixtureId)
				.season(season)
				.build();

		when(leagueFixtureRepository.findById(fixtureId)).thenReturn(Optional.of(entity));

		LeagueFixture actual = leagueFixtureService.getLeagueFixture(fixtureId);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void shouldThrowNotFoundExceptionWhenFixtureNotFoundById() {
		long fixtureId = 214L;

		when(leagueFixtureRepository.findById(fixtureId)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> leagueFixtureService.getLeagueFixture(fixtureId))
			.isInstanceOf(NotFoundException.class);
	}

	@Test
	public void shouldGetFixturesByCompetitionAndSeason() {
		int attendance = 328;
		ClubEntity awayClub = ClubEntity.builder().id(28L).build();
		ClubEntity homeClub = ClubEntity.builder().id(841L).build();
		int awayGoals = 2;
		int homeGoals = 1;
		CompetitionEntity competitionEntity = CompetitionEntity.builder().id(4L).build();
		Competition competition = Competition.builder().id(4L).build();
		LocalDate date = LocalDate.now();
		int season = 2000;
		long fixtureId = 214L;

		LeagueFixtureEntity fixtureEntity = LeagueFixtureEntity.builder()
				.attendance(attendance)
				.awayClub(awayClub)
				.homeClub(homeClub)
				.awayGoals(awayGoals)
				.homeGoals(homeGoals)
				.competition(competitionEntity)
				.fixtureDate(date)
				.id(fixtureId)
				.season(season)
				.build();
		LeagueFixture fixture = LeagueFixture.builder()
				.attendance(attendance)
				.awayClub(awayClub)
				.homeClub(homeClub)
				.awayGoals(awayGoals)
				.homeGoals(homeGoals)
				.competition(competition)
				.fixtureDate(date)
				.id(fixtureId)
				.season(season)
				.build();

		List<LeagueFixture> expected = Lists.newArrayList(fixture);
		List<LeagueFixtureEntity> entities = Lists.newArrayList(fixtureEntity);

		when(leagueFixtureRepository.findByCompetition_IdAndSeason(competition.getId(), season)).thenReturn(entities);

		List<LeagueFixture> actual = leagueFixtureService.getFixtures(competition.getId(), season);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void shouldDeleteFixture() {
		long fixtureId = 2817L;

		leagueFixtureService.removeLeagueFixture(2817L);

		verify(leagueFixtureRepository).deleteById(fixtureId);
	}

	@Test
	public void shouldReturnMostRecentFixtureForSeason() {
		int attendance = 328;
		ClubEntity awayClub = ClubEntity.builder().id(28L).build();
		ClubEntity homeClub = ClubEntity.builder().id(841L).build();
		int awayGoals = 2;
		int homeGoals = 1;
		CompetitionEntity competitionEntity = CompetitionEntity.builder().id(4L).build();
		Competition competition = Competition.builder().id(4L).build();
		LocalDate date = LocalDate.now();
		int season = 2000;
		long fixtureId = 214L;

		LeagueFixtureEntity entity = LeagueFixtureEntity.builder()
				.attendance(attendance)
				.awayClub(awayClub)
				.homeClub(homeClub)
				.awayGoals(awayGoals)
				.homeGoals(homeGoals)
				.competition(competitionEntity)
				.fixtureDate(date)
				.id(fixtureId)
				.season(season)
				.build();


		LeagueFixture expected = LeagueFixture.builder()
				.attendance(attendance)
				.awayClub(awayClub)
				.homeClub(homeClub)
				.awayGoals(awayGoals)
				.homeGoals(homeGoals)
				.competition(competition)
				.fixtureDate(date)
				.id(fixtureId)
				.season(season)
				.build();

		when(leagueFixtureRepository.findFirstByCompetition_IdAndSeasonOrderByFixtureDateDesc(competition.getId(), season)).thenReturn(Optional.of(entity));

		LeagueFixture actual = leagueFixtureService.getMostRecentFixture(competition.getId(), season);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void shouldReturnNullWhenNoMostRecentFixture() {
		long competitionId = 1L;
		int season = 2000;

		when(leagueFixtureRepository.findFirstByCompetition_IdAndSeasonOrderByFixtureDateDesc(competitionId, season)).thenReturn(Optional.empty());

		LeagueFixture actual = leagueFixtureService.getMostRecentFixture(competitionId, season);

		assertThat(actual).isNull();
	}
}
