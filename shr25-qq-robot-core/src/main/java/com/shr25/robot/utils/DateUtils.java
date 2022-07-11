package com.shr25.robot.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 
 * 时间操作类<br>
 *     
 * 创建人：huobing   <br>
 * 创建时间：2018/5/8/008 17:42    <br>
 * @version   V1.0      
 */
@Slf4j
public class DateUtils {
    /**
     * 取某年某月的最后一天
     *
     * @param date
     * @return
     */
    public static String getYearMonth(Date date) {

        try {
            return  dateFormat(date,"yyyyMM");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 根据Date 得到对应的Calendar
     *
     * @param date
     * @return @
     */
    public static Calendar getCalendarFromDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c;
    }

    /**
     * 得到当前系统的年份
     */
    public static int getSysYear() {
        Calendar calendar = new GregorianCalendar();
        int iyear = calendar.get(1);
        return iyear;
    }

    /**
     * 得到当前系统的月份
     */
    public static int getSysMonth() {
        Calendar calendar = new GregorianCalendar();
        int imonth = calendar.get(2) + 1;
        return imonth;
    }


    /**
     * 得到当前系统的天
     */
    public static int getSysDay() {
        Calendar calendar = new GregorianCalendar();
        int idate = calendar.get(5);
        return idate;
    }

    public static int getDays(String yearMonth) {
        int[] days = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
        int year = Integer.parseInt(yearMonth.substring(0, 4));
        int month = Integer.parseInt(yearMonth.substring(4)) - 1;
        if (month == 1) {
            if (year % 4 == 0) {
                if (year % 100 == 0) {
                    return 28;
                }
                return 29;
            }

            return 28;
        }

        return days[month];
    }

    public static Date addYMD(Date date, int yearNum,
                              int monthNum, int dayNum) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(1, yearNum);
        c.add(2, monthNum);
        c.add(5, dayNum);
        return c.getTime();
    }

    public static String getCurrentTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);

    }

    /**
     * 取某年某月的第一天
     *
     * @param month
     * @return
     */
    public static Date getFirstDayOfMonth(String year, String month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * 取某年某月的最后一天
     *
     * @param month
     * @return
     */
    public static Date getLastDayOfMonth(String year, String month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar.getTime();
    }

    /**
     * 获取当天的开始时间
     * @return
     */
    public static Date getDayStartTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取两个日期是否同一天
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isOneDay(Date date1,Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(date1);
        calendar2.setTime(date2);
        if (calendar1.get(Calendar.YEAR)!=calendar2.get(Calendar.YEAR)) {
            return false;
        }else {
            return calendar1.get(Calendar.DAY_OF_YEAR)==calendar2.get(Calendar.DAY_OF_YEAR)?true:false;
        }
    }

    /**
     * 比较两个时间,newDate大于oldDate 返回true
     * @param newDate
     * @param oldDate
     * @return
     */
    public static boolean isOut(Date newDate,Date oldDate) {
        return newDate.getTime() > oldDate.getTime();
    }

    /**
     * 获取week_day
     *
     * @param date
     * @return
     */
    public static String getWeekDay(Date date) {
        Calendar calendar1=Calendar.getInstance();
        calendar1.set(Calendar.HOUR_OF_DAY, 23);
        calendar1.set(Calendar.MINUTE, 59);
        calendar1.set(Calendar.SECOND, 59);
        calendar1.set(Calendar.MILLISECOND, 999);
        long td = calendar1.getTimeInMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
        if (date.getTime()>td) {
            return "明天";
        }else if (date.getTime()<td&&date.getTime()>td-24*3600*1000) {
            return "今天";
        }
        switch (weekDay) {
            case 1:
                return "周日";
            case 2:
                return "周一";
            case 3:
                return "周二";
            case 4:
                return "周三";
            case 5:
                return "周四";
            case 6:
                return "周五";
            case 7:
                return "周六";
            default:
                return "";
        }
    }

    /**
     * util.Date转sql.Date
     * @param date java.util.Date
     * @return
     */
    public static java.sql.Date toSqlDate(Date date){
        return new java.sql.Date(date.getTime());
    }

    /**
     * sql.Date转util.Date
     * @param date java.sql.Date
     * @return
     */
    public static Date toUtilDate(java.sql.Date date){
        return new Date(date.getTime());
    }

    /**
     * 格式化日期
     * @param calendar  java.util.Calendar类型
     * @return
     */
    public static String dateFormat(Calendar calendar){
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return dateFormat(calendar.getTime(),pattern);
    }

    /**
     * 格式化日期
     * @param date   java.sql.Date
     * @return
     */
    public static String dateFormat(java.sql.Date date) {
        String pattern = "yyyy-MM-dd";
        return dateFormat(date, pattern);
    }

    /**
     * 格式化日期
     * @param date   java.util.Date
     * @return
     */
    public static String dateFormat(Date date) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return dateFormat(date, pattern);
    }

    /**
     * 格式化日期
     * @param calendar  java.util.Calendar类型
     * @param pattern
     * @return
     */
    public static String dateFormat(Calendar calendar, String pattern){
        return dateFormat(calendar.getTime(),pattern);
    }

    /**
     * 格式化日期
     * @param date   java.sql.Date
     * @param pattern
     * @return
     */
    public static String dateFormat(java.sql.Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String demo = sdf.format(date);
        return demo;
    }

    /**
     * 格式化日期
     * @param date   java.util.Date
     * @param pattern
     * @return
     */
    public static String dateFormat(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        String demo = sdf.format(date);
        return demo;
    }

    /**
     * @Description: 返回两个时间的时间差，按天返回
     * @param currDate
     * @param oldDate
     * @return
     */
    public static Integer  timeDifference(Date currDate,Date oldDate){
        return millisecondDifference(currDate, oldDate, TimeMillisecond.DAY);
    }

    /**
     * @Description: 返回两个时间的时间差，毫秒
     * @param currDate
     * @param oldDate
     * @param millisecond   按时间差值 ，1秒1000毫米
     * @return
     */
    public static Integer  millisecondDifference(Date currDate,Date oldDate,Long millisecond){
        Long curr =  currDate.getTime();
        Long old = oldDate.getTime();
        Long tmp = (curr-old)/millisecond;
        return 	Math.abs(tmp.intValue());
    }

    public static Integer getCurrMinuteByDay(){
        Calendar calendar = Calendar.getInstance();
        Long curr =  calendar.getTimeInMillis();

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Long old =calendar.getTimeInMillis();
        Long tmp = (curr-old)/60000;

        return tmp.intValue();
    }



    /**
     * @Title: addDay
     * @Description:一个时间增加多少天
     * @param date	需要增加的时间
     * @param day	增加的天数
     * @return 返回增加之后的时间
     */
    public static Date addDay(Date date, int day){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);

        return calendar.getTime();
    }



    /**
     * @Title: addTime
     * @Description: 当前时间添加固定时长
     * @param field	单位：Calendar.SECOND，Calendar.DATE
     * @param time	增加的时长
     * @return 返回增加之后的时间
     */
    public static Date addTime(int field, int time){
        Calendar calendar = Calendar.getInstance();
        calendar.add(field, time);
        return calendar.getTime();
    }



    /**
     * @Title: addTime
     * @Description: 添加固定时长
     * @param date	需要增加的时间
     * @param field	单位：Calendar.SECOND
     * @param time	增加的时长
     * @return 返回增加之后的时间
     */
    public static Date addTime(Date date, int field, int time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, time);

        return calendar.getTime();
    }

    /**
     * 以天为单位进行上舍入<br>
     * <br>
     * 创建人：邓强   <br>
     * 创建时间：2017年12月22日 上午9:03:46    <br>
     * 修改人：  <br>
     * 修改时间：2017年12月22日 上午9:03:46   <br>
     * 修改备注：     <br>
     * @param date		需要操作的时间
     * @return
     */
    public static Date ceilDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DATE, 1);

        return calendar.getTime();
    }


    /**
     * 以天为单位进行下舍入<br>
     * <br>
     * 创建人：邓强   <br>
     * 创建时间：2017年12月22日 上午9:04:43    <br>
     * 修改人：  <br>
     * 修改时间：2017年12月22日 上午9:04:43   <br>
     * 修改备注：     <br>
     * @param date		需要操作的时间
     * @return
     */

    public static Date floorDay(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
    /**
     * 字符串转时间
     * 支持格式:
     * yyyyMMdd<br>
     * yyyyMMddHH<br>
     * yyyyMMddHHmm<br>
     * yyyyMMddHHmmss<br>
     * yyyy-MM-dd<br>
     * yyyy.MM.dd<br>
     * yyyy/MM/dd<br>
     * yyyy MM dd<br>
     * 年月日格式可以和下面的日以交换
     * yyyyMMdd HH<br>
     * yyyyMMdd HHmm<br>
     * yyyyMMdd HH mm<br>
     * yyyyMMdd HH:mm<br>
     * yyyyMMdd HH：mm<br>
     * yyyyMMdd HH mm ss<br>
     * yyyyMMdd HH:mm:ss<br>
     * yyyyMMdd HH：mm：ss<br>
     * MM/dd<br>
     * MM-dd<br>
     * MM.dd<br>
     * HH<br>
     * HHmm<br>
     * HH mm<br>
     * HH:mm<br>
     * HH：mm<br>
     * HH mm ss<br>
     * HH:mm:ss<br>
     * HH：mm：ss<br>
     * @param dateStr
     * @return
     */
    public static Date getDateFromString(String dateStr){
        dateStr = dateStr.trim();
        Pattern pat = Pattern.compile("^(([0-9]{4})([0-9]{2})([0-9]{2})\\s?([0-9]{2})([0-9]{2})([0-9]{2})|((([0-9]{4})([^0-9:]*)([0-9]{2})([^0-9:]*)([0-9]{2}))|((([0-9]{4})([^0-9:\\s]))?([0-9]{1,2})([^0-9:\\s])([0-9]{1,2})))?((T|\\s*)?([0-9]{1,2})((([:：\\s])([0-9]{1,2})(([:：\\s])?([0-9]{1,2}))?)|([0-9]{2}))?)?)$");
        Matcher mat = pat.matcher(dateStr);
        if(!mat.find()){
            return null;
        }

        if(StringUtils.isBlank(mat.group(1))){
            return null;
        }else{
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            int y,M,d,H,m,s;
            if(mat.group(2) != null || mat.group(10) != null || mat.group(17) != null){
                y = Integer.parseInt(mat.group(2) != null?mat.group(2):mat.group(10)!=null?mat.group(10):mat.group(17));
                calendar.set(Calendar.YEAR, y);
            }
            if(mat.group(3) != null || mat.group(12) != null || mat.group(19) != null){
                M = Integer.parseInt(mat.group(3) != null?mat.group(3):mat.group(12)!=null?mat.group(12):mat.group(19));
                M = M>0?M-1:0;
                calendar.set(Calendar.MONTH, M);
            }
            if(mat.group(4) != null || mat.group(14) != null || mat.group(21) != null){
                d = Integer.parseInt(mat.group(4) != null?mat.group(4):mat.group(14)!=null?mat.group(14):mat.group(21));
                calendar.set(Calendar.DAY_OF_MONTH, d);
            }
            if(mat.group(5) != null || mat.group(24) != null){
                H = Integer.parseInt(mat.group(5) != null?mat.group(5):mat.group(24));
                calendar.set(Calendar.HOUR_OF_DAY, H);
            }
            if(mat.group(6) != null || mat.group(28) != null || mat.group(32) != null){
                m = Integer.parseInt(mat.group(6) != null?mat.group(6):mat.group(28)!=null?mat.group(28):mat.group(32));
                calendar.set(Calendar.MINUTE, m);
            }
            if(mat.group(7) != null || mat.group(31) != null){
                s = Integer.parseInt(mat.group(7) != null?mat.group(7):mat.group(31));
                calendar.set(Calendar.SECOND, s);
            }
            return calendar.getTime();
        }
    }

    public static class TimeSecond{
        public static final Integer MINUTE = 60;
        public static final Integer HOUR = 60*MINUTE;
        public static final Integer DAY = 24*HOUR;
        public static final Integer THREE_DAYS = 3*DAY;
        public static final Integer WEEK = 7*DAY;
    }

    public static class TimeMillisecond{
        public static final Long SECOND = 1000L;
        public static final Long MINUTE = 60*SECOND;
        public static final Long HOUR = 60*MINUTE;
        public static final Long DAY = 24*HOUR;
        public static final Long THREE_DAYS = 3*DAY;
        public static final Long WEEK = 7*DAY;
    }
}
