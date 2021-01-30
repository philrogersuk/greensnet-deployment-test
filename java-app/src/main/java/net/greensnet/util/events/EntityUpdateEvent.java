package net.greensnet.util.events;

import org.springframework.context.ApplicationEvent;

public abstract class EntityUpdateEvent<T> extends ApplicationEvent {

    private final T entity;
    private final long entityId;
    private final UpdateType type;

    public EntityUpdateEvent(Object source,
                             T entity,
                             long entityId,
                             UpdateType type) {
        super(source);
        this.entity = entity;
        this.entityId = entityId;
        this.type = type;
    }

    public T getEntity() {
        return entity;
    }

    public long getEntityId() {
        return entityId;
    }

    public UpdateType getType() {
        return type;
    }
}
