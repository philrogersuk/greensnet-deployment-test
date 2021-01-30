/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web.admin;

import net.greensnet.domain.SnowballDraw;
import net.greensnet.service.SnowballService;
import net.greensnet.util.DateHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;

@Controller("AdminSnowballController")
public class SnowballController {

	private static final String PAGE_TITLE = "page-title";
	private static final String SNOWBALL_DRAW = "snowball_draw";
	private static final String SNOWBALL_DRAWS = "snowball_draws";

	private final SnowballService snowballService;
	private final DateHelper dateHelper;

	public SnowballController(SnowballService snowballService,
							  DateHelper dateHelper) {
		this.snowballService = snowballService;
		this.dateHelper = dateHelper;
	}

	@RequestMapping("AdminSnowball")
	public String handleRequest(Model model,
								@RequestParam(value = "method", required = false) String method,
								@RequestParam(value = "id", required = false) Long id,
								@RequestParam(value = "drawId", required = false) Long drawId,
								@RequestParam(value = "name", required = false) String name,
								@RequestParam(value = "numEntries", defaultValue = "-1") Integer numEntries,
								@RequestParam(value = "fund", defaultValue = "-1") Float fund,
								@RequestParam(value = "firstPrizeWinner", defaultValue = "-1") Integer firstPrizeWinner,
								@RequestParam(value = "firstPrize", defaultValue = "-1") Float firstPrize,
								@RequestParam(value = "secondPrizeWinner", defaultValue = "-1") Integer secondPrizeWinner,
								@RequestParam(value = "secondPrize", defaultValue = "-1") Float secondPrize,
								@RequestParam(value = "trustContribution", defaultValue = "-1") Float trustContribution,
								@RequestParam(value = "snowballContribution", defaultValue = "-1") Float snowballContribution,
								@RequestParam(value = "draw", defaultValue = "-1") String drawDate) {
		model.addAttribute(PAGE_TITLE, "Snowball");

		if (null != method) {
			if (!"edit".equals(method)) {
				List<SnowballDraw> clubs = snowballService.getDraws();
				model.addAttribute(SNOWBALL_DRAWS, clubs);
			}
			if ("edit".equals(method)) {
				model.addAttribute(SNOWBALL_DRAW, snowballService.getDraw(id));
				return "th_admin/snowball/edit";
			} else if ("add".equals(method) || "editSubmit".equals(method)) {

				LocalDateTime date = dateHelper.parseStandardDateTimeFormat(drawDate);
				if ("add".equals(method)) {
					snowballService.createDraw(date, name, numEntries, fund, firstPrizeWinner, firstPrize, secondPrizeWinner,
							secondPrize, trustContribution, snowballContribution);
				} else {
					snowballService.editDraw(drawId, date, name, numEntries, fund, firstPrizeWinner, firstPrize,
							secondPrizeWinner, secondPrize, trustContribution, snowballContribution);
				}
			}
		} else {
			List<SnowballDraw> clubs = snowballService.getDraws();
			model.addAttribute(SNOWBALL_DRAWS, clubs);
		}
		return "th_admin/snowball/list";
	}
}
