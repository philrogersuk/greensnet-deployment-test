package net.greensnet.competition.league;

import net.greensnet.club.dao.ClubEntity;
import net.greensnet.club.service.ClubService;
import net.greensnet.competition.CompetitionService;
import net.greensnet.competition.dao.CompetitionEntity;
import net.greensnet.competition.league.dao.LeagueFixtureEntity;
import net.greensnet.competition.league.dao.LeagueFixtureRepository;
import net.greensnet.competition.league.domain.LeagueFixture;
import net.greensnet.competition.league.domain.LeagueFixtureEntityToDomainConverter;
import net.greensnet.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeagueFixtureServiceImpl implements LeagueFixtureService {

    private final LeagueFixtureRepository leagueFixtureRepository;
    private final LeagueFixtureEntityToDomainConverter converter;
    private final CompetitionService competitionService;
    private final ClubService clubService;

    @Autowired
    public LeagueFixtureServiceImpl (LeagueFixtureRepository leagueFixtureRepository,
                                     LeagueFixtureEntityToDomainConverter converter,
                                     CompetitionService competitionService,
                                     ClubService clubService) {
        this.leagueFixtureRepository = leagueFixtureRepository;
        this.converter = converter;
        this.competitionService = competitionService;
        this.clubService = clubService;
    }


    @Override
    public void createLeagueFixture(Long homeId, Long awayId, int season, Long competitionId, int homeGoals,
                                    int awayGoals, int attendance, LocalDate date) {
        LeagueFixtureEntity item = new LeagueFixtureEntity();
        processLeagueFixture(homeId, awayId, season, competitionId, homeGoals, awayGoals, attendance, date, item);
    }

    @Override
    public void editLeagueFixture(Long id, Long homeId, Long awayId, int season, Long competitionId, int homeGoals,
                                     int awayGoals, int attendance, LocalDate date) {
        LeagueFixtureEntity item = getLeagueFixtureEntity(id);
        processLeagueFixture(homeId, awayId, season, competitionId, homeGoals, awayGoals, attendance, date, item);
    }

    private LeagueFixtureEntity getLeagueFixtureEntity(Long id) {
        Optional<LeagueFixtureEntity> optional = leagueFixtureRepository.findById(id);
        return optional.orElseThrow(NotFoundException::new);
    }

    private void processLeagueFixture(Long homeId, Long awayId, int season, Long competitionId, int homeGoals, int awayGoals, int attendance, LocalDate date, LeagueFixtureEntity item) {
        CompetitionEntity competition = competitionService.getCompetitionEntity(competitionId);
        ClubEntity homeClub = clubService.getClubEntity(homeId);
        ClubEntity awayClub = clubService.getClubEntity(awayId);
        item.setSeason(season);
        item.setCompetition(competition);
        item.setHomeClub(homeClub);
        item.setAwayClub(awayClub);
        item.setHomeGoals(homeGoals);
        item.setAwayGoals(awayGoals);
        item.setAttendance(attendance);
        item.setFixtureDate(date);
        leagueFixtureRepository.save(item);
    }

    @Override
    public List<LeagueFixture> getFixtures(Long competitionId, int season) {
        List<LeagueFixtureEntity> fixtures = leagueFixtureRepository.findByCompetition_IdAndSeason(competitionId, season);
        return fixtures.stream().map(converter::convert)
                .collect(Collectors.toList());
    }

    @Override
    public LeagueFixture getLeagueFixture(Long id) {
        return converter.convert(getLeagueFixtureEntity(id));
    }

    @Override
    public void removeLeagueFixture(Long id) {
        leagueFixtureRepository.deleteById(id);
    }

    @Override
    public LeagueFixture getMostRecentFixture(Long competitionId, int season) {
        Optional<LeagueFixtureEntity> entity = leagueFixtureRepository.findFirstByCompetition_IdAndSeasonOrderByFixtureDateDesc(competitionId, season);
        return entity.map(converter::convert)
                .orElse(null);

   }
}
