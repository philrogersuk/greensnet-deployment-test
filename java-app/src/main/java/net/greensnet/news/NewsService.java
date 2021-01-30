/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.news;

import net.greensnet.news.domain.NewsItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NewsService {

    NewsItem deleteItemById(long id);

    List<NewsItem> getByMonth(int month, int year, boolean includeFutureDated);

    Optional<LocalDateTime> getTimestampOfOldestItem();

    List<NewsItem> getLatest(int numToGet, boolean getFutureDated);

    NewsItem createNewsItem(String headline, String story, LocalDateTime timeStamp, String youtubeId, Long fixtureId);

    NewsItem editNewsItem(long id, String headline, String story, LocalDateTime timeStamp, String youtubeId, Long fixtureId);

    Optional<NewsItem> getNewsItemById(long id);

    Optional<NewsItem> getNextNewsItem(LocalDateTime timestamp);

    Optional<NewsItem> getPreviousNewsItem(LocalDateTime timestamp);
}
