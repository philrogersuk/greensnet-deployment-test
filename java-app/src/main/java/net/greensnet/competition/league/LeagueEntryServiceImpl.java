package net.greensnet.competition.league;

import net.greensnet.club.domain.Club;
import net.greensnet.competition.league.dao.LeagueEntryEntity;
import net.greensnet.competition.league.dao.LeagueEntryRepository;
import net.greensnet.competition.league.domain.LeagueEntry;
import net.greensnet.competition.league.domain.LeagueEntryEntityToDomainConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@Service
public class LeagueEntryServiceImpl implements LeagueEntryService {

    private final LeagueEntryRepository leagueEntryRepository;
    private final LeagueEntryEntityToDomainConverter converter;

    @Autowired
    public LeagueEntryServiceImpl(LeagueEntryRepository leagueEntryRepository,
                                  LeagueEntryEntityToDomainConverter converter) {
        this.leagueEntryRepository = leagueEntryRepository;
        this.converter = converter;
    }

    @Override
    public List<Long> getCompetitionsEnteredByClub(int season, long clubId) {
        return leagueEntryRepository.findByClubIdAndSeason(clubId, season).stream()
                .map(LeagueEntryEntity::getCompetitionId)
                .collect(toList());
    }

    @Override
    public void createLeagueEntry(LeagueEntry leagueEntry) {
        LeagueEntryEntity item = LeagueEntryEntity.builder()
                .clubId(leagueEntry.getClubId())
                .clubName(leagueEntry.getClubName())
                .competitionId(leagueEntry.getCompetitionId())
                .season(leagueEntry.getSeason())
                .build();
        leagueEntryRepository.save(item);
    }

    @Override
    public List<LeagueEntry> getLeagueEntries(int season, long competitionId) {
        return leagueEntryRepository.findByCompetitionIdAndSeason(competitionId, season).stream()
                .map(converter::convert)
                .collect(toList());
    }

    @Override
    public List<Integer> getSeasonsWithKnownEntryForClub(long clubId) {
        return leagueEntryRepository.findSeasonsEnteredByClub(clubId);
    }

    @Override
    public List<LeagueEntry> getAllEntries() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(leagueEntryRepository.findAll().iterator(), 0), false)
                .map(converter::convert)
                .collect(toList());
    }

    @Override
    public void updateClubNames(Club club) {
        List<LeagueEntry> entries = leagueEntryRepository.findByClubId(club.getId());
        for(LeagueEntry entry: entries) {
            entry.setClubName(club.getNameForSeason(entry.getSeason()));
        }

    }
}
