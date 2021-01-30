package net.greensnet.club.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Crest {

    Long id;
    Club club;
    String awsUrl;
    int firstSeason;
    int lastSeason;

    public boolean validForSeason(int season) {
        return (firstSeason <= season || firstSeason == -1) &&
                (lastSeason >= season || lastSeason == -1);
    }
}
