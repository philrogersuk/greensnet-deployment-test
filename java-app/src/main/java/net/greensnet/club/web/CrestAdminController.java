package net.greensnet.club.web;

import net.greensnet.club.dao.ClubEntity;
import net.greensnet.club.dao.CrestEntity;
import net.greensnet.club.domain.Club;
import net.greensnet.club.service.ClubService;
import net.greensnet.club.service.CrestService;
import net.greensnet.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
public class CrestAdminController {

    private static final String CREST_LIST_PAGE = "th_admin/crest";

    private static final String CLUB = "club";
    private static final String CRESTS = "crests";

    private final ClubService clubService;
    private final CrestService crestService;

    @Autowired
    public CrestAdminController(ClubService clubService,
                                CrestService crestService) {
        this.clubService = clubService;
        this.crestService = crestService;
    }

    @GetMapping("AdminCrest/{clubId}")
    public String showCrests(Model model,
                             @PathVariable("clubId") long clubId) {
        Club club = clubService.getClub(clubId);
        if (null == club) {
            throw new NotFoundException();
        }

        List<CrestEntity> crests = crestService.getByClub(clubId);

        model.addAttribute(CLUB, club);
        model.addAttribute(CRESTS, crests);

        return CREST_LIST_PAGE;
    }

    @PostMapping("AdminCrest/{clubId}")
    public String addCrest(Model model,
                           @PathVariable("clubId") long clubId,
                           @RequestParam(value="firstSeason", defaultValue="-1") int firstSeason,
                           @RequestParam(value="lastSeason", defaultValue="-1") int lastSeason,
                           @RequestParam(value="crest") MultipartFile file) {
        ClubEntity club = clubService.getClubEntity(clubId);

        crestService.createCrest(file, club, firstSeason, lastSeason);

        return showCrests(model, clubId);
    }

}
