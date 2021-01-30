package net.greensnet.dao;

import net.greensnet.domain.TeamNews;
import net.greensnet.domain.TeamType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamNewsRepository extends CrudRepository<TeamNews, Long> {
    List<TeamNews> findBySeasonAndTeamOrderByTimeOfReleaseDesc(int season, TeamType team);
}
