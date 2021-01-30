/*
 * Copyright (c) 2019, Phil Rogers
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class NewsServiceImplTest {

    @Mock
    private NewsItemRepository repository;
    @Mock
    private TwitterService twitterService;
    @Mock
    private DateHelper dateHelper;
    @Mock
    private FixtureService fixtureService;
    @Mock
    private HfctvService hfctvService;
    private NewsItemEntityToDomainConverter converter;

    private NewsService newsService;

    @BeforeEach
    public void setup() {
        converter = new NewsItemEntityToDomainConverter();
        newsService = new NewsServiceImpl(repository,
                                        twitterService,
                                        hfctvService,
                                        fixtureService,
                                        dateHelper,
                                        converter);
    }

    @Test
    public void shouldSaveNewItemWithTweet() {
        LocalDateTime expectedTimestamp = LocalDateTime.now();
        NewsItemEntity expectedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(null)
                .timeOfRelease(expectedTimestamp)
                .build();
        NewsItemEntity returnedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(12L)
                .timeOfRelease(expectedTimestamp)
                .build();
        NewsItem expectedItem = NewsItem.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(12L)
                .timeOfRelease(expectedTimestamp)
                .build();

        when(dateHelper.getLocalDateTimeOfNow()).thenReturn(expectedTimestamp);
        when(repository.save(expectedEntity)).thenReturn(returnedEntity);

        assertThat(newsService.createNewsItem("headline", "item", null,  null, null)).isEqualTo(expectedItem);

        verify(twitterService).tweet("News: headline", "http://www.hendonfc.net/News/12/headline");
        verify(hfctvService, never()).createVideo(anyString(), any(LocalDateTime.class), anyInt(), any(NewsItemEntity.class), any(Fixture.class), anyList());
    }

    @Test
    public void shouldSaveNewItemWithoutTweet() {
        LocalDateTime timestamp = LocalDateTime.now();
        NewsItemEntity expectedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(null)
                .timeOfRelease(timestamp)
                .build();
        NewsItemEntity returnedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(12L)
                .timeOfRelease(timestamp)
                .build();
        NewsItem expectedItem = NewsItem.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(12L)
                .timeOfRelease(timestamp)
                .build();

        when(repository.save(expectedEntity)).thenReturn(returnedEntity);

        assertThat(newsService.createNewsItem("headline", "item", timestamp,  null, null)).isEqualTo(expectedItem);

        verify(twitterService, never()).tweet(anyString(), anyString());
        verify(hfctvService, never()).createVideo(anyString(), any(LocalDateTime.class), anyInt(), any(NewsItemEntity.class), any(Fixture.class), anyList());
    }

    @Test
    public void shouldSaveNewItemWithYoutubeVideoAndRelatedFixture() {
        LocalDateTime timestamp = LocalDateTime.now();
        NewsItemEntity expectedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(null)
                .timeOfRelease(timestamp)
                .build();
        NewsItemEntity returnedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(12L)
                .timeOfRelease(timestamp)
                .build();
        NewsItem expectedItem = NewsItem.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(12L)
                .timeOfRelease(timestamp)
                .build();
        Fixture relatedFixture = Fixture.builder()
                .id(123L)
                .build();

        when(repository.save(expectedEntity)).thenReturn(returnedEntity);
        when(fixtureService.getFixtureById(123L, false, false)).thenReturn(relatedFixture);
        when(dateHelper.getCurrentSeason()).thenReturn(2019);

        assertThat(newsService.createNewsItem("headline", "item", timestamp,  "ab2urb0Xe", 123L)).isEqualTo(expectedItem);

        verify(twitterService, never()).tweet(anyString(), anyString());
        verify(hfctvService).createVideo("ab2urb0Xe", timestamp, 2019, returnedEntity,  relatedFixture, Lists.newArrayList());
    }

    @Test
    public void shouldSaveNewItemWithYoutubeVideoButNoRelatedFixture() {
        LocalDateTime timestamp = LocalDateTime.now();
        NewsItemEntity expectedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(null)
                .timeOfRelease(timestamp)
                .build();
        NewsItemEntity returnedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(12L)
                .timeOfRelease(timestamp)
                .build();
        NewsItem expectedItem = NewsItem.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(12L)
                .timeOfRelease(timestamp)
                .build();
        Fixture relatedFixture = Fixture.builder()
                .id(123L)
                .build();

        when(repository.save(expectedEntity)).thenReturn(returnedEntity);
        when(fixtureService.getFixtureById(123L, false, false)).thenReturn(relatedFixture);
        when(dateHelper.getCurrentSeason()).thenReturn(2019);

        assertThat(newsService.createNewsItem("headline", "item", timestamp,  "ab2urb0Xe", null)).isEqualTo(expectedItem);

        verify(twitterService, never()).tweet(anyString(), anyString());
        verify(hfctvService).createVideo("ab2urb0Xe", timestamp, 2019, returnedEntity,  null, Lists.newArrayList());
    }

    @Test
    public void shouldRetrieveItemById() {
        LocalDateTime timestamp = LocalDateTime.now();
        NewsItemEntity returnedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(123L)
                .timeOfRelease(timestamp)
                .build();

        when(repository.findById(12L)).thenReturn(Optional.of(returnedEntity));

        NewsItem actual = newsService.getNewsItemById(12L).get();

        assertThat(actual.getHeadline()).isEqualTo("headline");
        assertThat(actual.getItem()).isEqualTo("item");
        assertThat(actual.getAuthor()).isEqualTo(null);
        assertThat(actual.getId()).isEqualTo(123L);
        assertThat(actual.getTimeOfRelease()).isEqualTo(timestamp);
    }

    @Test
    public void shouldReturnEmptyOptionalWhenCannotFindItemById() {
        Optional<NewsItem> actual = newsService.getNewsItemById(12L);

        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    public void shouldRetrieveNextItem() {
        LocalDateTime timestamp = LocalDateTime.now();
        NewsItemEntity returnedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(123L)
                .timeOfRelease(timestamp)
                .build();

        when(repository.findFirst1ByTimeOfReleaseGreaterThanOrderByTimeOfReleaseAsc(timestamp)).thenReturn(Optional.of(returnedEntity));

        NewsItem actual = newsService.getNextNewsItem(timestamp).get();

        assertThat(actual.getHeadline()).isEqualTo("headline");
        assertThat(actual.getItem()).isEqualTo("item");
        assertThat(actual.getAuthor()).isEqualTo(null);
        assertThat(actual.getId()).isEqualTo(123L);
        assertThat(actual.getTimeOfRelease()).isEqualTo(timestamp);
    }

    @Test
    public void shouldReturnEmptyOptionalWhenCannotFindNextItem() {
        Optional<NewsItem> actual = newsService.getNextNewsItem(LocalDateTime.now());

        assertThat(actual.isPresent()).isFalse();
    }
    @Test
    public void shouldRetrievePreviousItem() {
        LocalDateTime timestamp = LocalDateTime.now();
        NewsItemEntity returnedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(123L)
                .timeOfRelease(timestamp)
                .build();

        when(repository.findFirst1ByTimeOfReleaseLessThanOrderByTimeOfReleaseDesc(timestamp)).thenReturn(Optional.of(returnedEntity));

        NewsItem actual = newsService.getPreviousNewsItem(timestamp).get();

        assertThat(actual.getHeadline()).isEqualTo("headline");
        assertThat(actual.getItem()).isEqualTo("item");
        assertThat(actual.getAuthor()).isEqualTo(null);
        assertThat(actual.getId()).isEqualTo(123L);
        assertThat(actual.getTimeOfRelease()).isEqualTo(timestamp);
    }

    @Test
    public void shouldReturnEmptyOptionalWhenCannotFindPreiousItem() {
        Optional<NewsItem> actual = newsService.getPreviousNewsItem(LocalDateTime.now());

        assertThat(actual.isPresent()).isFalse();
    }


    @Test
    public void shouldNotAttemptToDeleteItemWhenNotFound() {
        when(repository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> newsService.deleteItemById(123L));
        verify(repository, never()).deleteById(123L);
    }

    @Test
    public void shouldDeleteItem() {
        LocalDateTime timestamp = LocalDateTime.now();
        NewsItemEntity returnedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(123L)
                .timeOfRelease(timestamp)
                .build();
        NewsItem expectedItem = NewsItem.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(123L)
                .timeOfRelease(timestamp)
                .build();


        when(repository.findById(123L)).thenReturn(Optional.of(returnedEntity));

        assertThat(newsService.deleteItemById(123L)).isEqualTo(expectedItem);

        verify(repository).deleteById(123L);
    }

    @Test
    public void shouldGetTimeStampOfOldestItem() {
        LocalDateTime expected = LocalDateTime.now();
        NewsItemEntity oldestItem = NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(123L)
                .timeOfRelease(expected)
                .build();

        when(repository.findFirst1ByOrderByTimeOfReleaseAsc()).thenReturn(Optional.of(oldestItem));

        Optional<LocalDateTime> actual = newsService.getTimestampOfOldestItem();

        assertThat(actual.get()).isEqualTo(expected);
    }

    @Test
    public void shouldGetEmptyOptionalWhenNoItemsReturned() {
        Optional<LocalDateTime> actual = newsService.getTimestampOfOldestItem();

        assertThat(actual.isPresent()).isFalse();
    }

    @Test
    public void shouldGetLatestItemsWithFutureDated() {
        LocalDateTime timestamp = LocalDateTime.now();
        List<NewsItemEntity> expected = Lists.newArrayList(NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(123L)
                .timeOfRelease(timestamp)
                .build());

        when(repository.findFirst30ByOrderByTimeOfReleaseDesc()).thenReturn(expected);

        List<NewsItem> actual = newsService.getLatest(10, true);

        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual.get(0).getItem()).isEqualTo("item");
        assertThat(actual.get(0).getHeadline()).isEqualTo("headline");
        assertThat(actual.get(0).getAuthor()).isEqualTo(null);
        assertThat(actual.get(0).getId()).isEqualTo(123L);
        assertThat(actual.get(0).getTimeOfRelease()).isEqualTo(timestamp);
    }

    @Test
    public void shouldGetLatestItemsWithoutFutureDated() {
        LocalDateTime timestamp = LocalDateTime.now();
        LocalDateTime now = LocalDateTime.now().plusHours(1);
        List<NewsItemEntity> expected = Lists.newArrayList(NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(123L)
                .timeOfRelease(timestamp)
                .build());

        when(dateHelper.getLocalDateTimeOfNow()).thenReturn(now);
        when(repository.findFirst30ByTimeOfReleaseLessThanOrderByTimeOfReleaseDesc(now)).thenReturn(expected);

        List<NewsItem> actual = newsService.getLatest(10, false);

        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual.get(0).getItem()).isEqualTo("item");
        assertThat(actual.get(0).getHeadline()).isEqualTo("headline");
        assertThat(actual.get(0).getAuthor()).isEqualTo(null);
        assertThat(actual.get(0).getId()).isEqualTo(123L);
        assertThat(actual.get(0).getTimeOfRelease()).isEqualTo(timestamp);
    }

    @Test
    public void shouldGetByOldMonth() {
        LocalDateTime timestamp = LocalDateTime.now();
        List<NewsItemEntity> expected = Lists.newArrayList(NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(123L)
                .timeOfRelease(timestamp)
                .build());

        LocalDateTime fromTimestamp = LocalDateTime.of(2000, 11, 1, 0, 0, 0);
        LocalDateTime toTimestamp = LocalDateTime.of(2000, 11, 30, 23, 59, 59);

        when(dateHelper.getLocalDateTimeOfNow()).thenReturn(timestamp);
        when(repository.findAllByTimeOfReleaseBetweenOrderByTimeOfReleaseDesc(fromTimestamp, toTimestamp)).thenReturn(expected);

        List<NewsItem> actual = newsService.getByMonth(11, 2000, false);

        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual.get(0).getItem()).isEqualTo("item");
        assertThat(actual.get(0).getHeadline()).isEqualTo("headline");
        assertThat(actual.get(0).getAuthor()).isEqualTo(null);
        assertThat(actual.get(0).getId()).isEqualTo(123L);
        assertThat(actual.get(0).getTimeOfRelease()).isEqualTo(timestamp);
    }

    @Test
    public void shouldGetByCurrentMonthWithFutureDated() {
        LocalDateTime timestamp = LocalDateTime.now();
        List<NewsItemEntity> expected = Lists.newArrayList(NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(123L)
                .timeOfRelease(timestamp)
                .build());

        LocalDateTime fromTimestamp = LocalDateTime.of(2000, 11, 1, 0, 0, 0);
        LocalDateTime toTimestamp = LocalDateTime.of(2000, 11, 30, 23, 59, 59);
        LocalDateTime todayTimestamp = LocalDateTime.of(2000, 11, 15, 12, 0, 0);

        when(dateHelper.getLocalDateTimeOfNow()).thenReturn(todayTimestamp);
        when(repository.findAllByTimeOfReleaseBetweenOrderByTimeOfReleaseDesc(fromTimestamp, toTimestamp)).thenReturn(expected);

        List<NewsItem> actual = newsService.getByMonth(11, 2000, true);

        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual.get(0).getItem()).isEqualTo("item");
        assertThat(actual.get(0).getHeadline()).isEqualTo("headline");
        assertThat(actual.get(0).getAuthor()).isEqualTo(null);
        assertThat(actual.get(0).getId()).isEqualTo(123L);
        assertThat(actual.get(0).getTimeOfRelease()).isEqualTo(timestamp);
    }

    @Test
    public void shouldGetByCurrentMonthWithoutFutureDated() {
        LocalDateTime timestamp = LocalDateTime.now();
        List<NewsItemEntity> expected = Lists.newArrayList(NewsItemEntity.builder()
                .item("item")
                .headline("headline")
                .author(null)
                .id(123L)
                .timeOfRelease(timestamp)
                .build());

        LocalDateTime fromTimestamp = LocalDateTime.of(2000, 11, 1, 0, 0, 0);
        LocalDateTime todayTimestamp = LocalDateTime.of(2000, 11, 15, 12, 0, 0);

        when(dateHelper.getLocalDateTimeOfNow()).thenReturn(todayTimestamp);
        when(repository.findAllByTimeOfReleaseBetweenOrderByTimeOfReleaseDesc(fromTimestamp, todayTimestamp)).thenReturn(expected);

        List<NewsItem> actual = newsService.getByMonth(11, 2000, false);

        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual.get(0).getItem()).isEqualTo("item");
        assertThat(actual.get(0).getHeadline()).isEqualTo("headline");
        assertThat(actual.get(0).getAuthor()).isEqualTo(null);
        assertThat(actual.get(0).getId()).isEqualTo(123L);
        assertThat(actual.get(0).getTimeOfRelease()).isEqualTo(timestamp);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenNoItemFound() {
        assertThatThrownBy(() -> newsService.editNewsItem(1, "headline", "story", LocalDateTime.now(), null, null))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void shouldEditNewsItemWithoutYoutubeVideo() {
        LocalDateTime timestamp = LocalDateTime.now();
        LocalDateTime newTimestamp = LocalDateTime.now().plusHours(1);
        NewsItemEntity returnedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline2")
                .author(null)
                .id(12L)
                .timeOfRelease(timestamp)
                .build();
        NewsItemEntity expectedEntity = NewsItemEntity.builder()
                .item("story")
                .headline("headline")
                .author(null)
                .id(12L)
                .timeOfRelease(newTimestamp)
                .build();
        NewsItem expectedItem = NewsItem.builder()
                .item("story")
                .headline("headline")
                .author(null)
                .id(12L)
                .timeOfRelease(newTimestamp)
                .build();

        when(repository.findById(12L)).thenReturn(Optional.of(returnedEntity));

        NewsItem item = newsService.editNewsItem(12L, "headline", "story", newTimestamp, null, null);

        assertThat(item).isEqualTo(expectedItem);

        verify(repository).save(expectedEntity);
        verify(hfctvService, never()).editVideoByNewsItemId(any(), any(), anyInt(), any(), any(), anyList());
        verify(hfctvService).removeVideoByNewsItemId(12L);
    }

    @Test
    public void shouldEditNewsItemWithYoutubeVideo() {
        LocalDateTime timestamp = LocalDateTime.now();
        NewsItemEntity returnedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline2")
                .author(null)
                .id(12L)
                .timeOfRelease(timestamp)
                .build();
        NewsItemEntity expectedEntity = NewsItemEntity.builder()
                .item("story")
                .headline("headline")
                .author(null)
                .id(12L)
                .timeOfRelease(timestamp)
                .build();
        NewsItem expectedItem = NewsItem.builder()
                .item("story")
                .headline("headline")
                .author(null)
                .id(12L)
                .timeOfRelease(timestamp)
                .build();
        Fixture expectedFixture = Fixture.builder().build();

        when(dateHelper.getCurrentSeason()).thenReturn(2010);
        when(fixtureService.getFixtureById(123L, false, false)).thenReturn(expectedFixture);
        when(repository.findById(12L)).thenReturn(Optional.of(returnedEntity));

        NewsItem item = newsService.editNewsItem(12L, "headline", "story", null, "ewfanke72L", 123L);

        assertThat(item).isEqualTo(expectedItem);

        verify(repository).save(expectedEntity);
        verify(hfctvService).editVideoByNewsItemId("ewfanke72L", timestamp, 2010, expectedEntity, expectedFixture, Lists.newArrayList());
        verify(hfctvService, never()).removeVideoByNewsItemId(anyLong());
    }


    @Test
    public void shouldEditNewsItemWithYoutubeVideoButNoRelatedFixture() {
        LocalDateTime timestamp = LocalDateTime.now();
        NewsItemEntity returnedEntity = NewsItemEntity.builder()
                .item("item")
                .headline("headline2")
                .author(null)
                .id(12L)
                .timeOfRelease(timestamp)
                .build();
        NewsItemEntity expectedEntity = NewsItemEntity.builder()
                .item("story")
                .headline("headline")
                .author(null)
                .id(12L)
                .timeOfRelease(timestamp)
                .build();
        NewsItem expectedItem = NewsItem.builder()
                .item("story")
                .headline("headline")
                .author(null)
                .id(12L)
                .timeOfRelease(timestamp)
                .build();

        when(dateHelper.getCurrentSeason()).thenReturn(2010);
        when(repository.findById(12L)).thenReturn(Optional.of(returnedEntity));

        NewsItem item = newsService.editNewsItem(12L, "headline", "story", null, "ewfanke72L", null);

        assertThat(item).isEqualTo(expectedItem);

        verify(repository).save(expectedEntity);
        verify(hfctvService).editVideoByNewsItemId("ewfanke72L", timestamp, 2010, expectedEntity, null, Lists.newArrayList());
        verify(hfctvService, never()).removeVideoByNewsItemId(anyLong());
    }
}
