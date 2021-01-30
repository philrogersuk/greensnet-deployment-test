/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.news;

import net.greensnet.domain.hfctv.YoutubeVideo;
import net.greensnet.exceptions.NotFoundException;
import net.greensnet.news.domain.NewsItem;
import net.greensnet.service.hfctv.HfctvService;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Optional;

@Controller
public class NewsController {

    private static final String NEWS_ITEM_PAGE = "th_news/item";
    private static final String NEWS_LIST_PAGE = "th_news/list";
    private static final String NEWS_ARCHIVE_PAGE = "th_news/archive";

    static final String NEWS_ITEM = "newsItem";
    static final String NEXT_ITEM = "nextItem";
    static final String PREVIOUS_ITEM = "previousItem";
    static final String NEWS_ITEMS = "newsItems";
    static final String PAGE_TITLE = "page_title";
    static final String MONTH = "month";
    static final String YEAR = "year";
    static final String FIRST_ITEM = "firstItem";
    static final String YOUTUBE_VIDEO = "youtube_video";

    private final NewsService newsService;
    private final DateHelper dateHelper;
    private final HfctvService hfctvService;

    @Autowired
    public NewsController(NewsService newsService,
                          DateHelper dateHelper,
                          HfctvService hfctvService) {
        this.newsService = newsService;
        this.hfctvService = hfctvService;
        this.dateHelper = dateHelper;
    }

    @RequestMapping("/News")
    public String getNews(Model model,
                          @RequestParam(value = "method", required = false) String method,
                          @RequestParam(value = "month", required = false) Integer month,
                          @RequestParam(value = "year", required = false) Integer year) {
        if (null != method) {
            if ("archive".equals(method) && null != month && null != year) {
                return String.format("redirect:/News/Archive/%d/%d", year, month + 1);
            } else if ("archive".equals(method)) {
                return "redirect:/News/Archive";
            }
        }
        return getLatestNews(model);
    }

    @GetMapping("/News/{id}/{headline}")
    public String getNewsItem(Model model,
                              @PathVariable("id") long id,
                              @PathVariable("headline") String headline) {
        NewsItem item = newsService.getNewsItemById(id).orElseThrow(NotFoundException :: new);
        model.addAttribute(NEWS_ITEM, item);

        NewsItem next = newsService.getNextNewsItem(item.getTimeOfRelease()).orElse(null);
        model.addAttribute(NEXT_ITEM, next);

        NewsItem previous = newsService.getPreviousNewsItem(item.getTimeOfRelease()).orElse(null);
        model.addAttribute(PREVIOUS_ITEM, previous);

        Optional<YoutubeVideo> video = hfctvService.getByNewsItem(item);
        model.addAttribute(YOUTUBE_VIDEO, video.orElse(null));

        model.addAttribute(PAGE_TITLE, item.getHeadline());

        return NEWS_ITEM_PAGE;
    }

    @GetMapping("/News/Recent")
    public String getLatestNews(Model model) {
        LocalDateTime currentTimestamp = dateHelper.getLocalDateTimeOfNow();
        model.addAttribute(NEWS_ITEMS, newsService.getLatest(30, false));
        model.addAttribute(PAGE_TITLE, "Latest News");
        model.addAttribute(MONTH, currentTimestamp.getMonthValue());
        model.addAttribute(YEAR, currentTimestamp.getYear());

        return NEWS_LIST_PAGE;
    }

    @GetMapping("/News/Archive/{year}/{month}")
    public String getNewsByMonth(Model model,
                                 @PathVariable("year") Integer year,
                                 @PathVariable("month") Integer month) {
        model.addAttribute(NEWS_ITEMS, newsService.getByMonth(month, year, false));
        model.addAttribute(PAGE_TITLE,"News - " + Month.of(month).getDisplayName(TextStyle.FULL, Locale.UK) + " " + year);
        model.addAttribute(MONTH, month);
        model.addAttribute(YEAR,year);

        return NEWS_LIST_PAGE;
    }


    @GetMapping("/News/Archive")
    public String getNewsArchivePage(Model model) {
        model.addAttribute(FIRST_ITEM, newsService.getTimestampOfOldestItem()
                                            .orElse(dateHelper.getLocalDateTimeOfNow()));
        model.addAttribute(PAGE_TITLE, "News Archive");

        return NEWS_ARCHIVE_PAGE;
    }
}
