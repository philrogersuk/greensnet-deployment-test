/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.news;

import com.google.common.collect.Lists;
import net.greensnet.domain.Fixture;
import net.greensnet.exceptions.NotFoundException;
import net.greensnet.news.dao.NewsItemEntity;
import net.greensnet.news.dao.NewsItemRepository;
import net.greensnet.news.domain.NewsItem;
import net.greensnet.news.domain.NewsItemEntityToDomainConverter;
import net.greensnet.service.FixtureService;
import net.greensnet.service.TwitterService;
import net.greensnet.service.hfctv.HfctvService;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.isBlank;

/**
 *
 * @author Phil
 */
@Service
public class NewsServiceImpl implements NewsService {

    private final NewsItemRepository repository;
    private final HfctvService hfctvService;
    private final FixtureService fixtureService;
    private final TwitterService twitterService;
    private final DateHelper dateHelper;
    private final NewsItemEntityToDomainConverter newsItemConverter;

    @Autowired
    public NewsServiceImpl(NewsItemRepository repository,
                           TwitterService twitterService,
                           HfctvService hfctvService,
                           FixtureService fixtureService,
                           DateHelper dateHelper,
                           NewsItemEntityToDomainConverter newsItemConverter) {
        this.repository = repository;
        this.twitterService = twitterService;
        this.hfctvService = hfctvService;
        this.fixtureService = fixtureService;
        this.dateHelper = dateHelper;
        this.newsItemConverter = newsItemConverter;
    }

    @Override
    public NewsItem deleteItemById(long id) {
        NewsItem item = getNewsItemById(id).orElseThrow(NotFoundException::new);

        repository.deleteById(id);

        return item;
    }

    @Override
    public List<NewsItem> getByMonth(int month, int year, boolean includeFutureDated) {
        LocalDateTime startDate = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, month, 1, 23, 59, 59).with(TemporalAdjusters.lastDayOfMonth());
        LocalDateTime now = dateHelper.getLocalDateTimeOfNow();

        if (!includeFutureDated && now.isBefore(endDate)) {
            return repository.findAllByTimeOfReleaseBetweenOrderByTimeOfReleaseDesc(startDate, now).stream()
                    .map(newsItemConverter::convert)
                    .collect(toList());

        }
        return repository.findAllByTimeOfReleaseBetweenOrderByTimeOfReleaseDesc(startDate, endDate).stream()
                    .map(newsItemConverter::convert)
                    .collect(toList());
    }

    @Override
    public Optional<LocalDateTime> getTimestampOfOldestItem() {
        Optional<NewsItemEntity> item = repository.findFirst1ByOrderByTimeOfReleaseAsc();
        return item.map(NewsItemEntity::getTimeOfRelease);
    }

    @Override
    public List<NewsItem> getLatest(int numToGet, boolean getFutureDated) {
        if (getFutureDated) {
            return repository.findFirst30ByOrderByTimeOfReleaseDesc().stream()
                    .map(newsItemConverter::convert)
                    .collect(toList());
        } else {
            LocalDateTime now = dateHelper.getLocalDateTimeOfNow();
            return repository.findFirst30ByTimeOfReleaseLessThanOrderByTimeOfReleaseDesc(now).stream()
                    .map(newsItemConverter::convert)
                    .collect(toList());
        }
    }

    @Override
    public NewsItem createNewsItem(String headline, String story, LocalDateTime timeStamp, String youtubeId, Long fixtureId) {
        LocalDateTime timeOfRelease = isNull(timeStamp) ?
                dateHelper.getLocalDateTimeOfNow() :
                timeStamp;
        NewsItemEntity item = NewsItemEntity.builder().item(story)
                .author(null)
                .headline(headline)
                .timeOfRelease(timeOfRelease)
                .id(null)
                .build();

        item = repository.save(item);

        if (!isBlank(youtubeId)) {
            createYoutubeVideo(timeStamp, youtubeId, fixtureId, item);
        }

        NewsItem newsItem = newsItemConverter.convert(item);

        if (isNull(timeStamp)) {
            tweetNewsItem(newsItem);
        }
        return newsItem;
    }

    private void tweetNewsItem(NewsItem item) {
        String url = "http://www.hendonfc.net/News/" + item.getId() + "/" + item.getHeadlineAsEncodedString();
        String content = "News: " + item.getHeadline();
        twitterService.tweet(content, url);
    }

    private void createYoutubeVideo(LocalDateTime timeStamp, String youtubeId, Long fixtureId, NewsItemEntity item) {
        int currentSeason = dateHelper.getCurrentSeason();

        Fixture fixture = nonNull(fixtureId) ?
                fixtureService.getFixtureById(fixtureId, false, false):
                null;

        hfctvService.createVideo(youtubeId, timeStamp, currentSeason, item, fixture, Lists.newArrayList());
    }

    @Override
    public NewsItem editNewsItem(long id, String headline, String story, LocalDateTime timeStamp, String youtubeId, Long fixtureId) {
        NewsItemEntity item = repository.findById(id).orElseThrow(NotFoundException::new);
        item.setHeadline(headline);
        item.setItem(story);
        if (nonNull(timeStamp)) {
            item.setTimeOfRelease(timeStamp);
        }
        repository.save(item);

        if (!isBlank(youtubeId)) {
            int currentSeason = dateHelper.getCurrentSeason();

            Fixture fixture = null;

            if (nonNull(fixtureId)) {
                fixture = fixtureService.getFixtureById(fixtureId, false, false);
            }

            hfctvService.editVideoByNewsItemId(youtubeId, item.getTimeOfRelease(), currentSeason, item, fixture, Lists.newArrayList());
        } else {
            hfctvService.removeVideoByNewsItemId(item.getId());
        }

        return newsItemConverter.convert(item);
    }

    @Override
    public Optional<NewsItem> getNewsItemById(long id) {
        return repository.findById(id)
                .map(newsItemConverter::convert);
    }

    @Override
    public Optional<NewsItem> getNextNewsItem(LocalDateTime timestamp) {
        return repository.findFirst1ByTimeOfReleaseGreaterThanOrderByTimeOfReleaseAsc(timestamp)
                .map(newsItemConverter::convert);
    }

    @Override
    public Optional<NewsItem> getPreviousNewsItem(LocalDateTime timestamp) {
        return repository.findFirst1ByTimeOfReleaseLessThanOrderByTimeOfReleaseDesc(timestamp)
                .map(newsItemConverter::convert);
    }
}
