/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.news;

import com.google.common.collect.Lists;
import net.greensnet.exceptions.NotFoundException;
import net.greensnet.news.domain.NewsItem;
import net.greensnet.news.domain.NewsResponseStatus;
import net.greensnet.service.hfctv.HfctvService;
import net.greensnet.util.DateHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static net.greensnet.news.NewsAdminController.*;
import static net.greensnet.news.domain.NewsResponseStatus.Action.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewsAdminControllerTest {

    @Mock
    public NewsService newsService;
    @Mock
    public DateHelper dateHelper;
    @Mock
    public HfctvService hfctvService;

    private NewsAdminController controller;

    private Model model;

    @BeforeEach
    public void setup() {
        controller = new NewsAdminController(newsService, hfctvService, dateHelper);
        model = new ConcurrentModel();
    }

    @Test
    public void shouldListNewsItemsForCurrentMonth() {
        LocalDateTime dateTime = LocalDateTime.of(1999, 11, 15, 0, 0, 0);
        LocalDateTime currentDateTime = LocalDateTime.of(2010, 6, 15, 0, 0, 0);
        String[] months = {"Jan", "Feb", "Mar", "etc."};

        List<NewsItem> expectedItems = Lists.newArrayList(NewsItem.builder().id(12L).build());

        when(newsService.getTimestampOfOldestItem()).thenReturn(Optional.of(dateTime));
        when(newsService.getByMonth(6, 2010, true)).thenReturn(expectedItems);
        when(dateHelper.getLocalDateTimeOfNow()).thenReturn(currentDateTime);
        when(dateHelper.getCurrentMonth()).thenReturn(8);
        when(dateHelper.getCurrentYear()).thenReturn(2019);
        when(dateHelper.getMonthArray()).thenReturn(months);

        String pageToDisplay = controller.listNewsItems(model);

        assertThat(pageToDisplay).isEqualTo("th_admin/news");
        assertThat(model.getAttribute(FIRST_MONTH)).isEqualTo(11);
        assertThat(model.getAttribute(FIRST_YEAR)).isEqualTo(1999);
        assertThat(model.getAttribute(CURRENT_MONTH)).isEqualTo(8);
        assertThat(model.getAttribute(CURRENT_YEAR)).isEqualTo(2019);
        assertThat(model.getAttribute(MONTH_LIST)).isEqualTo(months);
        assertThat(model.getAttribute(MONTH)).isEqualTo(6);
        assertThat(model.getAttribute(YEAR)).isEqualTo(2010);
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Admin - News");
        assertThat(model.getAttribute(NEWS_ITEMS)).isEqualTo(expectedItems);

    }

    @Test
    public void shouldListNewsItemsForGivenMonth() {
        LocalDateTime dateTime = LocalDateTime.of(1999, 11, 15, 0, 0, 0);
        String[] months = {"Jan", "Feb", "Mar", "etc."};

        List<NewsItem> expectedItems = Lists.newArrayList(NewsItem.builder().id(12L).build());

        when(newsService.getTimestampOfOldestItem()).thenReturn(Optional.of(dateTime));
        when(newsService.getByMonth(10, 2015, true)).thenReturn(expectedItems);
        when(dateHelper.getCurrentMonth()).thenReturn(8);
        when(dateHelper.getCurrentYear()).thenReturn(2019);
        when(dateHelper.getMonthArray()).thenReturn(months);

        String pageToDisplay = controller.listNewsItems(model, 10, 2015);

        assertThat(pageToDisplay).isEqualTo("th_admin/news");
        assertThat(model.getAttribute(FIRST_MONTH)).isEqualTo(11);
        assertThat(model.getAttribute(FIRST_YEAR)).isEqualTo(1999);
        assertThat(model.getAttribute(CURRENT_MONTH)).isEqualTo(8);
        assertThat(model.getAttribute(CURRENT_YEAR)).isEqualTo(2019);
        assertThat(model.getAttribute(MONTH_LIST)).isEqualTo(months);
        assertThat(model.getAttribute(MONTH)).isEqualTo(10);
        assertThat(model.getAttribute(YEAR)).isEqualTo(2015);
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Admin - News");
        assertThat(model.getAttribute(NEWS_ITEMS)).isEqualTo(expectedItems);
    }

    @Test
    public void shouldHandleNoItemsExistingYet() {
        String[] months = {"Jan", "Feb", "Mar", "etc."};

        List<NewsItem> expectedItems = Lists.newArrayList();

        when(newsService.getTimestampOfOldestItem()).thenReturn(Optional.empty());
        when(newsService.getByMonth(10, 2015, true)).thenReturn(expectedItems);
        when(dateHelper.getCurrentMonth()).thenReturn(8);
        when(dateHelper.getCurrentYear()).thenReturn(2019);
        when(dateHelper.getMonthArray()).thenReturn(months);

        String pageToDisplay = controller.listNewsItems(model, 10, 2015);

        assertThat(pageToDisplay).isEqualTo("th_admin/news");
        assertThat(model.getAttribute(FIRST_MONTH)).isEqualTo(-1);
        assertThat(model.getAttribute(FIRST_YEAR)).isEqualTo(-1);
        assertThat(model.getAttribute(CURRENT_MONTH)).isEqualTo(8);
        assertThat(model.getAttribute(CURRENT_YEAR)).isEqualTo(2019);
        assertThat(model.getAttribute(MONTH_LIST)).isEqualTo(months);
        assertThat(model.getAttribute(MONTH)).isEqualTo(10);
        assertThat(model.getAttribute(YEAR)).isEqualTo(2015);
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Admin - News");
        assertThat(model.getAttribute(NEWS_ITEMS)).isEqualTo(expectedItems);
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenItemDoesntExist() {
        when(newsService.getNewsItemById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.viewItem(model, 1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void shouldRetrieveItemToView() {
        LocalDateTime timestamp = LocalDateTime.of(2011, 1, 31, 12, 0, 0);

        NewsItem expectedItem = NewsItem.builder()
                .timeOfRelease(timestamp)
                .build();

        when(newsService.getNewsItemById(1L)).thenReturn(Optional.of(expectedItem));
        when(hfctvService.getByNewsItem(expectedItem)).thenReturn(Optional.empty());

        String pageToDisplay = controller.viewItem(model, 1L);

        assertThat(pageToDisplay).isEqualTo("th_admin/news");
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Admin - News");
        assertThat(model.getAttribute(NEWS_ITEM)).isEqualTo(expectedItem);
        assertThat(model.getAttribute(YOUTUBE_VIDEO)).isNull();
    }

    @Test
    public void shouldThrowNotFoundExceptionOnDeleteWhenItemDoesntExist() {
        when(newsService.deleteItemById(1L)).thenThrow(new NotFoundException());

        assertThatThrownBy(() -> controller.deleteItem(model, 1L))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void shouldDeleteItem() {
        LocalDateTime timestamp = LocalDateTime.of(2011, 1, 31, 12, 0, 0);
        String[] months = {"Jan", "Feb", "Mar", "etc."};

        NewsItem expectedItem = NewsItem.builder()
                .timeOfRelease(timestamp)
                .build();
        List<NewsItem> expectedItems = Lists.newArrayList(NewsItem.builder().build());

        when(newsService.deleteItemById(1L)).thenReturn(expectedItem);
        when(newsService.getTimestampOfOldestItem()).thenReturn(Optional.empty());
        when(newsService.getByMonth(1, 2011, true)).thenReturn(expectedItems);
        when(dateHelper.getCurrentMonth()).thenReturn(8);
        when(dateHelper.getCurrentYear()).thenReturn(2019);
        when(dateHelper.getMonthArray()).thenReturn(months);

        String pageToDisplay = controller.deleteItem(model, 1L);

        assertThat(pageToDisplay).isEqualTo("th_admin/news");
        assertThat(model.getAttribute(FIRST_MONTH)).isEqualTo(-1);
        assertThat(model.getAttribute(FIRST_YEAR)).isEqualTo(-1);
        assertThat(model.getAttribute(CURRENT_MONTH)).isEqualTo(8);
        assertThat(model.getAttribute(CURRENT_YEAR)).isEqualTo(2019);
        assertThat(model.getAttribute(MONTH_LIST)).isEqualTo(months);
        assertThat(model.getAttribute(MONTH)).isEqualTo(1);
        assertThat(model.getAttribute(YEAR)).isEqualTo(2011);
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Admin - News");
        assertThat(model.getAttribute(NEWS_ITEMS)).isEqualTo(expectedItems);
        assertThat(model.getAttribute(NEWS_ITEM)).isEqualTo(expectedItem);
        assertThat(model.getAttribute(RESPONSE_STATUS)).isEqualTo(NewsResponseStatus.builder().action(DELETE).success(true).build());
    }

    @Test
    public void shouldCreateNewsItem() {
        LocalDateTime timestamp = LocalDateTime.of(2020, 1, 1, 22, 11, 0);
        String[] months = {"Jan", "Feb", "Mar", "etc."};

        NewsItem expectedItem = NewsItem.builder()
                .timeOfRelease(timestamp)
                .build();
        List<NewsItem> expectedItems = Lists.newArrayList(expectedItem);

        when(newsService.createNewsItem("Headline", "Story", timestamp, null, null)).thenReturn(expectedItem);
        when(newsService.getTimestampOfOldestItem()).thenReturn(Optional.empty());
        when(newsService.getByMonth(6, 2011, true)).thenReturn(expectedItems);
        when(dateHelper.getCurrentMonth()).thenReturn(8);
        when(dateHelper.getCurrentYear()).thenReturn(2019);
        when(dateHelper.getMonthArray()).thenReturn(months);
        when(dateHelper.parseStandardDateTimeFormat("2020-1-1 22:11:00")).thenReturn(timestamp);

        String pageToDisplay = controller.createItem(model,
                6, 2011, "Story",
                "Headline", "2020-1-1 22:11:00",
                null, null);

        assertThat(pageToDisplay).isEqualTo("th_admin/news");
        assertThat(model.getAttribute(FIRST_MONTH)).isEqualTo(-1);
        assertThat(model.getAttribute(FIRST_YEAR)).isEqualTo(-1);
        assertThat(model.getAttribute(CURRENT_MONTH)).isEqualTo(8);
        assertThat(model.getAttribute(CURRENT_YEAR)).isEqualTo(2019);
        assertThat(model.getAttribute(MONTH_LIST)).isEqualTo(months);
        assertThat(model.getAttribute(MONTH)).isEqualTo(6);
        assertThat(model.getAttribute(YEAR)).isEqualTo(2011);
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Admin - News");
        assertThat(model.getAttribute(NEWS_ITEMS)).isEqualTo(expectedItems);
        assertThat(model.getAttribute(NEWS_ITEM)).isEqualTo(expectedItem);
        assertThat(model.getAttribute(RESPONSE_STATUS)).isEqualTo(NewsResponseStatus.builder().action(CREATE).success(true).build());
    }

    @Test
    public void shouldCreateNewsItemWhenTimestampIsNull() {
        LocalDateTime timestamp = LocalDateTime.of(2020, 1, 1, 22, 11, 0);
        String[] months = {"Jan", "Feb", "Mar", "etc."};

        NewsItem expectedItem = NewsItem.builder()
                .timeOfRelease(timestamp)
                .build();
        List<NewsItem> expectedItems = Lists.newArrayList(expectedItem);

        when(newsService.createNewsItem("Headline", "Story", timestamp, null, null)).thenReturn(expectedItem);
        when(newsService.getTimestampOfOldestItem()).thenReturn(Optional.empty());
        when(newsService.getByMonth(6, 2011, true)).thenReturn(expectedItems);
        when(dateHelper.getCurrentMonth()).thenReturn(8);
        when(dateHelper.getCurrentYear()).thenReturn(2019);
        when(dateHelper.getMonthArray()).thenReturn(months);
        when(dateHelper.getLocalDateTimeOfNow()).thenReturn(timestamp);

        String pageToDisplay = controller.createItem(model,
                6, 2011, "Story",
                "Headline", null,
                null, null);

        assertThat(pageToDisplay).isEqualTo("th_admin/news");
        assertThat(model.getAttribute(FIRST_MONTH)).isEqualTo(-1);
        assertThat(model.getAttribute(FIRST_YEAR)).isEqualTo(-1);
        assertThat(model.getAttribute(CURRENT_MONTH)).isEqualTo(8);
        assertThat(model.getAttribute(CURRENT_YEAR)).isEqualTo(2019);
        assertThat(model.getAttribute(MONTH_LIST)).isEqualTo(months);
        assertThat(model.getAttribute(MONTH)).isEqualTo(6);
        assertThat(model.getAttribute(YEAR)).isEqualTo(2011);
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Admin - News");
        assertThat(model.getAttribute(NEWS_ITEMS)).isEqualTo(expectedItems);
        assertThat(model.getAttribute(NEWS_ITEM)).isEqualTo(expectedItem);
        assertThat(model.getAttribute(RESPONSE_STATUS)).isEqualTo(NewsResponseStatus.builder().action(CREATE).success(true).build());
    }

    @Test
    public void shouldNotCreateNewsItemWhenTimestampCannotBeParsed() {
        LocalDateTime timestamp = LocalDateTime.of(2020, 1, 1, 22, 11, 0);
        String[] months = {"Jan", "Feb", "Mar", "etc."};

        NewsItem expectedItem = NewsItem.builder()
                .timeOfRelease(timestamp)
                .build();
        List<NewsItem> expectedItems = Lists.newArrayList(expectedItem);

        when(newsService.getTimestampOfOldestItem()).thenReturn(Optional.empty());
        when(newsService.getByMonth(6, 2011, true)).thenReturn(expectedItems);
        when(dateHelper.getCurrentMonth()).thenReturn(8);
        when(dateHelper.getCurrentYear()).thenReturn(2019);
        when(dateHelper.getMonthArray()).thenReturn(months);
        when(dateHelper.parseStandardDateTimeFormat("abcd")).thenReturn(null);

        String pageToDisplay = controller.createItem(model,
                6, 2011, "Story",
                "Headline", "abcd",
                null, null);

        verify(newsService, never()).createNewsItem(any(), any(), any(), any(), any());

        assertThat(pageToDisplay).isEqualTo("th_admin/news");
        assertThat(model.getAttribute(FIRST_MONTH)).isEqualTo(-1);
        assertThat(model.getAttribute(FIRST_YEAR)).isEqualTo(-1);
        assertThat(model.getAttribute(CURRENT_MONTH)).isEqualTo(8);
        assertThat(model.getAttribute(CURRENT_YEAR)).isEqualTo(2019);
        assertThat(model.getAttribute(MONTH_LIST)).isEqualTo(months);
        assertThat(model.getAttribute(MONTH)).isEqualTo(6);
        assertThat(model.getAttribute(YEAR)).isEqualTo(2011);
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Admin - News");
        assertThat(model.getAttribute(NEWS_ITEMS)).isEqualTo(expectedItems);
        assertThat(model.getAttribute(RESPONSE_STATUS)).isEqualTo(NewsResponseStatus.builder().action(CREATE).success(false).build());
        assertThat(model.getAttribute(ERROR_MESSAGE)).isEqualTo("abcd is not a valid date. expected format is yyyy-mm-dd hh:MM:ss");
    }




    @Test
    public void shouldEditNewsItem() {
        LocalDateTime timestamp = LocalDateTime.of(2020, 1, 1, 22, 11, 0);
        String[] months = {"Jan", "Feb", "Mar", "etc."};

        NewsItem expectedItem = NewsItem.builder()
                .timeOfRelease(timestamp)
                .build();
        List<NewsItem> expectedItems = Lists.newArrayList(expectedItem);

        when(newsService.editNewsItem(1L, "Headline", "Story", timestamp, null, null)).thenReturn(expectedItem);
        when(newsService.getTimestampOfOldestItem()).thenReturn(Optional.empty());
        when(newsService.getByMonth(6, 2011, true)).thenReturn(expectedItems);
        when(dateHelper.getCurrentMonth()).thenReturn(8);
        when(dateHelper.getCurrentYear()).thenReturn(2019);
        when(dateHelper.getMonthArray()).thenReturn(months);
        when(dateHelper.parseStandardDateTimeFormat("2020-1-1 22:11:00")).thenReturn(timestamp);

        String pageToDisplay = controller.editItem(model, 1L,
                6, 2011, "Story",
                "Headline", "2020-1-1 22:11:00",
                null, null);

        assertThat(pageToDisplay).isEqualTo("th_admin/news");
        assertThat(model.getAttribute(FIRST_MONTH)).isEqualTo(-1);
        assertThat(model.getAttribute(FIRST_YEAR)).isEqualTo(-1);
        assertThat(model.getAttribute(CURRENT_MONTH)).isEqualTo(8);
        assertThat(model.getAttribute(CURRENT_YEAR)).isEqualTo(2019);
        assertThat(model.getAttribute(MONTH_LIST)).isEqualTo(months);
        assertThat(model.getAttribute(MONTH)).isEqualTo(6);
        assertThat(model.getAttribute(YEAR)).isEqualTo(2011);
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Admin - News");
        assertThat(model.getAttribute(NEWS_ITEMS)).isEqualTo(expectedItems);
        assertThat(model.getAttribute(NEWS_ITEM)).isEqualTo(expectedItem);
        assertThat(model.getAttribute(RESPONSE_STATUS)).isEqualTo(NewsResponseStatus.builder().action(EDIT).success(true).build());
    }

    @Test
    public void shouldEditNewsItemWhenTimestampIsNull() {
        LocalDateTime timestamp = LocalDateTime.of(2020, 1, 1, 22, 11, 0);
        String[] months = {"Jan", "Feb", "Mar", "etc."};

        NewsItem expectedItem = NewsItem.builder()
                .timeOfRelease(timestamp)
                .build();
        List<NewsItem> expectedItems = Lists.newArrayList(expectedItem);

        when(newsService.editNewsItem(1L, "Headline", "Story", timestamp, null, null)).thenReturn(expectedItem);
        when(newsService.getTimestampOfOldestItem()).thenReturn(Optional.empty());
        when(newsService.getByMonth(6, 2011, true)).thenReturn(expectedItems);
        when(dateHelper.getCurrentMonth()).thenReturn(8);
        when(dateHelper.getCurrentYear()).thenReturn(2019);
        when(dateHelper.getMonthArray()).thenReturn(months);
        when(dateHelper.getLocalDateTimeOfNow()).thenReturn(timestamp);

        String pageToDisplay = controller.editItem(model, 1L,
                6, 2011, "Story",
                "Headline", null,
                null, null);

        assertThat(pageToDisplay).isEqualTo("th_admin/news");
        assertThat(model.getAttribute(FIRST_MONTH)).isEqualTo(-1);
        assertThat(model.getAttribute(FIRST_YEAR)).isEqualTo(-1);
        assertThat(model.getAttribute(CURRENT_MONTH)).isEqualTo(8);
        assertThat(model.getAttribute(CURRENT_YEAR)).isEqualTo(2019);
        assertThat(model.getAttribute(MONTH_LIST)).isEqualTo(months);
        assertThat(model.getAttribute(MONTH)).isEqualTo(6);
        assertThat(model.getAttribute(YEAR)).isEqualTo(2011);
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Admin - News");
        assertThat(model.getAttribute(NEWS_ITEMS)).isEqualTo(expectedItems);
        assertThat(model.getAttribute(NEWS_ITEM)).isEqualTo(expectedItem);
        assertThat(model.getAttribute(RESPONSE_STATUS)).isEqualTo(NewsResponseStatus.builder().action(EDIT).success(true).build());
    }

    @Test
    public void shouldNotEditNewsItemWhenTimestampCannotBeParsed() {
        LocalDateTime timestamp = LocalDateTime.of(2020, 1, 1, 22, 11, 0);
        String[] months = {"Jan", "Feb", "Mar", "etc."};

        NewsItem expectedItem = NewsItem.builder()
                .timeOfRelease(timestamp)
                .build();
        List<NewsItem> expectedItems = Lists.newArrayList(expectedItem);

        when(newsService.getTimestampOfOldestItem()).thenReturn(Optional.empty());
        when(newsService.getByMonth(6, 2011, true)).thenReturn(expectedItems);
        when(dateHelper.getCurrentMonth()).thenReturn(8);
        when(dateHelper.getCurrentYear()).thenReturn(2019);
        when(dateHelper.getMonthArray()).thenReturn(months);
        when(dateHelper.parseStandardDateTimeFormat("abcd")).thenReturn(null);

        String pageToDisplay = controller.editItem(model, 1L,
                6, 2011, "Story",
                "Headline", "abcd",
                null, null);

        verify(newsService, never()).createNewsItem(any(), any(), any(), any(), any());

        assertThat(pageToDisplay).isEqualTo("th_admin/news");
        assertThat(model.getAttribute(FIRST_MONTH)).isEqualTo(-1);
        assertThat(model.getAttribute(FIRST_YEAR)).isEqualTo(-1);
        assertThat(model.getAttribute(CURRENT_MONTH)).isEqualTo(8);
        assertThat(model.getAttribute(CURRENT_YEAR)).isEqualTo(2019);
        assertThat(model.getAttribute(MONTH_LIST)).isEqualTo(months);
        assertThat(model.getAttribute(MONTH)).isEqualTo(6);
        assertThat(model.getAttribute(YEAR)).isEqualTo(2011);
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Admin - News");
        assertThat(model.getAttribute(NEWS_ITEMS)).isEqualTo(expectedItems);
        assertThat(model.getAttribute(RESPONSE_STATUS)).isEqualTo(NewsResponseStatus.builder().action(EDIT).success(false).build());
        assertThat(model.getAttribute(ERROR_MESSAGE)).isEqualTo("abcd is not a valid date. expected format is yyyy-mm-dd hh:MM:ss");
    }

    @Test
    public void shouldThrowNotFoundExceptionOnEditWhenItemDoesNotExist() {
        when(newsService.editNewsItem(1L, null, null, null, null, null))
                .thenThrow(new NotFoundException());

        assertThatThrownBy(() -> controller.editItem(model, 1L, 0, 0, null, null, null, null, null))
            .isInstanceOf(NotFoundException.class);

    }
}
