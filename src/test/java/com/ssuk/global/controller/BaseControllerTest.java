package com.ssuk.global.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssuk.global.annotation.IntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

@IntegrationTest
public abstract class BaseControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
