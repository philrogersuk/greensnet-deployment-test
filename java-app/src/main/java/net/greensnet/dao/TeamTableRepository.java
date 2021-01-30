package net.greensnet.dao;

import net.greensnet.domain.TeamTable;
import net.greensnet.domain.TeamType;
import org.springframework.data.repository.CrudRepository;

public interface TeamTableRepository extends CrudRepository<TeamTable, Long> {
    TeamTable findBySeasonAndTeam(int season, TeamType team);
}
