/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import net.greensnet.domain.*;
import net.greensnet.service.AppearanceService;
import net.greensnet.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class StaffController {

	private final StaffService staffService;
	private final AppearanceService appearanceService;

	private static final String TOTAL_APPS = "total_apps";
	private static final String TOTAL_GOALS = "total_goals";
	private static final String STAFF_PERIODS = "staff_periods";
	private static final String DEBUT = "debut";
	private static final String PLAYER_APPEARANCES = "player_appearances";
	private static final String PAGE_TITLE = "page_title";
	private static final String ARCHIVE_LETTER = "archive_letter";
	private static final String STAFF_INFO = "staff_info";
	private static final String STAFF_UPDATE = "staff_update";
	private static final String STAFF_UPDATE_LIST = "staff_update_list";
	private static final String STAFF_LIST = "staff_list";

	@Autowired
	public StaffController(StaffService staffService,
						   AppearanceService appearanceService) {
		this.staffService = staffService;
		this.appearanceService = appearanceService;
	}

	@RequestMapping("/Squad")
	public String getSquad(Model model,
						   @RequestParam(value = "method", required = false) String method,
						   @RequestParam(value = "id", required = false) Long id,
						   @RequestParam(value = "letter", required = false) Character character) {
		if (null != method) {
			if ("wherearetheynow".equals(method) && null != id) {
				StaffMember person = staffService.getStaffMember(id);
				StaffUpdate update = staffService.getStaffUpdateByStaffId(id);

				model.addAttribute(STAFF_INFO, person);
				model.addAttribute(STAFF_UPDATE, update);
				model.addAttribute(PAGE_TITLE,
						"Where Are They Now - " + person.getFirstName() + " " + person.getLastName());
			} else if ("wherearetheynow".equals(method)) {
				model.addAttribute(STAFF_UPDATE_LIST, staffService.getStaffMembersWithUpdate());
				model.addAttribute(PAGE_TITLE, "Where Are They Now");
			} else if ("view".equals(method)) {
				StaffMember person = staffService.getStaffMember(id);
				Fixture debut = staffService.getDebutById(id);

				List<PlayerAppearanceRow> playerApps = appearanceService.getPlayerApps(person);

				List<TimeAtClub> staffTime = staffService.getStaffPeriods(person);

				model.addAttribute(DEBUT, debut);
				if (null != playerApps && !playerApps.isEmpty()) {
					model.addAttribute(PLAYER_APPEARANCES, playerApps);
					model.addAttribute(TOTAL_APPS, playerApps.stream()
							.mapToInt(PlayerAppearanceRow::getTotalApps)
							.sum());
					model.addAttribute(TOTAL_GOALS, playerApps.stream()
							.mapToInt(PlayerAppearanceRow::getTotalGoals)
							.sum());
				} else {
					model.addAttribute(TOTAL_APPS, 0);
					model.addAttribute(TOTAL_GOALS, 0);
				}
				if (null != staffTime && !staffTime.isEmpty()) {
					model.addAttribute(STAFF_PERIODS, staffTime);
				}
				model.addAttribute(STAFF_INFO, person);
				model.addAttribute(ARCHIVE_LETTER, person.getLastName().charAt(0));
				if (person.isAtClub()) {
					model.addAttribute(PAGE_TITLE,
							"Squad - " + person.getFirstName() + " " + person.getLastName());
				} else {
					model.addAttribute(PAGE_TITLE,
							"Former Staff - " + person.getFirstName() + " " + person.getLastName());
				}
			} else if ("archive".equals(method)) {
				char letter;
				if (null != character) {
					letter = character;
				} else {
					letter = 'A';
				}
				model.addAttribute(STAFF_LIST, staffService.getStaffMembersByInitial(letter));
				model.addAttribute(PAGE_TITLE, "Former Staff - " + letter);
			}
		} else {
			model.addAttribute(STAFF_LIST, staffService.getCurrentSquadList());
			model.addAttribute(PAGE_TITLE, "Squad");
		}

		return "th_squad";
	}
}
