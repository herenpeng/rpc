package com.herenpeng.rpc.kit;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateKit {

    private static final long ONE_SECOND = 1000L;
    private static final long ONE_MINUTE = 60 * ONE_SECOND;
    private static final long ONE_HOUR = 60 * ONE_MINUTE;
    private static final long ONE_DAY = 24 * ONE_HOUR;


    /**
     * 获取当前时间的方法统一使用该接口，禁止在其他地方使用 System.currentTimeMillis() 获取时间
     */
    public static long now() {
        return System.currentTimeMillis();
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
