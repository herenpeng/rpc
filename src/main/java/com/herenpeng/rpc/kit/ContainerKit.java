package com.herenpeng.rpc.kit;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.Collections;
import java.util.function.ToLongFunction;

/**
 * @author herenpeng
 * @since 2023-02-25 10:21
 */
@Slf4j
public class ContainerKit {

    public static boolean isEmpty(Object[] arr) {
        return arr == null || arr.length == 0;
    }

    public static <E> boolean isEmpty(Collection<E> collection) {
        return collection == null || collection.isEmpty();
    }


    public static <E> boolean isNotEmpty(Collection<E> collection) {
        return !isEmpty(collection);
    }


    public static <E> long sum(Collection<E> collection, ToLongFunction<? super E> mapper) {
        return isEmpty(collection) ? 0 : collection.stream().mapToLong(mapper).sum();
    }

    public static <E> long max(Collection<E> collection, ToLongFunction<? super E> mapper) {
        return isEmpty(collection) ? 0 : collection.stream().mapToLong(mapper).max().orElse(0);
    }


}
