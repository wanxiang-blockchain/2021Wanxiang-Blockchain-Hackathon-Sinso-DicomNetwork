package cn.sinso.DICOMNetwork.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @version V4.0
 * @Title: DateUtil
 * @Company: 成都影达科技有限公司
 * @Description: 日期工具类
 * @author: Lee
 * @date : 19-1-4 上午12:41
 */
public final class DateUtil {

    private static final Logger log = LoggerFactory.getLogger(DateUtil.class);
    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    private static final String YYYY_MM_DD_HH = "yyyy-MM-dd HH";
    private static final String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * 获取今天0点0分0秒的时间
     *
     * @return
     */
    public static Date getZoreClockThisDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        return calendar.getTime();
    }

    /**
     * 获取今天23点59分59秒的时间
     *
     * @return
     */
    public static Date getLastClockThisDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                23, 59, 59);
        return calendar.getTime();
    }

    /**
     * 获取yyyy-MM-dd HH:mm:ss格式时间字符串
     *
     * @param date
     * @return
     */
    public static String formatYMDHMS(Date date) {
        if (null == date) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return sdf.format(date);
    }
    /**
     * 获取yyyy-MM-dd HH:mm格式时间字符串
     *
     * @param date
     * @return
     */
    public static String formatYMDHM(Date date) {
        if (null == date) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM);
        return sdf.format(date);
    }
    /**
     * 获取yyyy-MM-dd HH格式时间字符串
     *
     * @param date
     * @return
     */
    public static String formatYMDH(Date date) {
        if (null == date) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH);
        return sdf.format(date);
    }
    /**
     * 获取yyyy-MM-dd HH:mm:ss格式时间
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date formatYMDHMS(String dateStr) {
        if (dateStr==null || "".equals(dateStr)) {
            return null;
        }
        try {
            SimpleDateFormat sdf;
            if (dateStr.length() == 10) {
                sdf = new SimpleDateFormat(YYYY_MM_DD);
            } else {
                sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
            }
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    /**
     * 获取yyyy-MM-dd格式时间字符串
     *
     * @param date
     * @return
     */
    public static String formatYMD(Date date) {
        if (null == date) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        return sdf.format(date);
    }

    public static Date formatYMD(String dateStr) {
        if (dateStr==null || "".equals(dateStr)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            log.error(e.getMessage());
            return null;
        }
    }

 public static Date getDiffDate(Integer dateDiff,int dateType) {
     Date date = new Date();
     Calendar cal = Calendar.getInstance();
     cal.setTime(date);//设置起时间
     //System.out.println("111111111::::"+cal.getTime());
     cal.add(dateType, dateDiff);
//     cal.add(Calendar.YEAR, 1);//增加一年
     //cd.add(Calendar.DATE, n);//增加一天
     //cd.add(Calendar.DATE, -10);//减10天
     //cd.add(Calendar.MONTH, n);//增加一个月
     Date time = cal.getTime();
     return time;
 }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static Timestamp getCurrentTimestamp() {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        return time;
    }

    /**
     * 获取当前时间戳
     */
    public static String getCurrentTimeString() {
        long time = Calendar.getInstance().getTimeInMillis();
        return String.valueOf(time);
    }
}
