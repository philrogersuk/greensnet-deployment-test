/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import net.greensnet.domain.hfctv.YoutubeVideo;
import net.greensnet.exceptions.NotFoundException;
import net.greensnet.service.hfctv.HfctvService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Controller
public class HfctvController {

	private static final String VIDEOS = "videos";
	private static final String VIDEO = "video";
	private static final String PAGE_TITLE = "page_title";

	private final HfctvService hfctvService;

	public HfctvController (HfctvService hfctvService) {
		this.hfctvService = hfctvService;
	}


	@GetMapping("/HFCTV")
	public String viewLatest(Model model) {
		List<YoutubeVideo> videos = hfctvService.getLatest(20);
		model.addAttribute(VIDEOS, videos);

		model.addAttribute(PAGE_TITLE, "HFCTV");
		return "th_hfctv";
	}

	@GetMapping("/HFCTV/{id}")
	public String watchVideo(Model model,
							 @PathVariable("id") long id) {
		Optional<YoutubeVideo> video = hfctvService.getVideo(id);
		if (!video.isPresent()) {
			throw new NotFoundException();
		}

		model.addAttribute(VIDEO, video.get());

		model.addAttribute(PAGE_TITLE, "HFCTV");
		return "th_hfctv/watch";
	}
}