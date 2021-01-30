package net.greensnet.club.event.producer;

import net.greensnet.club.domain.Club;
import net.greensnet.util.events.EntityUpdateEvent;
import net.greensnet.util.events.UpdateType;

public class ClubUpdateEvent<T extends Club> extends EntityUpdateEvent<T> {

    public ClubUpdateEvent(Object source,
                           T club,
                           long id,
                           UpdateType updateType) {
        super(source, club, id, updateType);
    }

    @Override
    public T getEntity() {
        return super.getEntity();
    }

}
