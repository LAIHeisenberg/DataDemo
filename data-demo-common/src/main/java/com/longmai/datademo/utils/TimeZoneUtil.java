package com.longmai.datademo.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


/**
 * 时区时间格式转化
 * @author zhenyu.guo
 * @date 2018/8/28.
 */
public class TimeZoneUtil {

    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";
    public static final String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";

    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
    public static final String yyyy_MM_dd = "yyyy-MM-dd";
    public static final String yyyyMMdd = "yyyyMMdd";
    public static final String yyyy_MM = "yyyy-MM";

    /**
     * 将系统本地时间转换成中国时间
     * @param date
     * @return
     */
    public static String localDate2ChineseDateStr(Date date) {
    	if (Objects.isNull(date)) {
    		return null;
    	}
    	return dateToStrTimeZone(date);
    }

    /**
     * 将系统本地时间转换成中国时间
     * @param date
     * @return
     */
    public static String localDate2ChineseDateStrFormat(Date date, String dateFormat) {
        if (Objects.isNull(date)) {
            return null;
        }
        return dateToStrTimeZone(date, dateFormat);
    }

    /**
     * 将系统本地时间转换成中国时间
     * @param localDate
     * @return
     */
    public static String localDate2ChineseDateStr(Date localDate, String format) {
    	if (Objects.isNull(localDate)) {
    		return null;
    	}
    	return  new DateTime(localDate, DateTimeZone.forID("+8")).toString(format);

    }

    /**
     * 将时间戳转换成中国时间
     * @param timestamp
     * @param timeUnit
     * @return
     */
    public static String timestamp2ChineseDateStr(Long timestamp, TimeUnit timeUnit) {
        return timestamp2ChineseDateStr(timestamp, timeUnit, yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 将时间戳转换成中国时间
     *
     * @param timestamp 时间戳
     * @param timeUnit
     * @param format    格式
     * @author wuqiong 2019-07-17 12:03
     */
    public static String timestamp2ChineseDateStr(Long timestamp, TimeUnit timeUnit, String format) {
        if (Objects.isNull(timestamp)) {
            return null;
        }
        Long ms = timeUnit.toMillis(timestamp);
        return new DateTime(ms, DateTimeZone.forID("+8")).toString(format);
    }

    /**
     * 获取当前毫秒值
     * 例如：20211119171159890
     * @return
     */
    public static String getCurrentMilliSecond(){
        return new DateTime(DateTime.now(), DateTimeZone.forID("+8")).toString(TimeZoneUtil.yyyyMMddHHmmssSSS);
    }



    public static void main(String[] args) {
        String currntTimeStr = TimeZoneUtil.timestamp2ChineseDateStr(Instant.now().getEpochSecond(),
                TimeUnit.SECONDS, TimeZoneUtil.yyyyMMddHHmmss);

        System.out.println("currntTimeStr = " + currntTimeStr);

        //System.out.println(timestamp2ChineseDateStr(System.currentTimeMillis(), TimeUnit.MILLISECONDS, yyyyMMdd));
        System.out.println(timestamp2ChineseDateStr(System.currentTimeMillis(), TimeUnit.MILLISECONDS, yyyy_MM_dd_HH_mm_ss));

//    	chineseDateStr2LocalDate(String)
//    	chineseDateStr2LocalDateStr(String)
//    	dateToStrTimeZone(Date, TimeZoneEnum)
//    	localDate2ChineseDateStr(Date)
//    	DateTime dateTime = new DateTime(2019, 3, 9, 10, 9, 11);
//    	System.out.println(chineseDateStr2LocalDate("2019-03-10 02:09:11"));
//    	System.out.println(chineseDateStr2LocalDateStr("2019-03-10 02:09:11"));
//    	System.out.println(dateToStrTimeZone(dateTime.toDate(), TimeZoneEnum.CHINESE));
//    	System.out.println(localDate2ChineseDateStr(dateTime.toDate()));
//    	System.err.println(timestamp2ChineseDateStr(1551628800L, TimeUnit.SECONDS));
//        System.err.println(chineseDateStr2Timestamp("2019-03-04 00:00:00", TimeUnit.SECONDS));
//        String chineseDateStr = TimeZoneUtil.localDate2ChineseDateStr(new Date(), TimeZoneUtil.yyyy_MM_dd);
//    	System.out.println(chineseDateStr);
//		Date todayMorningOfChinese = TimeZoneUtil.chineseDateStr2LocalDate("2019-06-29 00:00:00");
//		String str = new DateTime().toString("yyyy-MM-dd HH:mm:ss", Locale.US);
//        System.out.println(str);
    }
//
//    /**
//     * 将系统本地时间转换成中国时间
//     * @param date
//     * @return
//     */
//    public static Date localDate2ChineseDate(Date date) {
//        if (Objects.isNull(date)) {
//            return null;
//        }
//        return new DateTime(date.getTime(), DateTimeZone.forID(TimeZoneEnum.CHINESE.getValue())).toDate();
//    }


    /**
     * 将中国时间转换成系统本地时间
     * @param chineseDateStr: 中国时间 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date chineseDateStr2LocalDate(String chineseDateStr) {
        return chineseDateStr2LocalDate(chineseDateStr, yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 将中国时间转换成系统本地时间
     * @param chineseDateStr: 中国时间 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date chineseDateStr2LocalDate(String chineseDateStr, String format) {
        if (!StringUtils.hasText(chineseDateStr)) {
            return null;
        }
        DateTime parse = DateTime.parse(chineseDateStr, DateTimeFormat.forPattern(format).withZone(DateTimeZone.forID("+8")));
        return parse.toDate();
    }

    /**
     * 将中国时间转换成系统本地时间
     * @param chineseDateStr: 中国时间 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String chineseDateStr2LocalDateStr(String chineseDateStr) {
        if (Objects.isNull(chineseDateStr)) {
            return null;
        }
        Date localDate = TimeZoneUtil.chineseDateStr2LocalDate(chineseDateStr);
        return new DateTime(localDate).toString(yyyy_MM_dd_HH_mm_ss);
    }

    /**
     * 将中国时间转换成时间戳
     * @param chineseDateStr
     * @param timeUnit
     * @return
     */
    public static Long chineseDateStr2Timestamp(String chineseDateStr, TimeUnit timeUnit) {
        Date localDate = TimeZoneUtil.chineseDateStr2LocalDate(chineseDateStr);
        return timeUnit.convert(localDate.getTime(), TimeUnit.MILLISECONDS);
    }

    /**
     * 将中国时间转换成时间戳
     * @param chineseDateStr
     * @param timeUnit
     * @return
     */
    public static Long chineseDateStr2Timestamp(String chineseDateStr, TimeUnit timeUnit, String format) {
        Date localDate = TimeZoneUtil.chineseDateStr2LocalDate(chineseDateStr, format);
        return localDate == null ? null : timeUnit.convert(localDate.getTime(), TimeUnit.MILLISECONDS);
    }

    public static String dateToStrTimeZone(Date date) {
        return new DateTime(date, DateTimeZone.forID("+8")).toString(yyyy_MM_dd_HH_mm_ss);
    }

    public static String dateToStrTimeZone(Date date, String format) {
        return new DateTime(date, DateTimeZone.forID("+8")).toString(format);
    }


}
