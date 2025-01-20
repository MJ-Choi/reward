package com.product.reward.util;

import com.product.reward.api.error.ErrorCode;
import com.product.reward.api.error.ResponseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;

@Slf4j
@Service
@AllArgsConstructor
public class DateUtils {

    private final Clock clock;

    public LocalDateTime now() {
        return LocalDateTime.now(clock);
    }

    public String getDay() {
        return date("yyyyMMdd");
    }

    public String getWeek() {
        LocalDateTime weekMon = LocalDateTime.now(clock)
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return date(weekMon, "yyyyMMdd");
    }

    public LocalDateTime getWeek(LocalDateTime startDtm) {
        return startDtm.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    public String getMonth() {
        return date("yyyyMM");
    }

    public String getYear() {
        return date("yyyy");
    }

    public String date(String format) {
        return date(LocalDateTime.now(clock), format);
    }

    public String date(LocalDateTime dateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        try {
            return dateTime.format(formatter);
        } catch (DateTimeParseException dateTimeParseException) {
            log.error("failed to convert date: {}", dateTime);
            return "";
        }
    }

    public LocalDateTime strToDt(String strDateTime) {
        // 문자열을 파싱하기 위한 DateTimeFormatter 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            return LocalDateTime.parse(strDateTime, formatter);
        } catch (DateTimeParseException dateTimeParseException) {
            log.error("Invalid String: {}", strDateTime);
            throw new ResponseException(ErrorCode.INPUT_ERROR);
        }
    }

    public static String dtToStr(LocalDateTime dateTime) {
        // 문자열을 파싱하기 위한 DateTimeFormatter 정의
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        try {
            return dateTime.format(formatter);
        } catch (DateTimeParseException dateTimeParseException) {
            log.error("failed to convert date: {}", dateTime);
            return "";
        }
    }

}