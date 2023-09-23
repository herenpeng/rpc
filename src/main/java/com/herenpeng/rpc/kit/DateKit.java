package com.herenpeng.rpc.kit;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public class DateKit {

    private static final long ONE_SECOND = 1000L;
    private static final long ONE_MINUTE = 60 * ONE_SECOND;
    private static final long ONE_HOUR = 60 * ONE_MINUTE;
    private static final long ONE_DAY = 24 * ONE_HOUR;

    private static final String TIME_FORMAT_TEMPLATE = "yyyy-MM-dd HH:mm:ss";
    private static final String TIME_FORMAT_TEMPLATE2 = "yyyy年MM月dd日 HH时mm分ss秒";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(TIME_FORMAT_TEMPLATE);
    private static final SimpleDateFormat sdf2 = new SimpleDateFormat(TIME_FORMAT_TEMPLATE2);


    /**
     * 获取当前时间的方法统一使用该接口，禁止在其他地方使用 System.currentTimeMillis() 获取时间
     */
    public static long now() {
        return System.currentTimeMillis();
    }

    public static String format(long time) {
        return sdf.format(new Date(time));
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

}
