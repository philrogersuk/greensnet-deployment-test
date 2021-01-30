package net.greensnet.news;

import net.greensnet.domain.hfctv.YoutubeVideo;
import net.greensnet.exceptions.NotFoundException;
import net.greensnet.news.domain.NewsItem;
import net.greensnet.service.hfctv.HfctvService;
import net.greensnet.util.DateHelper;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.ConcurrentModel;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static net.greensnet.news.NewsController.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class NewsControllerTest {

    @Mock
    private NewsService newsService;
    @Mock
    private DateHelper dateHelper;
    @Mock
    private HfctvService hfctvService;

    private NewsController controller;

    @BeforeEach
    public void setup() {
        controller = new NewsController(newsService, dateHelper, hfctvService);
    }

    @Test
    public void shouldRetrieveArchivePage() {
        LocalDateTime timestamp = LocalDateTime.now();
        Model model = new ConcurrentModel();

        when(newsService.getTimestampOfOldestItem()).thenReturn(Optional.of(timestamp));

        String expectedPage = controller.getNewsArchivePage(model);

        assertThat(expectedPage).isEqualTo("th_news/archive");
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("News Archive");
        assertThat(model.getAttribute(FIRST_ITEM)).isEqualTo(timestamp);
    }

    @Test
    public void shouldRetrieveArchivePageWithNoOldestPage() {
        LocalDateTime timestamp = LocalDateTime.now();
        Model model = new ConcurrentModel();

        when(newsService.getTimestampOfOldestItem()).thenReturn(Optional.empty());
        when(dateHelper.getLocalDateTimeOfNow()).thenReturn(timestamp);

        String expectedPage = controller.getNewsArchivePage(model);

        assertThat(expectedPage).isEqualTo("th_news/archive");
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("News Archive");
        assertThat(model.getAttribute(FIRST_ITEM)).isEqualTo(timestamp);
    }

    @Test
    public void shouldRetrieveNewsByMonth() {
        Model model = new ConcurrentModel();

        List<NewsItem> expectedItems = Lists.newArrayList(NewsItem.builder().build());

        when(newsService.getByMonth(2, 2010, false)).thenReturn(expectedItems);

        String expectedPage = controller.getNewsByMonth(model, 2010, 2);

        assertThat(expectedPage).isEqualTo("th_news/list");
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("News - February 2010");
        assertThat(model.getAttribute(NEWS_ITEMS)).isEqualTo(expectedItems);
        assertThat(model.getAttribute(MONTH)).isEqualTo(2);
        assertThat(model.getAttribute(YEAR)).isEqualTo(2010);
    }

    @Test
    public void shouldRetrieveLatestNews() {
        LocalDateTime timestamp = LocalDateTime.of(2011, 6, 1, 0, 0, 0);
        Model model = new ConcurrentModel();

        List<NewsItem> expectedItems = Lists.newArrayList(NewsItem.builder().build());

        when(dateHelper.getLocalDateTimeOfNow()).thenReturn(timestamp);
        when(newsService.getLatest(30, false)).thenReturn(expectedItems);

        String expectedPage = controller.getLatestNews(model);

        assertThat(expectedPage).isEqualTo("th_news/list");
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Latest News");
        assertThat(model.getAttribute(NEWS_ITEMS)).isEqualTo(expectedItems);
        assertThat(model.getAttribute(MONTH)).isEqualTo(6);
        assertThat(model.getAttribute(YEAR)).isEqualTo(2011);
    }

    @Test
    public void shouldGetNewsItemWithAllRelatedInfo() {
        Model model = new ConcurrentModel();

        LocalDateTime timestamp = LocalDateTime.now();
        NewsItem expectedItem = NewsItem.builder().id(1L).timeOfRelease(timestamp).build();
        NewsItem nextItem = NewsItem.builder().id(2L).build();
        NewsItem previousItem = NewsItem.builder().id(3L).build();
        YoutubeVideo expectedVideo = YoutubeVideo.builder().id(1L).build();

        when(newsService.getNewsItemById(1L)).thenReturn(Optional.of(expectedItem));
        when(newsService.getPreviousNewsItem(timestamp)).thenReturn(Optional.of(previousItem));
        when(newsService.getNextNewsItem(timestamp)).thenReturn(Optional.of(nextItem));
        when(hfctvService.getByNewsItem(expectedItem)).thenReturn(Optional.of(expectedVideo));

        String expectedPage = controller.getNewsItem(model, 1L, "");

        assertThat(expectedPage).isEqualTo("th_news/item");
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo(null);
        assertThat(model.getAttribute(NEWS_ITEM)).isEqualTo(expectedItem);
        assertThat(model.getAttribute(NEXT_ITEM)).isEqualTo(nextItem);
        assertThat(model.getAttribute(PREVIOUS_ITEM)).isEqualTo(previousItem);
        assertThat(model.getAttribute(YOUTUBE_VIDEO)).isEqualTo(expectedVideo);
    }

    @Test
    public void shouldGetNewsItemWithNoRelatedInfo() {
        Model model = new ConcurrentModel();

        LocalDateTime timestamp = LocalDateTime.now();
        NewsItem expectedItem = NewsItem.builder().id(1L).timeOfRelease(timestamp).build();

        when(newsService.getNewsItemById(1L)).thenReturn(Optional.of(expectedItem));
        when(newsService.getPreviousNewsItem(timestamp)).thenReturn(Optional.empty());
        when(newsService.getNextNewsItem(timestamp)).thenReturn(Optional.empty());
        when(hfctvService.getByNewsItem(expectedItem)).thenReturn(Optional.empty());

        String expectedPage = controller.getNewsItem(model, 1L, "");

        assertThat(expectedPage).isEqualTo("th_news/item");
        assertThat(model.getAttribute(PAGE_TITLE)).isNull();
        assertThat(model.getAttribute(NEWS_ITEM)).isEqualTo(expectedItem);
        assertThat(model.getAttribute(NEXT_ITEM)).isNull();
        assertThat(model.getAttribute(PREVIOUS_ITEM)).isNull();
        assertThat(model.getAttribute(YOUTUBE_VIDEO)).isNull();
    }

    @Test
    public void shouldThrowNotFoundExceptionWhenNewsItemDoesntExist() {
        Model model = new ConcurrentModel();

        when(newsService.getNewsItemById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.getNewsItem(model, 1L, ""))
            .isInstanceOf(NotFoundException.class);
    }

    @Test
    public void shouldRedirectToMonthPage() {
        assertThat(controller.getNews(null, "archive", 11, 2011)).isEqualTo("redirect:/News/Archive/2011/12");
    }

    @Test
    public void shouldRedirectToArchivePage() {
        assertThat(controller.getNews(null, "archive", null, 2)).isEqualTo("redirect:/News/Archive");
        assertThat(controller.getNews(null, "archive", 1, null)).isEqualTo("redirect:/News/Archive");
        assertThat(controller.getNews(null, "archive", null, null)).isEqualTo("redirect:/News/Archive");
    }

    @Test
    public void shouldRedirectToRecentPage() {
        LocalDateTime timestamp = LocalDateTime.of(2011, 6, 1, 0, 0, 0);
        Model model = new ConcurrentModel();

        when(dateHelper.getLocalDateTimeOfNow()).thenReturn(timestamp);

        assertThat(controller.getNews(model, null, null, 2)).isEqualTo("th_news/list");
        assertThat(controller.getNews(model, null, 1, 2)).isEqualTo("th_news/list");
        assertThat(controller.getNews(model, "somethingElse", null, null)).isEqualTo("th_news/list");
        assertThat(model.getAttribute(PAGE_TITLE)).isEqualTo("Latest News");
    }
}
