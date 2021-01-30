package net.greensnet.club.event.producer;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestClubListenerConfig {

    @Bean
    public ClubUpdateListenerStub clubUpdateListener() {
        return new ClubUpdateListenerStub();
    }
}
