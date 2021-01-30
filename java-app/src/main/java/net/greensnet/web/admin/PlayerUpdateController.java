/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web.admin;

import net.greensnet.domain.StaffMember;
import net.greensnet.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;


@Controller
public class PlayerUpdateController {

	private static final String PAGE_TITLE = "page_title";
	private static final String STAFF_LIST = "staff_list";
	private static final String STAFF_INFO = "staff_info";


	private final StaffService staffService;

	@Autowired
	public PlayerUpdateController(StaffService staffService) {
		this.staffService = staffService;
	}
	
	public String handleRequest(Model model,
								@RequestParam(value = "method", required = false) String method,
								@RequestParam(value = "letter", required = false) char letter,
								@RequestParam(value = "id", required = false) long playerId,
								@RequestParam(value = "staffMemberId", required = false) long staffMember,
								@RequestParam(value = "occupation", required = false) String occupation,
								@RequestParam(value = "homeLocation", required = false) String homeLocation,
								@RequestParam(value = "stillInFootball", required = false) String stillInFootball,
								@RequestParam(value = "whereJoinedFrom", required = false) String whereJoinedFrom,
								@RequestParam(value = "otherPreviousClubs", required = false) String otherPreviousClubs,
								@RequestParam(value = "wherePlayedAfter", required = false) String wherePlayedAfter,
								@RequestParam(value = "favouriteMemories", required = false) String favouriteMemories,
								@RequestParam(value = "bestMatch", required = false) String bestMatch,
								@RequestParam(value = "bestGoal", required = false) String bestGoal,
								@RequestParam(value = "bestPlayer", required = false) String bestPlayer,
								@RequestParam(value = "id", required = false) String bestManager,
								@RequestParam(value = "favouriteGround", required = false) String favouriteGround,
								@RequestParam(value = "leastFavGround", required = false) String leastFavGround) {
		if (null != method) {
			if (method.equals("list")) {
				List<StaffMember> players = staffService.getStaffMembersByInitial(letter);
				model.addAttribute(STAFF_LIST, players);
			}
			if (method.equals("edit")) {
				StaffMember player = staffService.getStaffMemberById(playerId, true);
				model.addAttribute(STAFF_INFO, player);
			}
			if (method.equals("entry")) {
				StaffMember player = staffService.getStaffMemberById(playerId, true);
				model.addAttribute(STAFF_INFO, player);
			} else if (method.equals("add") || method.equals("editSubmit")) {
				if (method.equals("editSubmit")) {
					LocalDate timeStamp = LocalDate.now();
					staffService.createStaffUpdate(staffMember, timeStamp, occupation, homeLocation, stillInFootball,
							whereJoinedFrom, otherPreviousClubs, wherePlayedAfter, favouriteMemories, bestMatch,
							bestGoal, bestPlayer, bestManager, favouriteGround, leastFavGround);
				} else {
					staffService.editStaffUpdate(playerId, staffMember, occupation, homeLocation, stillInFootball, whereJoinedFrom,
							otherPreviousClubs, wherePlayedAfter, favouriteMemories, bestMatch, bestGoal, bestPlayer,
							bestManager, favouriteGround, leastFavGround);
				}
			}
		}

		model.addAttribute(PAGE_TITLE, "Staff Updates");
		return "admin/playerupdate";
	}
}