package net.greensnet.dao;

import net.greensnet.domain.TeamEntry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TeamEntryRepository extends CrudRepository<TeamEntry, Long> {
    List<TeamEntry> findBySeason(int season);

    @Query("SELECT item FROM TeamEntry item INNER JOIN FETCH item.team AS team ORDER BY item.season DESC, team.id ASC")
    List<TeamEntry> findAllSortedBySeasonAndName();
}
