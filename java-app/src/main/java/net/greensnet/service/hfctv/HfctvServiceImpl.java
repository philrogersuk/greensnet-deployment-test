package net.greensnet.service.hfctv;

import com.google.common.collect.Lists;
import net.greensnet.dao.hfctv.TagRepository;
import net.greensnet.dao.hfctv.YoutubeVideoRepository;
import net.greensnet.domain.Fixture;
import net.greensnet.domain.hfctv.Tag;
import net.greensnet.domain.hfctv.YoutubeVideo;
import net.greensnet.news.dao.NewsItemEntity;
import net.greensnet.news.domain.NewsItem;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class HfctvServiceImpl implements HfctvService {

    private final YoutubeVideoRepository youtubeVideoRepository;
    private final TagRepository tagRepository;

    public HfctvServiceImpl(YoutubeVideoRepository youtubeVideoRepository,
                            TagRepository tagRepository) {
        this.youtubeVideoRepository = youtubeVideoRepository;
        this.tagRepository = tagRepository;
    }

    @Override
    public boolean createVideo(String youtubeId, LocalDateTime timeStamp, int season, Fixture linkedFixture, List<String> tagsAsString) {
        List<Tag> tags = createTags(tagsAsString);

        YoutubeVideo video = YoutubeVideo.builder()
                .youtubeId(youtubeId)
                .timeStamp(timeStamp)
                .season(season)
                .linkedFixture(linkedFixture)
                .tags(tags)
                .build();
        youtubeVideoRepository.save(video);

        return true;
    }

    @Override
    public boolean createVideo(String youtubeId, LocalDateTime timeStamp, int season, NewsItemEntity linkedNewsItem, Fixture linkedFixture, List<String> tagsAsString) {
        List<Tag> tags = createTags(tagsAsString);

        YoutubeVideo video = YoutubeVideo.builder()
                .youtubeId(youtubeId)
                .timeStamp(timeStamp)
                .season(season)
                .linkedNewsItem(linkedNewsItem)
                .linkedFixture(linkedFixture)
                .tags(tags)
                .build();
        youtubeVideoRepository.save(video);

        return true;
    }

    @Override
    public List<YoutubeVideo> getByFixture(Fixture fixture) {
        return youtubeVideoRepository.findAllByLinkedFixtureOrderByLinkedFixtureDesc(fixture);
    }

    @Override
    public Optional<YoutubeVideo> getByNewsItem(NewsItem newsItem) {
        return youtubeVideoRepository.findByLinkedNewsItem_Id(newsItem.getId());
    }

    @Override
    public List<YoutubeVideo> getLatest(int numToGet) {
        return youtubeVideoRepository.findFirst20ByOrderByTimeStampDesc();
    }

    @Override
    public Optional<YoutubeVideo> getLatest() {
        return youtubeVideoRepository.findFirst1ByOrderByTimeStampDesc();
    }

    @Override
    public Optional<YoutubeVideo> getVideo(long id) {
        return youtubeVideoRepository.findById(id);
    }

    @Override
    public List<YoutubeVideo> getByTag(String tag) {
        return Lists.newArrayList();
    }

    private List<Tag> createTags(List<String> tagsAsString) {
        if (Objects.isNull(tagsAsString)) {
            return Lists.newArrayList();
        }
        List<Tag> tags = tagsAsString.stream()
                .map(tag -> Tag.builder().tag(tag).build())
                .collect(Collectors.toList());
        tagRepository.saveAll(tags);
        return tags;
    }


    @Override
    public void editVideoByNewsItemId(String youtubeId, LocalDateTime timeStamp, int season, NewsItemEntity linkedNewsItem, Fixture linkedFixture, List<String> tagsAsStrings) {

        Optional<YoutubeVideo> video = youtubeVideoRepository.findByLinkedNewsItem_Id(linkedNewsItem.getId());

        if (video.isPresent()) {
            List<Tag> tags = createTags(tagsAsStrings);

            video.get().setLinkedFixture(linkedFixture);
            video.get().setLinkedNewsItem(linkedNewsItem);
            video.get().setSeason(season);
            video.get().setTimeStamp(timeStamp);
            video.get().setYoutubeId(youtubeId);
            video.get().setTags(tags);

            youtubeVideoRepository.save(video.get());

        } else {
            createVideo(youtubeId, timeStamp, season, linkedNewsItem, linkedFixture, tagsAsStrings);
        }
    }

    @Override
    public void removeVideoByNewsItemId(Long newsId) {
        youtubeVideoRepository.deleteByLinkedNewsItem_Id(newsId);
    }
}
