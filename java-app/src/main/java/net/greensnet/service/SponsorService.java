/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import net.greensnet.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SponsorService {

    List<Fixture> getMatchballSponsors();
    List<Fixture> getMascots();
    List<Fixture> getMatchSponsors();
    List<Fixture> getProgrammeSponsors();
    List<StaffMember> getPlayerSponsors();

    boolean createMainSponsor(MultipartFile multipartFile,
                              String sponsorName,
                              String website,
                              String altText,
                              boolean active);
    boolean editMainSponsor(Long id,
                            String sponsorName,
                            String website,
                            String altText);
    boolean updateImage(Long id,
                        MultipartFile multipartFile);
    List<Sponsor> getAllMainSponsors();
    List<Sponsor> getAllActiveSponsors();

    Sponsor getSponsor(long id);
    boolean deactivateSponsor(long id);
    boolean activateSponsor(long id);


    boolean deleteSeatById(Long id);

    boolean createSeat(String sponsor, SeatType seatType, int row, int column);

    boolean editSeat(Long id, String sponsor, SeatType seatType);

    Seat getSeatById(Long id);

    SeatType getSeatTypeById(Long id);

    Seat[][] getSeatsInGrid();

    List<SeatType> getAllSeatTypes();
}
