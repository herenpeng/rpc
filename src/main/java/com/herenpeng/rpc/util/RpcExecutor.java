package com.herenpeng.rpc.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author herenpeng
 */
public class RpcExecutor {

    // 单线程池
    private static final ExecutorService service = Executors.newSingleThreadExecutor();

    // 使用单线程池执行任务
    public static void doTask(Runnable runnable) {
        service.execute(runnable);
    }

}
