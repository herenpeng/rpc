package com.herenpeng.rpc.common;

import com.herenpeng.rpc.kit.CollectionKit;
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
public class RpcServerMonitor implements Serializable {

    /**
     * 服务启动时间（毫秒）
     */
    private long startUpTime;

    private final Map<String, ServerMonitorInfo> serverMonitorMap = new HashMap<>();


    public void addRequest(String clientIp, int cmd) {
        ServerMonitorInfo ServerMonitorInfo = serverMonitorMap.computeIfAbsent(clientIp, key -> new ServerMonitorInfo());
        ServerMonitorInfo.addRequest(cmd);
    }

    public void addSuccess(String clientIp, int cmd, long useTime) {
        ServerMonitorInfo ServerMonitorInfo = serverMonitorMap.computeIfAbsent(clientIp, key -> new ServerMonitorInfo());
        ServerMonitorInfo.addSuccess(cmd, useTime);
    }

    public void addFail(String clientIp, int cmd) {
        ServerMonitorInfo ServerMonitorInfo = serverMonitorMap.computeIfAbsent(clientIp, key -> new ServerMonitorInfo());
        ServerMonitorInfo.addFail(cmd);
    }

    @Data
    public static class ServerMonitorInfo {

        private Map<Integer, MethodMonitorInfo> methodMonitor = new HashMap<>();

        public long requestNum() {
            return CollectionKit.sum(methodMonitor.values(), MethodMonitorInfo::getRequest);
        }

        public long successNum() {
            return CollectionKit.sum(methodMonitor.values(), MethodMonitorInfo::getSuccess);
        }

        public long failNum() {
            return CollectionKit.sum(methodMonitor.values(), MethodMonitorInfo::getFail);
        }


        public void addRequest(int cmd) {
            MethodMonitorInfo MethodMonitorInfo = methodMonitor.computeIfAbsent(cmd, key -> new MethodMonitorInfo());
            MethodMonitorInfo.setRequest(MethodMonitorInfo.getRequest() + 1);
        }

        public void addSuccess(int cmd, long useTime) {
            MethodMonitorInfo MethodMonitorInfo = methodMonitor.computeIfAbsent(cmd, key -> new MethodMonitorInfo());
            MethodMonitorInfo.setSuccess(MethodMonitorInfo.getSuccess() + 1);
            MethodMonitorInfo.setUseTimeMax(Math.max(MethodMonitorInfo.getUseTimeMax(), useTime));
        }

        public void addFail(int cmd) {
            MethodMonitorInfo MethodMonitorInfo = methodMonitor.computeIfAbsent(cmd, key -> new MethodMonitorInfo());
            MethodMonitorInfo.setFail(MethodMonitorInfo.getFail() + 1);
        }


    }


    @Getter
    @Setter
    public static class MethodMonitorInfo {
        private long request;
        private long success;
        private long fail;
        private long useTimeMax;
    }


}
