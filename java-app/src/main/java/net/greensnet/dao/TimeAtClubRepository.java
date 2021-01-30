/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.dao;

import net.greensnet.domain.StaffMember;
import net.greensnet.domain.TimeAtClub;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TimeAtClubRepository extends CrudRepository<TimeAtClub, Long> {
    List<TimeAtClub> findByStaffMember(StaffMember staffMember);

    @Query("SELECT item FROM TimeAtClub item "
            + "LEFT JOIN FETCH item.staffRole AS role "
            + "WHERE item.staffMember = ?1 "
            + "AND role.name != 'Player' "
            + "ORDER BY item.startSeason DESC, item.startDate DESC")
    List<TimeAtClub> findStaffPeriodsByStaffMember(StaffMember staffMember);
}
