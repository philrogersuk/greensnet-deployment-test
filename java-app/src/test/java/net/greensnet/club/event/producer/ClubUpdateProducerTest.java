package net.greensnet.club.event.producer;

import net.greensnet.club.domain.Club;
import net.greensnet.util.events.UpdateType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@Import(TestClubListenerConfig.class)
public class ClubUpdateProducerTest {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;
    @Autowired
    private ClubUpdateListenerStub listener;

    private ClubUpdateProducer producer;

    @BeforeEach
    public void setup() {
        producer = new ClubUpdateProducer(applicationEventPublisher);
    }

    @Test
    public void shouldProduceCreateMessage() {
        long id = 17364L;
        Club club = Club.builder()
                .id(id)
                .build();

        producer.publishCreate(club);

        ClubUpdateEvent event = listener.getLastEvent();
        assertThat(event.getEntityId()).isEqualTo(id);
        assertThat(event.getEntity()).isEqualTo(club);
        assertThat(event.getType()).isEqualTo(UpdateType.CREATE);
    }

    @Test
    public void shouldProduceDeleteMessage() {
        long id = 17364L;
        Club club = Club.builder()
                .id(id)
                .build();

        producer.publishDelete(club);

        ClubUpdateEvent event = listener.getLastEvent();
        assertThat(event.getEntityId()).isEqualTo(id);
        assertThat(event.getEntity()).isNull();
        assertThat(event.getType()).isEqualTo(UpdateType.DELETE);
    }

    @Test
    public void shouldProduceUpdateMessage() {
        long id = 17364L;
        Club club = Club.builder()
                .id(id)
                .build();

        producer.publishUpdate(club);

        ClubUpdateEvent event = listener.getLastEvent();
        assertThat(event.getEntityId()).isEqualTo(id);
        assertThat(event.getEntity()).isEqualTo(club);
        assertThat(event.getType()).isEqualTo(UpdateType.UPDATE);
    }
}
