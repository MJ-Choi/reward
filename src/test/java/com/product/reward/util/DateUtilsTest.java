package com.product.reward.util;

import com.product.reward.config.FixedTimeConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

class DateUtilsTest {

    private DateUtils dateUtils;

    @BeforeEach
    void init() {
        FixedTimeConfig clock = new FixedTimeConfig();
        this.dateUtils = new DateUtils(clock.fixedClock()); //2024-12-12
    }

    @Test
    void getDay() {
        String day = dateUtils.getDay();
        Assertions.assertEquals("20241212", day);
    }

    @Test
    void getMonth() {
        String day = dateUtils.getMonth();
        Assertions.assertEquals("202412", day);
    }

    @Test
    void getYear() {
        String day = dateUtils.getYear();
        Assertions.assertEquals("2024", day);
    }

    @Test
    void date() {
        String day = dateUtils.date("yyyy-MM-dd");
        Assertions.assertEquals("2024-12-12", day);
    }

    @Test
    void dateWithLocalDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2024-12-31 12:34", formatter);
        String day = dateUtils.date(dateTime, "yyyyMMdd");
        Assertions.assertEquals("20241231", day);
    }
}