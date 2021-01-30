package net.greensnet.club.domain;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class Home {

    Long id;
    Club club;
    Stadium stadium;
    LocalDateTime startDate;
    LocalDateTime endDate;
}
