package com.herenpeng.rpc.common;

import com.herenpeng.rpc.kit.CollectionKit;
import com.herenpeng.rpc.kit.DateKit;
import lombok.*;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author herenpeng
 * @since 2023-06-13 20:11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class RpcServerMonitor implements Serializable {

    /**
     * 服务启动时间（毫秒）
     */
    private long startUpTime;

    private final Map<String, ServerMonitorInfo> serverMonitorMap = new ConcurrentHashMap<>();


    public void addRequest(String clientIp, int cmd) {
        ServerMonitorInfo serverMonitorInfo = serverMonitorMap.computeIfAbsent(clientIp, key -> new ServerMonitorInfo());
        serverMonitorInfo.addRequest(cmd);
    }

    public void addSuccess(String clientIp, int cmd, long useTime) {
        ServerMonitorInfo serverMonitorInfo = serverMonitorMap.computeIfAbsent(clientIp, key -> new ServerMonitorInfo());
        serverMonitorInfo.addSuccess(cmd, useTime);
    }

    public void addFail(String clientIp, int cmd) {
        ServerMonitorInfo serverMonitorInfo = serverMonitorMap.computeIfAbsent(clientIp, key -> new ServerMonitorInfo());
        serverMonitorInfo.addFail(cmd);
    }

    @Data
    public static class ServerMonitorInfo {

        private Map<Integer, MethodMonitorInfo> methodMonitor = new ConcurrentHashMap<>();
        private long useTimeStart = 0;
        private Deque<MinuteMonitorInfo> minuteMonitor = new ArrayDeque<>();


        public long requestNum() {
            return CollectionKit.sum(methodMonitor.values(), MethodMonitorInfo::getRequest);
        }

        public long successNum() {
            return CollectionKit.sum(methodMonitor.values(), MethodMonitorInfo::getSuccess);
        }

        public long failNum() {
            return CollectionKit.sum(methodMonitor.values(), MethodMonitorInfo::getFail);
        }

        public long useTime() {
            return CollectionKit.sum(methodMonitor.values(), MethodMonitorInfo::getUseTime);
        }


        public void addRequest(int cmd) {
            MethodMonitorInfo methodMonitorInfo = methodMonitor.computeIfAbsent(cmd, key -> new MethodMonitorInfo());
            methodMonitorInfo.setRequest(methodMonitorInfo.getRequest() + 1);
        }

        public void addSuccess(int cmd, long useTime) {
            MethodMonitorInfo methodMonitorInfo = methodMonitor.computeIfAbsent(cmd, key -> new MethodMonitorInfo());
            methodMonitorInfo.setSuccess(methodMonitorInfo.getSuccess() + 1);
            methodMonitorInfo.setUseTimeMax(Math.max(methodMonitorInfo.getUseTimeMax(), useTime));
            methodMonitorInfo.setUseTime(methodMonitorInfo.getUseTime() + useTime);
            addMinuteMonitorInfo(useTime);
        }

        private synchronized void  addMinuteMonitorInfo(long useTime) {
                long minuteStart = DateKit.getMinuteStart();
                if (useTimeStart == 0) {
                    useTimeStart = minuteStart;
                }
                if ((minuteStart - useTimeStart) / DateKit.ONE_MINUTE >= minuteMonitor.size()) {
                    minuteMonitor.add(new MinuteMonitorInfo(minuteStart, 1, useTime));
                    if (minuteMonitor.size() > 60) {
                        // 只统计最近一个小时的数据
                        minuteMonitor.pollFirst();
                    }
                    return;
                }
                MinuteMonitorInfo monitorInfo = minuteMonitor.peekLast();
                if (monitorInfo != null) {
                    monitorInfo.setNum(monitorInfo.getNum() + 1);
                    monitorInfo.setUseTime(monitorInfo.getUseTime() + useTime);
                }
        }

        public void addFail(int cmd) {
            MethodMonitorInfo methodMonitorInfo = methodMonitor.computeIfAbsent(cmd, key -> new MethodMonitorInfo());
            methodMonitorInfo.setFail(methodMonitorInfo.getFail() + 1);
        }


    }


    @Getter
    @Setter
    public static class MethodMonitorInfo {
        private long request;
        private long success;
        private long fail;
        private long useTimeMax;
        private long useTime;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MinuteMonitorInfo {
        private long minute;
        private int num;
        private long useTime;
    }


}
