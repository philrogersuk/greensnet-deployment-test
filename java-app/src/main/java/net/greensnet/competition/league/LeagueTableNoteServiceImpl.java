package net.greensnet.competition.league;

import net.greensnet.club.dao.ClubEntity;
import net.greensnet.club.service.ClubService;
import net.greensnet.competition.league.dao.LeagueTableNoteEntity;
import net.greensnet.competition.league.dao.LeagueTableNoteRepository;
import net.greensnet.competition.league.domain.LeagueTableNote;
import net.greensnet.competition.league.domain.LeagueTableNoteEntityToDomainConverter;
import net.greensnet.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class LeagueTableNoteServiceImpl implements LeagueTableNoteService {

    private final ClubService clubService;
    private final LeagueTableNoteRepository leagueTableNoteRepository;
    private final LeagueTableNoteEntityToDomainConverter leagueTableNoteConverter;

    @Autowired
    public LeagueTableNoteServiceImpl(ClubService clubService,
                                      LeagueTableNoteRepository leagueTableNoteRepository,
                                      LeagueTableNoteEntityToDomainConverter leagueTableNoteConverter) {
        this.clubService = clubService;
        this.leagueTableNoteRepository = leagueTableNoteRepository;
        this.leagueTableNoteConverter = leagueTableNoteConverter;
    }

    @Override
    public void createLeagueTableNote(int season, Long competitionId, Long clubId, int points, String reason) {
        LeagueTableNoteEntity item = new LeagueTableNoteEntity();
        ClubEntity club = clubService.getClubEntity(clubId);
        item.setSeason(season);
        item.setCompetitionId(competitionId);
        item.setClub(club);
        item.setPointsDeducted(points);
        item.setDeductionReason(reason);
        leagueTableNoteRepository.save(item);
    }

    @Override
    public LeagueTableNote getLeagueTableNote(long id) {
        Optional<LeagueTableNoteEntity> optional = leagueTableNoteRepository.findById(id);
        return optional.map(leagueTableNoteConverter::convert)
                .orElse(null);
    }

    @Override
    public List<LeagueTableNote> getLeagueTableNotes(int season, long competitionId) {
        return leagueTableNoteRepository.findByCompetitionIdAndSeason(competitionId, season).stream()
                .map(leagueTableNoteConverter::convert)
                .collect(toList());
    }

    @Override
    public void editLeagueTableNote(Long id, Long clubId, int points, String reason) {
        LeagueTableNoteEntity item = leagueTableNoteRepository.findById(id).orElseThrow(NotFoundException::new);
        ClubEntity club = clubService.getClubEntity(clubId);
        item.setClub(club);
        item.setPointsDeducted(points);
        item.setDeductionReason(reason);
        leagueTableNoteRepository.save(item);
    }

    @Override
    public void removeLeagueNote(Long id) {
        leagueTableNoteRepository.deleteById(id);
    }
}
