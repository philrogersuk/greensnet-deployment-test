package net.greensnet.club.dao;

import lombok.Builder;
import lombok.Value;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Value
@Builder
@Entity
@Table(name = "club_home")
public class HomeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private ClubEntity club;
    @OneToOne
    private StadiumEntity stadium;
    private LocalDate startDate;
    private LocalDate endDate;
}
