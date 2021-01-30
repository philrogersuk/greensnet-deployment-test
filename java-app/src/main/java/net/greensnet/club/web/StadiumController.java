package net.greensnet.club.web;

import net.greensnet.club.dao.StadiumEntity;
import net.greensnet.club.service.StadiumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class StadiumController {

    private static final String STADIUM_LIST_PAGE = "th_admin/stadiums";

    private static final String STADIA = "stadia";

    private final StadiumService stadiumService;

    @Autowired
    public StadiumController(StadiumService stadiumService) {
        this.stadiumService = stadiumService;
    }

    @GetMapping("AdminStadium")
    public String showCrests(Model model) {
        List<StadiumEntity> stadia = stadiumService.getAll();

        model.addAttribute(STADIA, stadia);

        return STADIUM_LIST_PAGE;
    }


}
