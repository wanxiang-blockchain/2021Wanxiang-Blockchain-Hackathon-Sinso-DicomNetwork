package cn.sinso.DICOMNetwork.util;

import lombok.Getter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * 日期格式封装
 */
public class DateUtils {

    @Getter
    public enum Format {
        //时间格式
        YYYY("yyyy", "\\d{4}"),
        YYYYMM("yyyyMM", "\\d{6}"),
        YYYYMMDD("yyyyMMdd", "\\d{8}"),
        YYYYMMDDHH("yyyyMMddHH", "\\d{10}"),
        YYYYMMDDHHMM("yyyyMMddHHmm", "\\d{12}"),
        YYYYMMDDHHMMSS("yyyyMMddHHmmss", "\\d{14}"),
        YYMMDDHHMM("yyMMddHHmm", "\\d{10}"),
        YYYY_MM_DD("yyyy-MM-dd", "\\d{4}-\\d{2}-\\d{2}"),
        YYYY_MM("yyyy-MM", "\\d{4}-\\d{2}"),
        YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm", "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}"),
        YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss", "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"),
        HH_MM_SS("HH:mm:ss", "\\d{2}:\\d{2}:\\d{2}"),
        HHMMSS("HHmmss", "\\d{6}"),
        YYYY_MM_DD_MIN("yyyy-MM-dd 00:00:00", "\\d{2}:\\d{2}:\\d{2} \\d{2}:\\d{2}:\\d{2}"),
        YYYY_MM_DD_MAX("yyyy-MM-dd 23:59:59", "\\d{2}:\\d{2}:\\d{2} \\d{2}:\\d{2}:\\d{2}");
        private String format;
        private String reg;

        Format(String format, String reg) {
            this.format = format;
            this.reg = reg;
        }
    }

    /**
     * 一天
     */
    public static final long ONE_DAY_MIL = 24 * 60 * 60 * 1000;


    /**
     * 严格检验时间格式
     */
    public static boolean checkDateStr(String dateStr, Format format) {
        try {
            convertByStr(dateStr, format);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 根据字符串获取日期
     *
     * @param dateStr
     * @param format
     */
    public static Date convertByStr(String dateStr, Format format) {
        String reg = format.getReg();
        boolean valid = dateStr.matches(reg);
        if (!valid) {
            throw new RuntimeException("时间类型转换错误");
        }
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(format.getFormat()));
        ZonedDateTime zdt = localDateTime.atZone(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * 根据日期获取字符串
     */
    public static String convertByDate(Date date, Format format) {
        LocalDateTime localDateTime = LocalDateUtils.dateToLocalDateTime(date);
        String str = localDateTime.format(DateTimeFormatter.ofPattern(format.getFormat()));
        return str;
    }


    /**
     * 获取当前时间
     *
     * @return
     */
    public static Integer getTimeDayByTimestamp(Long timestamp) {
        LocalDateTime localDateTime = LocalDateUtils.timestampToLocalDateTime(timestamp);
        String time = localDateTime.format(DateTimeFormatter.ofPattern(Format.YYYYMMDD.getFormat()));
        return Integer.valueOf(time);
    }

    /**
     * 获取当前月
     *
     * @return
     */
    public static Integer getTimeMonthByTimestamp(Long timestamp) {
        LocalDateTime localDateTime = LocalDateUtils.timestampToLocalDateTime(timestamp);
        String time = localDateTime.format(DateTimeFormatter.ofPattern(Format.YYYYMM.getFormat()));
        return Integer.valueOf(time);
    }

    /**
     * 获取当日零点和最后一秒
     *
     * @param current 时间戳
     * @return 当日零点和最后一秒
     */
    public static List<Date> getDayZeroMilAndEndTimeMil(long current) {
        List<Date> dates = new ArrayList<>();
        LocalDate now = LocalDateUtils.timestampToLocalDate(current);
        LocalDateTime todayStart = LocalDateTime.of(now, LocalTime.MIN);
        LocalDateTime todayEnd = LocalDateTime.of(now, LocalTime.MAX);
        dates.add(LocalDateUtils.localDateTimeToDate(todayStart));
        dates.add(LocalDateUtils.localDateTimeToDate(todayEnd));
        return dates;
    }


    /**
     * 获取过去多少月数据
     *
     * @param beforeMonth
     * @return
     */
    public static Integer getBeforeMonth(Integer beforeMonth) {
        LocalDateTime localDateTime = LocalDateTime.now().minusMonths(beforeMonth);
        Date date = LocalDateUtils.localDateTimeToDate(localDateTime);
        Integer month = DateUtils.getTimeMonthByTimestamp(date.getTime());
        return month;
    }

    /**
     * 获取过去多少月时间
     *
     * @param beforeMonth
     * @return
     */
    public static Date getBeforeMonthFirstDate(Integer beforeMonth) {
        LocalDateTime localDateTime = LocalDateTime.now().minusMonths(beforeMonth);
        localDateTime = localDateTime.with(TemporalAdjusters.firstDayOfMonth());
        Date date = LocalDateUtils.localDateTimeToDate(localDateTime);
        LocalDate now = LocalDateUtils.timestampToLocalDate(date.getTime());
        LocalDateTime todayStart = LocalDateTime.of(now, LocalTime.MIN);
        return LocalDateUtils.localDateTimeToDate(todayStart);
    }

    /**
     * utc时间转换为本地时间
     *
     * @param utcTime
     * @return
     */
    public static Date utcToLocal(String utcTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date utcDate = null;
        try {
            utcDate = sdf.parse(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.setTimeZone(TimeZone.getDefault());
        Date localDate = null;
        String localTime = sdf.format(utcDate.getTime());
        try {
            localDate = sdf.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return localDate;
    }

    /**
     * 获取过去多少月时间
     *
     * @param beforeMonth
     * @return
     */
    public static Date getBeforeMonthLastDate(Integer beforeMonth) {
        LocalDateTime localDateTime = LocalDateTime.now().minusMonths(beforeMonth);
        localDateTime = localDateTime.with(TemporalAdjusters.lastDayOfMonth());
        Date date = LocalDateUtils.localDateTimeToDate(localDateTime);
        LocalDate now = LocalDateUtils.timestampToLocalDate(date.getTime());
        LocalDateTime todayStart = LocalDateTime.of(now, LocalTime.MAX);
        return LocalDateUtils.localDateTimeToDate(todayStart);
    }

    public static void main(String[] args) {
        long timestamp = System.currentTimeMillis();
        String s = DateUtils.convertByDate(new Date(timestamp), Format.YYYYMMDDHHMM);
        System.out.println(s);
    }
}