package com.herenpeng.rpc.common;

import lombok.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author herenpeng
 * @since 2023-06-13 20:11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcServerMonitorData implements Serializable {

    /**
     * 服务启动时间（毫秒）
     */
    private long startUpTime;

    private final Map<String, PerformanceData> performanceMap = new HashMap<>();


    public void addRequest(String clientIp, int cmd) {
        PerformanceData performanceData = performanceMap.computeIfAbsent(clientIp, key -> new PerformanceData());
        addNum(cmd, performanceData.getRequestMap());
    }

    public void addSuccess(String clientIp, int cmd) {
        PerformanceData performanceData = performanceMap.computeIfAbsent(clientIp, key -> new PerformanceData());
        addNum(cmd, performanceData.getSuccessMap());
    }

    public void addFail(String clientIp, int cmd) {
        PerformanceData performanceData = performanceMap.computeIfAbsent(clientIp, key -> new PerformanceData());
        addNum(cmd, performanceData.getFailMap());
    }


    private void addNum(int cmd, Map<Integer, Long> map) {
        Long num = map.getOrDefault(cmd, 0L);
        map.put(cmd, num + 1);
    }


    @Data
    public static class PerformanceData {

        private final Map<Integer, Long> requestMap = new HashMap<>();
        private final Map<Integer, Long> successMap = new HashMap<>();
        private final Map<Integer, Long> failMap = new HashMap<>();

        public long requestNum() {
            return requestMap.values().stream().mapToLong(Long::longValue).sum();
        }

        public long successNum() {
            return successMap.values().stream().mapToLong(Long::longValue).sum();
        }

        public long failNum() {
            return failMap.values().stream().mapToLong(Long::longValue).sum();
        }

    }


}
