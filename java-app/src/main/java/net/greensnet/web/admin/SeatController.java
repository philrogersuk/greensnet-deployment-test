/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web.admin;

import net.greensnet.domain.SeatType;
import net.greensnet.service.SponsorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("AdminSeatController")
public class SeatController {

	private static final String PAGE_TITLE = "page_title";
	private static final String SEAT_TYPE_LIST = "seat_type_list";
	private static final String SEATS = "seats";
	private static final String SEAT = "seat";


	private final SponsorService sponsorService;

	public SeatController(SponsorService sponsorService) {
		this.sponsorService = sponsorService;
	}

	@RequestMapping("AdminStand")
	public String handleRequest(Model model,
								@RequestParam(value = "method", required = false) String method,
								@RequestParam(value = "id", required = false) Long id,
								@RequestParam(value = "seatId", required = false) Long seatId,
								@RequestParam(value = "name", required = false) String name,
								@RequestParam(value = "type", required = false) Long typeId,
								@RequestParam(value = "row", defaultValue = "-1") Integer row,
								@RequestParam(value = "column", defaultValue = "-1") Integer column) {
		if (null == method || "list".equals(method)) {
			model.addAttribute(SEAT_TYPE_LIST, sponsorService.getAllSeatTypes());
			model.addAttribute(SEATS, sponsorService.getSeatsInGrid());
		} else if ("edit".equals(method)) {
			model.addAttribute(SEAT_TYPE_LIST, sponsorService.getAllSeatTypes());
			model.addAttribute(SEAT, sponsorService.getSeatById(id));
		} else if ("add".equals(method) || "editSubmit".equals(method)) {
			SeatType type = sponsorService.getSeatTypeById(typeId);

			if ("add".equals(method)) {
				sponsorService.createSeat(name, type, row, column);
			} else {
				sponsorService.editSeat(seatId, name, type);
			}
			model.addAttribute(SEAT_TYPE_LIST, sponsorService.getAllSeatTypes());
			model.addAttribute(SEATS, sponsorService.getSeatsInGrid());
		}
		model.addAttribute(PAGE_TITLE, "Seat Sponsorship");

		return "th_admin/seats";
	}
}
