/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web.admin;

import net.greensnet.domain.StaffMember;
import net.greensnet.service.StaffService;
import net.greensnet.util.DateHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller("AdminPlayerController")
public class PlayerController {

	private static final String PAGE_TITLE = "page_title";
	private static final String STAFF_ROLE_LIST = "staff_role_list";
	private static final String STAFF_LIST = "staff_list";
	private static final String SEASON_LIST = "season_list";
	private static final String STAFF_INFO = "staff_info";

	private final StaffService staffService;
	private final DateHelper dateHelper;

	@Autowired
	public PlayerController(StaffService staffService,
							DateHelper dateHelper) {
		this.staffService = staffService;
		this.dateHelper = dateHelper;
	}

	@RequestMapping("/AdminStaff")
	public String handleRequest(Model model,
							    @RequestParam(value = "method", required = false) String method,
							    @RequestParam(value = "letter", required = false) Character letter,
							    @RequestParam(value = "id", required = false) Long playerId,
								HttpServletRequest request) {

		model.addAttribute(STAFF_ROLE_LIST, staffService.getStaffRoleValues());
		model.addAttribute(SEASON_LIST, dateHelper.getSeasonArray());
		if (null != method) {
			switch (method) {
				case "list":
					List<StaffMember> players = staffService.getStaffMembersByInitial(letter);
					model.addAttribute(STAFF_LIST, players);
					break;
				case "edit":
					StaffMember player = staffService.getStaffMemberById(playerId, true);
					model.addAttribute(STAFF_INFO, player);
					model.addAttribute(PAGE_TITLE, "Staff");
					return "th_admin/staff/edit";
				case "add":
				case "editSubmit":
					Map<String, String> playerInfo = request.getParameterMap()
							.entrySet()
							.stream()
							.collect(Collectors.toMap(Map.Entry::getKey,
									this::getValue));

					if (method.equals("add")) {
						staffService.createStaffMember(playerInfo);
					} else {
						staffService.editStaffMember(playerInfo);
					}
					break;
			}
		}

		model.addAttribute(PAGE_TITLE, "Staff");
		return "th_admin/staff";
	}

	private String getValue(Map.Entry<String, String[]> entry) {
		if (entry.getValue().length < 1) {
			return null;
		}
		return entry.getValue()[0];
	}

	@PostMapping("/AdminStaff/{id}/UpdateImage")
	public String updatePenpic(Model model,
							   @PathVariable(value = "id") long id,
							   @RequestParam(value = "penpic") MultipartFile penpic) {
		staffService.updatePenpic(id, penpic);

		return "redirect:/AdminStaff?method=edit&id=" + id;
	}


	@PostMapping("/AdminStaff/{id}/UpdateSponsorImage")
	public String updateCorporateSponsor(Model model,
										  @PathVariable(value = "id") long id,
										  @RequestParam(value = "sponsorImage") MultipartFile sponsorImage) {
		staffService.updateCorporateSponsorImage(id, sponsorImage);

		return "redirect:/AdminStaff?method=edit&id=" + id;
	}

	@PostMapping("/AdminStaff/{id}/DeleteSponsorImage")
	public String deleteCorporateSponsor(Model model,
										  @PathVariable(value = "id") long id) {
		staffService.deleteCorporateSponsorImage(id);

		return "redirect:/AdminStaff?method=edit&id=" + id;
	}

	@PostMapping("/AdminStaff/{staffId}/CreateTimeAtClub")
	public String createTimePeriod(Model model,
								   @PathVariable(value = "staffId") Long staffId,
								   @RequestParam String role,
								   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate startDate,
								   @DateTimeFormat(pattern = "yyyy-MM-dd") @RequestParam LocalDate endDate,
								   @RequestParam(defaultValue = "-1") Integer firstSeason,
								   @RequestParam(defaultValue = "-1") Integer lastSeason,
								   @RequestParam(defaultValue = "false") Boolean onLoan,
								   @RequestParam(defaultValue = "false") Boolean onTrial) {
		staffService.createTimeAtClub(staffId, role, startDate,
				endDate, firstSeason, lastSeason, onLoan, onTrial);

		return "redirect:/AdminStaff?method=edit&id=" + staffId;
	}

	@PostMapping("/AdminStaff/{staffId}/UpdateTimeAtClub/{timeAtClubId}")
	public String updateTimeAtClub(Model model,
								   @PathVariable(value = "staffId") long staffId,
								   @PathVariable(value = "timeAtClubId") long timeAtClubId,
								   @RequestParam String role,
								   @RequestParam LocalDate startDate,
								   @RequestParam LocalDate endDate,
								   @RequestParam int startSeason,
								   @RequestParam int endSeason,
								   @RequestParam boolean onLoan,
								   @RequestParam boolean onTrial) {
		staffService.updateTimeAtClub(timeAtClubId, staffId, role, startDate,
				endDate, startSeason, endSeason, onLoan, onTrial);

		return "redirect:/AdminStaff?method=edit&id=" + staffId;
	}

	@PostMapping("/AdminStaff/{staffId}/DeleteTimeAtClub/{timeAtClubId}")
	public String updateTimeAtClub(Model model,
								   @PathVariable(value = "staffId") long staffId,
								   @PathVariable(value = "timeAtClubId") long timeAtClubId) {
		staffService.deleteTimeAtClub(timeAtClubId);

		return "redirect:/AdminStaff?method=edit&id=" + staffId;
	}
}
