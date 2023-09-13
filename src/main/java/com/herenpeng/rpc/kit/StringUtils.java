package com.herenpeng.rpc.kit;

import lombok.extern.slf4j.Slf4j;

/**
 * @author herenpeng
 */
@Slf4j
public class StringUtils {

    /**
     * 判断字符串是否为空字符串
     *
     * @param string 字符串
     * @return 为null或者为""返回true，否则返回false
     */
    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
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


    /**
     * 将指定的字符串内容，处理为 /aaa/bbb 以 / 开头，不以 / 结尾的字符串
     *
     * @param path 指定路径字符串
     * @return 以 / 开头，不以 / 结尾的字符串
     */
    public static String formatPath(String path) {
        if (isEmpty(path)) {
            return path;
        }
        path = path.startsWith("/") ? path : "/" + path;
        path = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
        return path;
    }

}
