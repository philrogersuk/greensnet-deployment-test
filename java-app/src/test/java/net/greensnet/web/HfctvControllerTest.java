/*
 * Copyright (c) 2019, Phil Rogers
 * All Rights Reserved
 */
package net.greensnet.web;

import net.greensnet.service.hfctv.HfctvService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class HfctvControllerTest {
    private static final String EXPECTED_VIEW_NAME = "th_hfctv";
    private static final String EXPECTED_PAGE_NAME = "HFCTV";

    private HfctvController controller;
    @Mock
    private HfctvService hfctvService;
    private Model model;

    @BeforeEach
    public void setup() {
        controller = new HfctvController(hfctvService);
        model = new ExtendedModelMap();
    }

    @Test
    public void testNullURL() {

        String viewName = controller.viewLatest(model);

        assertEquals(EXPECTED_VIEW_NAME, viewName);

        assertEquals(EXPECTED_PAGE_NAME, model.asMap().get("page_title"));
        assertNull(model.asMap().get("page_content"));
    }
}
