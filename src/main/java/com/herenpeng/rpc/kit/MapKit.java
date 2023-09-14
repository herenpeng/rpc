package com.herenpeng.rpc.kit;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class MapKit {

    public static <K, V> Map<V, K> reverse(Map<K, V> map) {
        Map<V, K> reverseMap = new HashMap<>();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            reverseMap.put(entry.getValue(), entry.getKey());
        }
        return reverseMap;
    }

}
