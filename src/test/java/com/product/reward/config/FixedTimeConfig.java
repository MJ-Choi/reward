package com.product.reward.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Configuration
public class FixedTimeConfig {

    @Bean
    @Primary
    public Clock fixedClock() {
        return Clock.fixed(Instant.parse("2024-12-12T12:12:12Z"), ZoneId.of("Asia/Seoul"));
    }
}
