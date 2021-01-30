package net.greensnet.club.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
public class DirectionsEntity {
    private String car;
    private String train;
    private String bus;
    private String tube;
}
