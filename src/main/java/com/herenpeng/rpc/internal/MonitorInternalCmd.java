package com.herenpeng.rpc.internal;

import com.herenpeng.rpc.client.RpcServerProxy;
import com.herenpeng.rpc.common.RpcServerMonitor;
import com.herenpeng.rpc.kit.ContainerKit;
import com.herenpeng.rpc.kit.DateKit;
import com.herenpeng.rpc.protocol.content.RpcResponse;
import com.herenpeng.rpc.server.RpcServer;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.Map;

import static java.lang.System.out;

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
        out.printf("服务器启动时间：%s\n", DateKit.format(startUpTime));
        long runTime = DateKit.now() - startUpTime;
        out.printf("服务器已运行时间：%s\n", DateKit.getTimeText(runTime));
        for (Map.Entry<String, RpcServerMonitor.ServerMonitorInfo> entry : serverMonitor.getServerMonitorMap().entrySet()) {
            RpcServerMonitor.ServerMonitorInfo serverMonitorInfo = entry.getValue();
            out.printf("客户端:%s, request:%s, success:%s, fail:%s, useTime:%sms\n", entry.getKey(), serverMonitorInfo.requestNum(),
                    serverMonitorInfo.successNum(), serverMonitorInfo.failNum(), serverMonitorInfo.useTime());
            printMinuteMonitor(serverMonitorInfo.getMinuteMonitor());
        }
    }


    private static final int ROW = 4;

    private void printMinuteMonitor(Deque<RpcServerMonitor.MinuteMonitorInfo> minuteMonitor) {
        long max = ContainerKit.max(minuteMonitor, RpcServerMonitor.MinuteMonitorInfo::getUseTime);
        if (max <= 0) {
            return;
        }
        for (int i = 0; i <= ROW; i++) {
            // 打印max行
            int value = ROW - i;
            for (RpcServerMonitor.MinuteMonitorInfo entry : minuteMonitor) {
                out.print(value <= entry.getUseTime() * ROW / max ? "▓" : "░");
            }
            out.println();
        }
        RpcServerMonitor.MinuteMonitorInfo first = minuteMonitor.peekFirst();
        out.print(DateKit.minuteFormat(first.getMinute()));
        if (minuteMonitor.size() > 1) {
            print(' ', Math.max(minuteMonitor.size() - 32, 1));
            RpcServerMonitor.MinuteMonitorInfo last = minuteMonitor.peekLast();
            out.println(DateKit.minuteFormat(last.getMinute()));
        }
    }


    private void print(Character c, int num) {
        for (int i = 0; i < num; i++) {
            out.print(c);
        }
    }

}
