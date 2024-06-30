package com.ssuk.global.config;

import com.ssuk.global.util.TestDataUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
public class TestDataConfig {

    @Autowired
    private TestDataUtil testDataUtil;

    @PostConstruct
    public void init() {
        this.testDataUtil.createTestMember();
    }
}
