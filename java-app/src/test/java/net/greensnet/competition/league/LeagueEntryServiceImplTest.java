package net.greensnet.competition.league;

import net.greensnet.competition.league.dao.LeagueEntryEntity;
import net.greensnet.competition.league.dao.LeagueEntryRepository;
import net.greensnet.competition.league.domain.LeagueEntry;
import net.greensnet.competition.league.domain.LeagueEntryEntityToDomainConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LeagueEntryServiceImplTest {

	@Mock
	private LeagueEntryRepository leagueEntryRepository;
	@Autowired
	private LeagueEntryEntityToDomainConverter converter;

	private LeagueEntryService leagueEntryService;

	@BeforeEach
	public void setup() {
		leagueEntryService = new LeagueEntryServiceImpl(leagueEntryRepository,
				converter);
	}

	@Test
	public void shouldCreateLeagueEntry() {
		int season = 2000;
		long competitionId = 2;
		long clubId = 101;
		String clubName = "Hendon";

		LeagueEntry newEntry = LeagueEntry.builder()
				.competitionId(competitionId)
				.season(season)
				.clubId(clubId)
				.clubName(clubName)
				.build();


		LeagueEntryEntity expected = LeagueEntryEntity.builder()
				.competitionId(competitionId)
				.season(season)
				.clubId(clubId)
				.clubName(clubName)
				.build();

		leagueEntryService.createLeagueEntry(newEntry);

		verify(leagueEntryRepository).save(expected);
	}

	@Test
	public void shouldGetSeasonsEnteredForClub() {
		long clubId = 101;
		List<Integer> expected = newArrayList(2000, 2001, 2002, 2003);

		when(leagueEntryRepository.findSeasonsEnteredByClub(101)).thenReturn(expected);

		List<Integer> actual = leagueEntryService.getSeasonsWithKnownEntryForClub(clubId);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void shouldGetClubsEnteredForCompetitionInSeason() {
		long competitionId = 2;
		int season = 2000;
		long clubId1 = 123L;
		long clubId2 = 234L;
		long clubId3 = 345L;

		List<LeagueEntryEntity> entities = createLeagueEntryEntities(clubId1, clubId2, clubId3);
		List<LeagueEntry> expected = createLeagueEntries(clubId1, clubId2, clubId3);

		when(leagueEntryRepository.findByCompetitionIdAndSeason(competitionId, season))
				.thenReturn(entities);

		List<LeagueEntry> actual = leagueEntryService.getLeagueEntries(season, competitionId);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void shouldGetClubEntriesBySeason() {
		int season = 2000;
		long clubId = 222;

		List<LeagueEntryEntity> entities = createLeagueEntryEntities(2L, 3L);
		List<Long> expected = newArrayList(2L, 3L);

		when(leagueEntryRepository.findByClubIdAndSeason(clubId, season))
				.thenReturn(entities);

		List<Long> actual = leagueEntryService.getCompetitionsEnteredByClub(season, clubId);

		assertThat(actual).isEqualTo(expected);
	}

	@Test
	public void shouldGetAllLeagueEntries() {
		int season = 2000;
		long competitionId = 222;
		long clubId = 381746;

		List<LeagueEntryEntity> entities = newArrayList(LeagueEntryEntity.builder()
				.clubId(clubId)
				.competitionId(competitionId)
				.season(season)
				.id(32L)
				.build());
		List<LeagueEntry> expected = newArrayList(LeagueEntry.builder()
				.clubId(clubId)
				.competitionId(competitionId)
				.season(season)
				.id(32L)
				.build());

		when(leagueEntryRepository.findAll())
				.thenReturn(entities);

		List<LeagueEntry> actual = leagueEntryService.getAllEntries();

		assertThat(actual).isEqualTo(expected);
	}

	private List<LeagueEntryEntity> createLeagueEntryEntities(long competition1, long competition2) {
		return newArrayList(
				LeagueEntryEntity.builder()
						.competitionId(competition1)
						.build(),
				LeagueEntryEntity.builder()
						.competitionId(competition2)
						.build()
		);
	}

	private ArrayList<LeagueEntryEntity> createLeagueEntryEntities(long club1, long club2, long club3) {
		return newArrayList(
				LeagueEntryEntity.builder()
						.clubId(club1)
						.build(),
				LeagueEntryEntity.builder()
						.clubId(club2)
						.build(),
				LeagueEntryEntity.builder()
						.clubId(club3)
						.build()
		);
	}

	private ArrayList<LeagueEntry> createLeagueEntries(long club1, long club2, long club3) {
		return newArrayList(
				LeagueEntry.builder()
						.clubId(club1)
						.build(),
				LeagueEntry.builder()
						.clubId(club2)
						.build(),
				LeagueEntry.builder()
						.clubId(club3)
						.build()
		);
	}
}
