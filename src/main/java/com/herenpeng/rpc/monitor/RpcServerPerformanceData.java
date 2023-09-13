package com.herenpeng.rpc.monitor;

import com.herenpeng.rpc.kit.StringUtils;
import com.herenpeng.rpc.protocol.content.RpcRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
public class RpcServerPerformanceData implements Serializable {

    /**
     * 服务启动时间（毫秒）
     */
    private long startUpTime;

    private Map<String, Long> requestMap = new HashMap<>();
    private Map<String, Long> successMap = new HashMap<>();
    private Map<String, Long> failMap = new HashMap<>();

    public long getRequestNum() {
        return requestMap.values().stream().mapToLong(Long::longValue).sum();
    }

    public long getSuccessNum() {
        return successMap.values().stream().mapToLong(Long::longValue).sum();
    }

    public long getFailNum() {
        return failMap.values().stream().mapToLong(Long::longValue).sum();
    }


    public void addRequestNum(RpcRequest<?> request) {
        addNum(request, requestMap);
    }

    public void addSuccessNum(RpcRequest<?> request) {
        addNum(request, successMap);
    }

    public void addFailNum(RpcRequest<?> request) {
        addNum(request, failMap);
    }

    private void addNum(RpcRequest<?> request, Map<String, Long> map) {
//        String key = key(request);
//        Long num = map.getOrDefault(key, 0L);
//        map.put(key, num + 1);
    }

    private String key(RpcRequest<?> request) {
//        return StringUtils.isNotEmpty(request.getMethodPath()) ? request.getMethodPath() :
//                request.getMethodLocator().toString();
        return null;
    }


}
