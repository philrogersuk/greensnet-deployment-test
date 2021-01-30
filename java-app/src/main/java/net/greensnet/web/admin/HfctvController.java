package net.greensnet.web.admin;

import net.greensnet.domain.Fixture;
import net.greensnet.domain.hfctv.YoutubeVideo;
import net.greensnet.exceptions.NotFoundException;
import net.greensnet.service.FixtureService;
import net.greensnet.service.hfctv.HfctvService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller("adminHfctvController")
public class HfctvController {

    private static final String HFCTV_PAGE = "th_admin/hfctv";

    private static final String FIXTURE = "fixture";
    private static final String VIDEOS = "videos";

    private final FixtureService fixtureService;
    private final HfctvService hfctvService;

    @Autowired
    public HfctvController(FixtureService fixtureService,
                           HfctvService hfctvService) {
        this.fixtureService = fixtureService;
        this.hfctvService = hfctvService;
    }

    @GetMapping("/AdminHfctv/Fixture/{fixtureId}")
    public String showHfctvVideosForFixture(Model model,
                                            @PathVariable("fixtureId") long fixtureId) {
        Fixture fixture = fixtureService.getFixtureById(fixtureId, false, false);
        if (null == fixture) {
            throw new NotFoundException();
        }

        List<YoutubeVideo> videos = hfctvService.getByFixture(fixture);

        model.addAttribute(FIXTURE, fixture);
        model.addAttribute(VIDEOS, videos);

        return HFCTV_PAGE;
    }

    @PostMapping("/AdminHfctv/Fixture/{fixtureId}")
    public String createHfctvVideoForFixture(Model model,
                                             @PathVariable("fixtureId") long fixtureId,
                                             @RequestParam("timestamp") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime timestamp,
                                             @RequestParam("youtubeId") String youtubeId,
                                             @RequestParam("tags") List<String> tags) {
        Fixture fixture = fixtureService.getFixtureById(fixtureId, false, false);
        if (null == fixture) {
            throw new NotFoundException();
        }

        hfctvService.createVideo(youtubeId, timestamp, fixture.getSeason(), fixture, tags);

        return showHfctvVideosForFixture(model, fixtureId);
    }
}