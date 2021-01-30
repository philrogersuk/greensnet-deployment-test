package net.greensnet.dao;

import net.greensnet.domain.TeamFixture;
import net.greensnet.domain.TeamType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamFixtureRepository extends CrudRepository<TeamFixture, Long> {
    List<TeamFixture> findBySeasonAndTeamOrderByKickOffDesc(int season, TeamType team);
}
