package com.herenpeng.rpc.internal;

import com.herenpeng.rpc.client.RpcServerProxy;
import com.herenpeng.rpc.common.RpcServerMonitor;
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
        return rpcServer.getServerMonitor();
    }

    @Override
    void handleClient(RpcServerProxy rpcServerProxy, RpcResponse response) {
        RpcServerMonitor serverMonitor = response.getReturnData(RpcServerMonitor.class);
        long startUpTime = serverMonitor.getStartUpTime();
        log.info("服务器启动时间：" + DateKit.format(startUpTime));
        long runTime = DateKit.now() - startUpTime;
        log.info("服务器已运行时间：" + DateKit.getTimeText(runTime));
        for (Map.Entry<String, RpcServerMonitor.ServerMonitorInfo> entry : serverMonitor.getServerMonitorMap().entrySet()) {
            log.info("客户端：{}", entry.getKey());
            RpcServerMonitor.ServerMonitorInfo ServerMonitorInfo = entry.getValue();
            log.info("      请求数：{}", ServerMonitorInfo.requestNum());
            log.info("      请求成功数：{}", ServerMonitorInfo.successNum());
            log.info("      请求失败数：{}", ServerMonitorInfo.failNum());
        }
    }

}
