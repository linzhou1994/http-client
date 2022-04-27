package com.http.client.tool.date;



import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 功能描述：
 *
 * @Auther: linzhou
 * @Date: 2021/3/17 22:15
 * @Description:
 */
public class DateUtil {

    private final static SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat SECOND = getFormat("yyyy-MM-dd HH:mm:ss");



    /**
     * 获得今年的开始时间
     *
     * @return
     */
    public static Date getCurrentYearStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.MONTH, 0);
        todayStart.set(Calendar.DATE, 1);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取前/后半年的开始时间
     *
     * @return
     */
    public static Date getHalfYearStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        if (currentMonth >= 1 && currentMonth <= 6) {
            c.set(Calendar.MONTH, 0);
        } else if (currentMonth >= 7 && currentMonth <= 12) {
            c.set(Calendar.MONTH, 6);
        }
        c.set(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();

    }

    /**
     * 当前季度的开始时间
     *
     * @return
     */
    public static Date getCurrentQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        if (currentMonth >= 1 && currentMonth <= 3) {
            c.set(Calendar.MONTH, 0);
        } else if (currentMonth >= 4 && currentMonth <= 6) {
            c.set(Calendar.MONTH, 3);
        } else if (currentMonth >= 7 && currentMonth <= 9) {
            c.set(Calendar.MONTH, 4);
        } else if (currentMonth >= 10 && currentMonth <= 12) {
            c.set(Calendar.MONTH, 9);
        }
        c.set(Calendar.DATE, 1);

        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    /**
     * 获得本月的开始时间
     *
     * @return
     */
    public static Date getCurrentMonthStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.DATE, 1);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }


    /**
     * 获取本周第一天
     *
     * @return
     */
    public static Date getWeekStart() {
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        int weekday = c.get(Calendar.DAY_OF_WEEK);
        if (weekday == 1) {
            return getDayStartTime(daysAddOrSub(today, -6));
        } else {
            return getDayStartTime(daysAddOrSub(today, 2 - weekday));
        }
    }

    /**
     * 获取整点时间
     */
    public static Date getHourTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取每天开始时间
     */
    public static Date getDayTime(int offSetDay) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.add(Calendar.DATE, offSetDay);
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取每天结束时间
     */
    public static Date getEndTime(int offSetDay) {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.add(Calendar.DATE, offSetDay);
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }
    /**
     * 获取00:00:00时间点
     *
     * @param date
     * @return Date 年月日不变，时分秒改为当天的00:00:00.000
     */
    public static Date getDayStartTime(Date date) {
        if (null == date) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    /**
     * method : 24、把date的天数增加offset
     */
    public static Date daysAddOrSub(Date date, int offset) {
        return addOrSub(date, Calendar.DAY_OF_MONTH, offset);
    }

    private static Date addOrSub(Date date, int type, int offset) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(type, cal.get(type) + offset);
        return cal.getTime();
    }

    /**
     * 获取当前时间戳
     * @return
     */
    public static Integer getCurrentTime(){
        return new Long(System.currentTimeMillis() / 1000).intValue();
    }

    /**
     * method : 9、根据Date获取"yyyy-MM-dd HH:mm:ss"字符串
     */
    public static String getSecondStr(Date date) {
        synchronized (SECOND) {
            return getStr(date, SECOND);
        }
    }

    private static String getStr(Date date, SimpleDateFormat format) {
        if (date == null) {
            return "";
        }
        return format.format(date);
    }

    private static SimpleDateFormat getFormat(String format) {
        return new SimpleDateFormat(format);
    }


    public static void main(String[] args) {
        System.out.println(getSecondStr(getHourTime()));
        System.out.println(getSecondStr(getDayTime(0)));
        System.out.println(getSecondStr(getDayTime(-1)));
        System.out.println(getSecondStr(getCurrentMonthStartTime()));
        System.out.println(getSecondStr(getCurrentQuarterStartTime()));
        System.out.println(getSecondStr(getHalfYearStartTime()));
        System.out.println(getSecondStr(getCurrentYearStartTime()));
    }
}
