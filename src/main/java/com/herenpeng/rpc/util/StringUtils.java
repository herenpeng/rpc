package com.herenpeng.rpc.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author herenpeng
 */
public class StringUtils {

    private static final Logger logger = LoggerFactory.getLogger(StringUtils.class);

    /**
     * 判断字符串是否为空字符串
     *
     * @param string 字符串
     * @return 为null或者为""返回true，否则返回false
     */
    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }

    /**
     * 判断字符串是否为不空字符串
     *
     * @param string 字符串
     * @return 为null或者为""返回false，否则返回true
     */
    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }

}
