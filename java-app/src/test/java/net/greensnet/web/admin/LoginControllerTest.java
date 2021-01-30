/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LoginControllerTest {
    private static final String EXPECTED_VIEW_NAME = "th_admin/login";
    private static final String EXPECTED_PAGE_NAME = "Admin - Login";

    private LoginController controller;
    private Model model;

    @BeforeEach
    public void setup() {
        controller = new LoginController();
        model = new ExtendedModelMap();
    }

    @Test
    public void testNullURL() {

        String viewName = controller.loginPage(model);

        assertEquals(EXPECTED_VIEW_NAME, viewName);

        assertEquals(EXPECTED_PAGE_NAME, model.asMap().get("page_title"));
        assertNull(model.asMap().get("page_content"));
    }
}
