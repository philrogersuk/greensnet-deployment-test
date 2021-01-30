/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.club.web;

import net.greensnet.club.domain.Club;
import net.greensnet.club.service.ClubService;
import net.greensnet.util.PrimitiveUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ClubAdminController {

    private final ClubService clubService;

    private static final String PAGE_TITLE = "page-title";

    @Autowired
    public ClubAdminController(ClubService clubService) {
        this.clubService = clubService;
    }

    @RequestMapping("AdminClubs")
    public String handleRequest(Model model, @RequestParam(value = "method", required = false) String method,
                                @RequestParam(value = "letter", required = false) Character letter,
                                @RequestParam(value = "id", required = false) Long clubId,
                                @RequestParam(value = "name", required = false) String fullName,
                                @RequestParam(value = "shortname", required = false) String shortName,
                                @RequestParam(value = "tla", required = false) String tla,
                                @RequestParam(value = "yearFounded", required = false) String year,
                                @RequestParam(value = "directionsByCar", required = false) String directionsByCar,
                                @RequestParam(value = "directionsByTrain", required = false) String directionsByTrain,
                                @RequestParam(value = "directionsByTube", required = false) String directionsByTube,
                                @RequestParam(value = "directionsByBus", required = false) String directionsByBus,
                                @RequestParam(value = "postcode", required = false) String postcode) {
        if (null != method) {
            switch (method) {
                case "list":
                    List<Club> clubs = clubService.getClubsByInitial(letter);
                    model.addAttribute("club_list", clubs);
                    break;
                case "edit":
                    Club club = clubService.getClub(clubId);
                    model.addAttribute("club_info", club);
                    break;
                case "add":
                case "editSubmit":
                    int yearFounded = PrimitiveUtils.parsePositiveIntSafe(year);
                    if ("add".equals(method)) {
                        Club clubToCreate = Club.builder()
                                .internalName(fullName)
                                .shortName(shortName)
                                .tla(tla)
                                .yearFounded(yearFounded)
                                .directionsByCar(directionsByCar)
                                .directionsByTrain(directionsByTrain)
                                .directionsByTube(directionsByTube)
                                .directionsByBus(directionsByBus)
                                .postcode(postcode)
                                .build();

                        clubService.createClub(clubToCreate);
                    } else {
                        Club newClubDetails = Club.builder()
                                .internalName(fullName)
                                .shortName(shortName)
                                .tla(tla)
                                .yearFounded(yearFounded)
                                .directionsByCar(directionsByCar)
                                .directionsByTrain(directionsByTrain)
                                .directionsByTube(directionsByTube)
                                .directionsByBus(directionsByBus)
                                .postcode(postcode)
                                .build();

                        clubService.editClub(clubId, newClubDetails);
                    }
                    break;
            }
        }
        model.addAttribute(PAGE_TITLE, "Club");

        return "th_admin/club";
    }
}
