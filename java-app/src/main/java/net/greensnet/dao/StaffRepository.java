/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.dao;

import net.greensnet.domain.StaffMember;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface StaffRepository extends CrudRepository<StaffMember, Long> {
    List<StaffMember> findAllByOrderByLastNameAsc();

    @Query("SELECT DISTINCT item FROM StaffMember item "
            + "LEFT JOIN FETCH item.timeAtClub AS tac LEFT JOIN FETCH tac.staffRole "
            + "WHERE item.isAtClub = true "
            + "ORDER BY item.lastName ASC, item.firstName ASC")
    List<StaffMember> getCurrentStaff();

    List<StaffMember> findDistinctByLastNameStartsWithOrderByLastNameAscFirstNameAsc(char character);

    @Query("SELECT DISTINCT item FROM StaffMember item "
            + "LEFT JOIN FETCH item.oldNames LEFT JOIN FETCH item.timeAtClub AS time "
            + "WHERE time.startSeason <= ?1 "
            + "AND "
            + "(time.endSeason = -1 OR time.endSeason >= ?1) "
            + "ORDER BY item.lastName ASC, item.firstName ASC")
    List<StaffMember> findAllStaffForSeason(int season);

    @Query("SELECT distinct item FROM StaffMember item "
            + "LEFT JOIN FETCH item.oldNames LEFT JOIN FETCH item.timeAtClub AS time LEFT JOIN FETCH "
            + "time.staffRole AS role "
            + "WHERE (time.startSeason <= ?1 AND (time.endSeason = -1 OR time.endSeason >= ?1) "
            + "AND role.name = 'Player') ORDER BY item.lastName ASC, item.firstName ASC")
    List<StaffMember> findAllPlayersForSeason(int season);

    @Query("SELECT distinct item FROM StaffMember item "
            + "LEFT JOIN FETCH item.oldNames LEFT JOIN FETCH item.timeAtClub AS time "
            + "WHERE (time.startSeason <= ?1 AND (time.endSeason = -1 OR time.endSeason >= ?1) "
            + "AND time.trialOnly = false) ORDER BY item.lastName ASC, item.firstName ASC")
    List<StaffMember> findAllStaffForSeasonExcludingTrialists(int season);

    @Query("SELECT distinct item FROM StaffMember item "
            + "LEFT JOIN FETCH item.oldNames LEFT JOIN FETCH item.timeAtClub AS time INNER JOIN FETCH "
            + "time.staffRole AS role "
            + "WHERE (time.startSeason <= ?1 AND (time.endSeason = -1 OR time.endSeason >= ?1) "
            + "AND time.trialOnly = false AND role.name = 'Player') "
            + "ORDER BY item.lastName ASC, item.firstName ASC")
    List<StaffMember> findAllPlayersForSeasonExcludingTrialists(int season);

}
