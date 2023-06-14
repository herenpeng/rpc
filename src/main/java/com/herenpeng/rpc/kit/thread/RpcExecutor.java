package com.herenpeng.rpc.kit.thread;

import java.util.concurrent.*;

/**
 * @author herenpeng
 */
public class RpcExecutor {

    /**
     * 单线程池
     */
    private static final ExecutorService service = new ThreadPoolExecutor(
            3,
            5,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(1),
            new RpcThreadFactory(RpcExecutor.class.getSimpleName()));

    /**
     * 使用单线程池执行任务
     *
     * @param runnable 函数式接口
     */
    public static void doTask(Runnable runnable) {
        service.execute(runnable);
    }

}
