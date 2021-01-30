/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.service;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import net.greensnet.dao.SeatRepository;
import net.greensnet.dao.SeatTypeRepository;
import net.greensnet.dao.SponsorRepository;
import net.greensnet.domain.*;
import net.greensnet.exceptions.NotFoundException;
import net.greensnet.service.files.FileService;
import net.greensnet.service.files.ImageSize;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static net.greensnet.service.files.ImageSize.Type.HEIGHT;
import static net.greensnet.service.files.ImageSize.Type.ORIGINAL;

/**
 * @author Phil
 */
@Service
public class SponsorServiceImpl implements SponsorService {

    private final StaffService staffService;
    private final FixtureService fixtureService;
    private final SeatRepository seatRepository;
    private final SeatTypeRepository typeRepository;
    private final SponsorRepository sponsorRepository;
    private final FileService fileService;
    private final DateHelper dateHelper;

    @Autowired
    public SponsorServiceImpl(StaffService staffService,
                              FixtureService fixtureService,
                              SeatRepository seatRepository,
                              SeatTypeRepository typeRepository,
                              SponsorRepository sponsorRepository,
                              FileService fileService,
                              DateHelper dateHelper) {
        this.staffService = staffService;
        this.fixtureService = fixtureService;
        this.seatRepository = seatRepository;
        this.typeRepository = typeRepository;
        this.sponsorRepository = sponsorRepository;
        this.fileService = fileService;
        this.dateHelper = dateHelper;
    }

    @Override
    public List<Fixture> getMatchballSponsors() {
        return fixtureService.getHomeFixturesBySeason(dateHelper.getCurrentSeason());
    }

    @Override
    public List<Fixture> getMascots() {
        return fixtureService.getHomeFixturesBySeason(dateHelper.getCurrentSeason());
    }

    @Override
    public List<Fixture> getMatchSponsors() {
        return fixtureService.getHomeFixturesBySeason(dateHelper.getCurrentSeason());
    }

    @Override
    public List<Fixture> getProgrammeSponsors() {
        return fixtureService.getHomeFixturesBySeason(dateHelper.getCurrentSeason());
    }

    @Override
    public List<StaffMember> getPlayerSponsors() {
        return staffService.getCurrentSquadList();
    }

    @Override
    public boolean createMainSponsor(MultipartFile fileStream, String sponsorName, String website, String altText, boolean active) {
        Sponsor sponsor = Sponsor.builder().name(sponsorName)
                .websiteUrl(website)
                .altText(altText)
                .active(active)
                .build();
        sponsorRepository.save(sponsor);

        if (null != fileStream && !fileStream.isEmpty()) {
            handleFileUpload(fileStream, sponsor);
        }

        return true;
    }

    @Override
    public boolean editMainSponsor(Long id, String sponsorName, String website, String altText) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find sponsor with ID=" + id));

        sponsor.setName(sponsorName);
        sponsor.setWebsiteUrl(website);
        sponsor.setAltText(altText);

        return true;
    }

    @Override
    public List<Sponsor> getAllMainSponsors() {
        return Lists.newArrayList(sponsorRepository.findAll());
    }

    @Override
    public List<Sponsor> getAllActiveSponsors() {
        return sponsorRepository.findAllByActiveEquals(true);
    }

    private void handleFileUpload(MultipartFile fileStream, Sponsor sponsor) {
        String filename = "sponsors/" + sponsor.getId() + "-" + sponsor.getName() + "." + Files.getFileExtension(fileStream.getOriginalFilename());
        List<ImageSize> sizes = Lists.newArrayList(new ImageSize(HEIGHT, 40), new ImageSize(ORIGINAL));

        fileService.uploadImage(fileStream, filename, sizes);
        sponsor.setAwsUrl(fileService.getUrl(new ImageSize(HEIGHT, 40).renameFile(filename)).orElse(null));
        sponsorRepository.save(sponsor);
    }


    @Override
    public Sponsor getSponsor(long id) {
        return sponsorRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public boolean deactivateSponsor(long id) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find item with ID=" + id));

        sponsor.setActive(false);
        sponsorRepository.save(sponsor);

        return true;
    }

    @Override
    public boolean activateSponsor(long id) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find sponsor with ID=" + id));

        sponsor.setActive(true);
        sponsorRepository.save(sponsor);
        return true;
    }

    @Override
    public boolean updateImage(Long id,
                               MultipartFile multipartFile) {
        Sponsor sponsor = sponsorRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Could not find sponsor with ID=" + id));

        if (null != multipartFile && !multipartFile.isEmpty()) {
            handleFileUpload(multipartFile, sponsor);
        }

        return true;
    }

    @Override
    public boolean deleteSeatById(Long id) {
        seatRepository.delete(getSeatById(id));
        return true;
    }

    @Override
    public boolean createSeat(String sponsor, SeatType seatType, int row, int column) {
        Seat seat = new Seat();
        populateSeat(sponsor, seatType, row, column, seat);
        seatRepository.save(seat);
        return true;
    }

    @Override
    public boolean editSeat(Long id, String sponsor, SeatType seatType) {
        Optional<Seat> optional = seatRepository.findById(id);
        if (!optional.isPresent()) {
            return false;
        }
        Seat seat = optional.get();
        populateSeat(sponsor, seatType, seat.getRow(), seat.getColumn(), seat);
        seatRepository.save(seat);
        return true;
    }

    @Override
    public SeatType getSeatTypeById(Long id) {
        Optional<SeatType> optional = typeRepository.findById(id);
        return optional.orElse(null);
    }

    private static void populateSeat(String sponsor, SeatType seatType, int row, int column, Seat seat) {
        seat.setSponsor(sponsor);
        seat.setType(seatType);
        seat.setRow(row);
        seat.setColumn(column);
    }

    @Override
    public Seat getSeatById(Long id) {
        Optional<Seat> optional = seatRepository.findById(id);
        return optional.orElse(null);
    }

    @Override
    public Seat[][] getSeatsInGrid() {
        List<Seat> seats = getAllSeats();
        if (seats.isEmpty()) {
            return new Seat[0][0];
        }
        int maxRow = findMaxRow(seats);
        int maxColumn = findMaxColumn(seats);

        Seat[][] grid = new Seat[maxRow + 1][maxColumn + 1];
        for (Seat seat : seats) {
            grid[seat.getRow()][seat.getColumn()] = seat;
        }
        return grid;
    }

    private static int findMaxRow(List<Seat> seats) {
        int max = 0;
        for (Seat seat : seats) {
            if (seat.getRow() > max) {
                max = seat.getRow();
            }
        }
        return max;
    }

    private static int findMaxColumn(List<Seat> seats) {
        int max = 0;
        for (Seat seat : seats) {
            if (seat.getColumn() > max) {
                max = seat.getColumn();
            }
        }
        return max;
    }

    private List<Seat> getAllSeats() {
        return Lists.newArrayList(seatRepository.findAll());
    }

    public List<SeatType> getAllSeatTypes() {
        return Lists.newArrayList(typeRepository.findAll());
    }
}
