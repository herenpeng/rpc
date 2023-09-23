package com.herenpeng.rpc.internal;

import com.herenpeng.rpc.client.RpcServerProxy;
import com.herenpeng.rpc.common.RpcServerMonitorData;
import com.herenpeng.rpc.kit.DateKit;
import com.herenpeng.rpc.protocol.content.RpcResponse;
import com.herenpeng.rpc.server.RpcServer;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class MonitorInternalCmd extends InternalCmdHandler {

    @Override
    InternalCmdEnum getCmdEnum() {
        return InternalCmdEnum.MONITOR;
    }

    @Override
    Object invoke(RpcServer rpcServer) {
        return rpcServer.getMonitorData();
    }

    @Override
    void handleClient(RpcServerProxy rpcServerProxy, RpcResponse response) {
        RpcServerMonitorData monitorData = response.getReturnData(RpcServerMonitorData.class);
        long startUpTime = monitorData.getStartUpTime();
        log.info("服务器启动时间：" + DateKit.format(startUpTime));
        long runTime = DateKit.now() - startUpTime;
        log.info("服务器已运行时间：" + DateKit.getTimeText(runTime));
        for (Map.Entry<String, RpcServerMonitorData.PerformanceData> entry : monitorData.getPerformanceMap().entrySet()) {
            log.info("客户端：{}", entry.getKey());
            RpcServerMonitorData.PerformanceData data = entry.getValue();
            log.info("      请求数：{}", data.requestNum());
            log.info("      请求成功数：{}", data.successNum());
            log.info("      请求失败数：{}", data.failNum());
        }
    }

}
