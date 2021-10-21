package cn.sinso.DICOMNetwork.util;

import lombok.Data;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * LocalDateUtil
 *
 * @author : alibeibei
 * @date : 2020/08/13 12:03
 */
public class LocalDateUtils {


    /**
     * LocalDate转Date
     *
     * @param localDate
     * @return
     */
    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant());
    }

    /**
     * LocalDateTime转Date
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneOffset.ofHours(8)).toInstant());
    }

    /**
     * Date转LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        return localDateTime;
    }

    /**
     * LocalDate转时间戳
     *
     * @param localDate
     * @return
     */
    public static Long localDateToTimestamp(LocalDate localDate) {
        long timestamp = localDate.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
        return timestamp;
    }

    /**
     * LocalDateTime转时间戳
     *
     * @param localDateTime
     * @return
     */
    public static Long localDateTimeToTimestamp(LocalDateTime localDateTime) {
        long timestamp = localDateTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
        return timestamp;
    }

    /**
     * 时间戳转LocalDateTime
     *
     * @param timestamp
     * @return
     */
    public static LocalDateTime timestampToLocalDateTime(Long timestamp) {
        LocalDateTime localDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        return localDateTime;
    }

    /**
     * 时间戳转LocalDate
     *
     * @param timestamp
     * @return
     */
    public static LocalDate timestampToLocalDate(Long timestamp) {
        LocalDate localDate = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDate();
        return localDate;
    }

    /**
     * 时间戳转Date（当日零点）
     *
     * @param timestamp
     * @return
     */
    public static Date timestampToMinOfTheDate(Long timestamp) {
        return localDateTimeToDate(LocalDateTime.of(timestampToLocalDate(timestamp), LocalTime.MIN));
    }

    /**
     * 时间戳转Date（当日最后时刻）
     *
     * @param timestamp
     * @return
     **/
    public static Date timestampToMaxOfTheDate(Long timestamp) {
        return localDateTimeToDate(LocalDateTime.of(timestampToLocalDate(timestamp), LocalTime.MAX));
    }

    /**
     * 根据当前时间获取今日结束时间
     *
     * @param now 当前时间
     * @return
     */
    public static LocalDateTime todayMaxWithNow(LocalDateTime now) {
        return LocalDateTime.of(now.toLocalDate(), LocalTime.MAX);
    }

    /**
     * 今日开始时间和结束时间
     *
     * @param today 今日
     * @return
     */
    public static CustomLocalDateTime todayMinAndMax(LocalDate today) {
        CustomLocalDateTime localDateTime = new CustomLocalDateTime();
        localDateTime.setMin(LocalDateTime.of(today, LocalTime.MIN));
        localDateTime.setMax(LocalDateTime.of(today, LocalTime.MAX));
        return localDateTime;
    }

    /**
     * 根据今天获取当月开始时间和结束时间
     *
     * @param today 今日
     * @return
     */
    public static CustomLocalDateTime thisMonthMinAndMax(LocalDate today) {
        CustomLocalDateTime localDateTime = new CustomLocalDateTime();
        localDateTime.setMin(LocalDateTime.of(today.with(TemporalAdjusters.firstDayOfMonth()), LocalTime.MIN));
        localDateTime.setMax(LocalDateTime.of(today.with(TemporalAdjusters.lastDayOfMonth()), LocalTime.MAX));
        return localDateTime;
    }

    @Data
    public static class CustomLocalDateTime {
        /**
         * 最小时间
         */
        private LocalDateTime min;

        /**
         * 最大时间
         */
        private LocalDateTime max;
    }
}
