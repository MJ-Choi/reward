package com.product.reward.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfig {
    @Bean
    public Clock clock() {
        // 시스템 시간을 호출
        return Clock.systemDefaultZone();
    }
}
