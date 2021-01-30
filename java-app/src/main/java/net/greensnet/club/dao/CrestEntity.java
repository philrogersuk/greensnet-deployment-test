package net.greensnet.club.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Crest")
public class CrestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private ClubEntity club;
    private String awsUrl;
    private int firstSeason;
    private int lastSeason;

    public boolean validForSeason(int season) {
        return (firstSeason <= season || firstSeason == -1) &&
                (lastSeason >= season || lastSeason == -1);
    }
}
