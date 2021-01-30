package net.greensnet.fixture.event.listener;

import net.greensnet.club.event.producer.ClubUpdateEvent;
import net.greensnet.service.FixtureService;
import net.greensnet.util.events.UpdateType;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FixtureClubUpdateListener {

    private FixtureService fixtureService;

    public FixtureClubUpdateListener(FixtureService fixtureService) {
        this.fixtureService = fixtureService;
    }

    @EventListener
    public void onApplicationEvent(ClubUpdateEvent event) {
        if (event.getType() != UpdateType.UPDATE) {
            return;
        }

        fixtureService.updateOppositionTeamNames(event.getEntity());
    }
}
