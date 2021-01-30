/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.news;

import net.greensnet.exceptions.NotFoundException;
import net.greensnet.news.domain.NewsItem;
import net.greensnet.news.domain.NewsResponseStatus;
import net.greensnet.service.hfctv.HfctvService;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.Objects.isNull;
import static net.greensnet.news.domain.NewsResponseStatus.Action.*;
import static org.apache.commons.lang.StringUtils.isNotBlank;

@Controller("AdminNewsController")
public class NewsAdminController {
	static final String PAGE_TITLE = "page_title";
	static final String FIRST_MONTH = "first_month";
	static final String FIRST_YEAR = "first_year";
	static final String CURRENT_MONTH = "current_month";
	static final String CURRENT_YEAR = "current_year";
	static final String MONTH = "month";
	static final String YEAR = "year";
	static final String MONTH_LIST = "month_list";
	static final String NEWS_ITEM = "news_item";
	static final String NEWS_ITEMS = "news_items";
	static final String YOUTUBE_VIDEO = "youtube_video";
	static final String ERROR_MESSAGE = "error_message";
	static final String RESPONSE_STATUS = "response_status";

	private final NewsService manager;
	private final HfctvService hfctvService;
	private final DateHelper dateHelper;

	@Autowired
	public NewsAdminController(NewsService manager,
                               HfctvService hfctvService,
                               DateHelper dateHelper) {
		this.manager = manager;
		this.hfctvService = hfctvService;
		this.dateHelper	= dateHelper;
	}

	@RequestMapping("AdminNews")
	public String listNewsItems(Model model) {
		LocalDateTime now = dateHelper.getLocalDateTimeOfNow();

		return listNewsItems(model, now.getMonthValue(), now.getYear());
	}


	@RequestMapping("AdminNews/{year}/{month}")
	public String listNewsItems(Model model,
								@PathVariable(value = "month") int month,
								@PathVariable(value = "year") int year) {

		Optional<LocalDateTime> oldestItemTimestamp = manager.getTimestampOfOldestItem();

		model.addAttribute(FIRST_MONTH, oldestItemTimestamp.map(LocalDateTime::getMonthValue).orElse(-1));
		model.addAttribute(FIRST_YEAR, oldestItemTimestamp.map(LocalDateTime::getYear).orElse(-1));
		model.addAttribute(CURRENT_MONTH, dateHelper.getCurrentMonth());
		model.addAttribute(CURRENT_YEAR, dateHelper.getCurrentYear());
		model.addAttribute(MONTH_LIST, dateHelper.getMonthArray());
		model.addAttribute(MONTH, month);
		model.addAttribute(YEAR, year);
		model.addAttribute(NEWS_ITEMS, manager.getByMonth(month, year, true));
		model.addAttribute(PAGE_TITLE, "Admin - News");

		return "th_admin/news";
	}

	@PostMapping("AdminNews/{id}")
	public String viewItem(Model model,
							 @PathVariable("id") long id) {
		NewsItem item = manager.getNewsItemById(id).orElseThrow(NotFoundException::new);

		model.addAttribute(NEWS_ITEM, item);
		model.addAttribute(YOUTUBE_VIDEO, hfctvService.getByNewsItem(item).orElse(null));

		model.addAttribute(MONTH, item.getTimeOfRelease().getMonthValue());
		model.addAttribute(YEAR, item.getTimeOfRelease().getYear());
		model.addAttribute(PAGE_TITLE, "Admin - News");

		return "th_admin/news";
	}


	@PostMapping("AdminNews/{id}/Delete")
	public String deleteItem(Model model,
								@PathVariable("id") long id) {
		NewsItem item = manager.deleteItemById(id);

		model.addAttribute(RESPONSE_STATUS, NewsResponseStatus.builder().action(DELETE).success(true).build());
		model.addAttribute(NEWS_ITEM, item);

		return listNewsItems(model,
				item.getTimeOfRelease().getMonthValue(),
				item.getTimeOfRelease().getYear());
	}

	@PostMapping("AdminNews/Create")
	public String createItem(Model model,
							 @RequestParam("month") int month,
							 @RequestParam("year") int year,
							 @RequestParam("story") String story,
							 @RequestParam("headline") String headline,
							 @RequestParam("datetime") String unparsedTimestamp,
							 @RequestParam("youtubeId") String youtubeId,
							 @RequestParam(value = "fixtureId", required=false) Long fixtureId) {
		LocalDateTime timeStamp = dateHelper.parseStandardDateTimeFormat(unparsedTimestamp);

		if (isNull(timeStamp)) {
			if (isNotBlank(unparsedTimestamp)){
				model.addAttribute(RESPONSE_STATUS, NewsResponseStatus.builder().action(CREATE).success(false).build());
				model.addAttribute(ERROR_MESSAGE, unparsedTimestamp + " is not a valid date. expected format is yyyy-mm-dd hh:MM:ss");
				return listNewsItems(model, month, year);
			}
			timeStamp = dateHelper.getLocalDateTimeOfNow();
		}

		NewsItem item = manager.createNewsItem(headline, story, timeStamp, youtubeId, fixtureId);

		model.addAttribute(NEWS_ITEM, item);
		model.addAttribute(RESPONSE_STATUS, NewsResponseStatus.builder().action(CREATE).success(true).build());

		return listNewsItems(model, month, year);
	}

	@PostMapping("AdminNews/Edit/{id}")
	public String editItem(Model model,
						   @PathVariable(value ="id", required = false) long id,
						   @RequestParam(value ="month", required = false) int month,
						   @RequestParam(value ="year", required = false) int year,
						   @RequestParam(value ="story", required = false) String story,
						   @RequestParam(value ="headline", required = false) String headline,
						   @RequestParam(value ="datetime", required = false) String unparsedTimestamp,
						   @RequestParam(value ="youtubeId", required = false) String youtubeId,
						   @RequestParam(value ="fixtureId", required = false) Long fixtureId) {
		LocalDateTime timeStamp = dateHelper.parseStandardDateTimeFormat(unparsedTimestamp);

		if (isNull(timeStamp)) {
			if (isNotBlank(unparsedTimestamp)){
				model.addAttribute(RESPONSE_STATUS, NewsResponseStatus.builder().action(EDIT).success(false).build());
				model.addAttribute(ERROR_MESSAGE, unparsedTimestamp + " is not a valid date. expected format is yyyy-mm-dd hh:MM:ss");
				return listNewsItems(model, month, year);
			}
			timeStamp = dateHelper.getLocalDateTimeOfNow();
		}

		NewsItem item = manager.editNewsItem(id, headline, story, timeStamp, youtubeId, fixtureId);
		model.addAttribute(RESPONSE_STATUS, NewsResponseStatus.builder().action(EDIT).success(true).build());
		model.addAttribute(NEWS_ITEM, item);
		return listNewsItems(model, month, year);
	}
}
