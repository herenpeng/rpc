package com.herenpeng.rpc.kit;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;

/**
 * @author herenpeng
 * @since 2023-02-25 10:21
 */
@Slf4j
public class Collections {

    public static boolean isEmpty(Object[] arr) {
        return arr == null || arr.length == 0;
    }

    public static <E> boolean isEmpty(Collection<E> collection) {
        return collection == null || collection.size() == 0;
    }


    public static <E> boolean isNotEmpty(Collection<E> collection) {
        return !isEmpty(collection);
    }

}
