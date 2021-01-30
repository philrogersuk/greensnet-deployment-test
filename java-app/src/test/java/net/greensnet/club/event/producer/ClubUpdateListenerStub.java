package net.greensnet.club.event.producer;


import net.greensnet.club.domain.Club;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ClubUpdateListenerStub {

    private ClubUpdateEvent<Club> lastEvent;

    @EventListener
    public void onMessage(ClubUpdateEvent event) {
        lastEvent = event;
    }

    public ClubUpdateEvent<Club> getLastEvent() {
        return lastEvent;
    }
}