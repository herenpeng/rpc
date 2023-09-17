package com.herenpeng.rpc.internal;

import com.herenpeng.rpc.kit.MonitorKit;

public class MonitorInternalTest {

    public static void main(String[] args) {
        MonitorKit.monitor("127.0.0.1", 10000);
    }

}
