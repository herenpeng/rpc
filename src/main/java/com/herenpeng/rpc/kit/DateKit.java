package com.herenpeng.rpc.kit;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateKit {

    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = 60 * ONE_SECOND;
    public static final long ONE_HOUR = 60 * ONE_MINUTE;
    public static final long ONE_DAY = 24 * ONE_HOUR;

    private static final String TIME_FORMAT_TEMPLATE = "yyyy-MM-dd HH:mm:ss";
    private static final String MINUTE_FORMAT_TEMPLATE = "yyyy-MM-dd HH:mm";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_TEMPLATE);
    private static final SimpleDateFormat minuteSdf = new SimpleDateFormat(MINUTE_FORMAT_TEMPLATE);


    /**
     * 获取当前时间的方法统一使用该接口，禁止在其他地方使用 System.currentTimeMillis() 获取时间
     */
    public static long now() {
        return System.currentTimeMillis();
    }

    public static String format(long time) {
        return sdf.format(time);
    }

    public static String minuteFormat(long time) {
        return minuteSdf.format(time);
    }

    public static String getTimeText(long time) {
        long hourTime = time % ONE_DAY;
        long minuteTime = hourTime % ONE_HOUR;
        long secondTime = minuteTime % ONE_MINUTE;

        return time / ONE_DAY + "天" +
                hourTime / ONE_HOUR + "小时" +
                minuteTime / ONE_MINUTE + "分钟" +
                secondTime / ONE_SECOND + "秒";
    }


    public static long getMinuteStart() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTimeInMillis();
    }


}
