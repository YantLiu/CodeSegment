package com.pbans.app.utils;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author
 * @description LocalDateTime Date 转换类
 * @date: 2018/12/21
 */
@Slf4j
public class LocalDateTimeUtil {
    // ======================日期格式化常量=====================//
    public static final String YYYY_MM_DDHHMMSS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY = "yyyy";
    public static final String MM = "MM";
    public static final String DD = "dd";
    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    public static final String YYYYMMDDHHMMSSMS = "yyyyMMddHHmmssSSS";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String HH = "HH";
    public static final String YYYYMM = "yyyyMM";
    public static final String YYYYMMDDHHMMSS_1 = "yyyy/MM/dd HH:mm:ss";
    public static final String YYYY_MM_DD_1 = "yyyy/MM/dd";
    public static final String YYYY_MM_1 = "yyyy/MM";

    //java.util.Date --> java.time.LocalDateTime
    public static LocalDateTime date2LocalDateTime(Date date) {
        if (null == date) {
            return null;
        }
        ZonedDateTime zonedDateTime = date.toInstant().atZone(ZoneId.systemDefault());
        return zonedDateTime.toLocalDateTime();
    }

    //LocalDate --> Date
    public static Date localDate2Date(LocalDate localDate) {
        if (null == localDate) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    //LocalDate --> LocalDateTime
    public static LocalDateTime localDate2LocalDateTime(LocalDate localDate) {
        if (null == localDate) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return zonedDateTime.toLocalDateTime();
    }

    //java.time.LocalDateTime --> java.util.Date
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        if (null == localDateTime) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    //Date --> LocalDate
    public static LocalDate date2LocalDate(Date date) {
        if (null == date) {
            return null;
        }
        ZonedDateTime zonedDateTime = date.toInstant().atZone(ZoneId.systemDefault());
        return zonedDateTime.toLocalDate();
    }

    /**
     * @param localDate 日期
     * @return java.lang.String
     * @description 格式化 LocalDateTime
     * @author liuyanting
     * @date 2018/12/21
     */
    public static String formatLocalDate(LocalDate localDate, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDate.format(formatter);
    }

    /**
     * @param localDateTime 时间
     * @return java.lang.String
     * @description 格式化 LocalDateTime
     * @author liuyanting
     * @date 2018/12/21
     */
    public static String formatLocalDateTime(LocalDateTime localDateTime, String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return localDateTime.format(formatter);
    }

    /**
     * @param date    时间
     * @param pattern 正则
     * @return java.lang.String
     * @description 格式化 Date
     * @author liuyanting
     * @date 2018/12/21
     */
    public static String formatDate(Date date, String pattern) {
        LocalDateTime localDateTime = date2LocalDateTime(date);
        return formatLocalDateTime(localDateTime, pattern);
    }

    /**
     * @param str yyyy-MM-dd HH:mm:ss 格式字符串
     * @return LocalDateTime
     * @description 字符串 解析成 LocalDateTime
     * @author liuyanting
     * @date 2018/12/21
     */
    public static LocalDateTime parseLocalDateTime(String str, String pattern) {
        if (ValidateUtils.isStringEmpty(str)) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        try {
            return LocalDateTime.parse(str, formatter);
        } catch (Exception e1) {
            try {
                return LocalDate.parse(str, formatter).atStartOfDay();
            } catch (Exception e2) {
                log.error("[{}]无法解析成[{}]时间", str, pattern);
                return null;
            }
        }
    }

    /**
     * @param str yyyy-MM-dd HH:mm:ss 格式字符串
     * @return Date
     * @description 字符串 解析成 Date
     * @author liuyanting
     * @date 2018/12/21
     */
    public static Date parseDate(String str, String pattern) {
        if (ValidateUtils.isStringEmpty(str)) {
            return null;
        }
        LocalDateTime localDateTime = parseLocalDateTime(str, pattern);
        if (null == localDateTime) {
            return null;
        }
        return localDateTime2Date(localDateTime);
    }
}
