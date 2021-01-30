package net.greensnet.competition.league.event.listener;

import net.greensnet.club.event.producer.ClubUpdateEvent;
import net.greensnet.competition.league.LeagueEntryService;
import net.greensnet.competition.league.LeagueFixtureService;
import net.greensnet.competition.league.LeagueTableNoteService;
import net.greensnet.util.events.UpdateType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class LeagueClubUpdateListener {

    private final LeagueEntryService leagueEntryService;
    private final LeagueFixtureService leagueFixtureService;
    private final LeagueTableNoteService leagueTableNoteService;

    public LeagueClubUpdateListener(LeagueEntryService leagueEntryService,
                                    LeagueFixtureService leagueFixtureService,
                                    LeagueTableNoteService leagueTableNoteService) {
        this.leagueEntryService = leagueEntryService;
        this.leagueFixtureService = leagueFixtureService;
        this.leagueTableNoteService = leagueTableNoteService;
    }

    @EventListener
    public void onApplicationEvent(ClubUpdateEvent event) {
        if (event.getType() != UpdateType.UPDATE) {
            return;
        }

        leagueEntryService.updateClubNames(event.getEntity());
//        leagueFixtureService.updateClubNames(event.getEntity());
//        leagueTableNoteService.updateClubNames(event.getEntity());
    }

}
