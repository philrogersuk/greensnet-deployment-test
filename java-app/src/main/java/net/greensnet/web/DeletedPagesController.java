/*
 * Copyright (c) 2007, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DeletedPagesController {

	@RequestMapping(value = {"/SupportersTeam",
							 "/EBooks"})
	public ResponseEntity<?> handleRequest() {
		return new ResponseEntity<>(HttpStatus.GONE);
	}
}
