package net.greensnet.club.event.producer;

import net.greensnet.club.domain.Club;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import static net.greensnet.util.events.UpdateType.*;

@Component
public class ClubUpdateProducer {
    private final ApplicationEventPublisher applicationEventPublisher;

    public ClubUpdateProducer(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishUpdate(Club club) {
        ClubUpdateEvent customSpringEvent = new ClubUpdateEvent(this, club, club.getId(), UPDATE);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }

    public void publishCreate(Club club) {
        ClubUpdateEvent customSpringEvent = new ClubUpdateEvent(this, club, club.getId(), CREATE);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }

    public void publishDelete(Club club) {
        ClubUpdateEvent customSpringEvent = new ClubUpdateEvent(this, null, club.getId(), DELETE);
        applicationEventPublisher.publishEvent(customSpringEvent);
    }

}
