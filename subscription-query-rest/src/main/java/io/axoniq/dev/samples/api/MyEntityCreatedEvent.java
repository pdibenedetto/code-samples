package io.axoniq.dev.samples.api;

/**
 * @author Sara Pellegrini
 * @since
 */
public class MyEntityCreatedEvent {

    private final String entityId;

    public MyEntityCreatedEvent(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityId() {
        return entityId;
    }
}
