package net.greensnet.club.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Directions {
    String car;
    String train;
    String bus;
    String tube;
}
