package net.greensnet.service.hfctv;

import net.greensnet.domain.Fixture;
import net.greensnet.domain.hfctv.YoutubeVideo;
import net.greensnet.news.dao.NewsItemEntity;
import net.greensnet.news.domain.NewsItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface HfctvService {

    boolean createVideo(String youtubeId,
                        LocalDateTime timeStamp,
                        int season,
                        Fixture linkedFixture,
                        List<String> tags);

    boolean createVideo(String youtubeId,
                        LocalDateTime timeStamp,
                        int season,
                        NewsItemEntity linkedNewsItem,
                        Fixture linkedFixture,
                        List<String> tags);

    List<YoutubeVideo> getByFixture(Fixture fixture);

    List<YoutubeVideo> getLatest(int numberToGet);

    Optional<YoutubeVideo> getLatest();

    Optional<YoutubeVideo> getVideo(long id);

    List<YoutubeVideo> getByTag(String tag);

    void editVideoByNewsItemId(String youtubeId, LocalDateTime timeStamp, int currentSeason, NewsItemEntity item, Fixture fixture, List<String> tags);

    void removeVideoByNewsItemId(Long newsId);

    Optional<YoutubeVideo> getByNewsItem(NewsItem newsItem);
}
