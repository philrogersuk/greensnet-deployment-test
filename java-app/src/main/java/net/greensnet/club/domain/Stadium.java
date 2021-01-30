package net.greensnet.club.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Stadium {
    Long id;
    String colloquialName;
    String actualName;
    String address;
    String postcode;
    Directions directions;
}
