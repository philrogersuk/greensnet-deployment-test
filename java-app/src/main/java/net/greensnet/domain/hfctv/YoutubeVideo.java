package net.greensnet.domain.hfctv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.greensnet.domain.Fixture;
import net.greensnet.news.dao.NewsItemEntity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class YoutubeVideo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String youtubeId;
    private LocalDateTime timeStamp;
    private int season;
    @ManyToOne
    private Fixture linkedFixture;
    @OneToOne
    private NewsItemEntity linkedNewsItem;
    @OneToMany(mappedBy="id")
    private List<Tag> tags;
}
