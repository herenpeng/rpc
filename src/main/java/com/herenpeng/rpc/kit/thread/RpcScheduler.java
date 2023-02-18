package com.herenpeng.rpc.kit.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @author herenpeng
 */
@Slf4j
public class RpcScheduler {

    private static final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor(
            new RpcThreadFactory(RpcScheduler.class.getSimpleName()));

    /**
     * @param task   定时循环任务，指定时间后执行，指定间隔时间后循环
     * @param delay  首次执行的延迟时间，单位默认为毫秒
     * @param period 循环执行的间隔时间，单位默认为毫秒
     */
    public static void doLoopTask(Runnable task, long delay, long period) {
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(task, delay, period, TimeUnit.MILLISECONDS);
    }

    /**
     * @param task     定时循环任务，指定时间后执行，指定间隔时间后循环
     * @param delay    首次执行的延迟时间
     * @param period   循环执行的间隔时间
     * @param timeUnit 时间单位
     */
    public static void doLoopTask(Runnable task, long delay, long period, TimeUnit timeUnit) {
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(task, delay, period, timeUnit);
    }

    /**
     * @param task  定时任务，指定时间后执行
     * @param delay 首次执行的延迟时间，单位默认为毫秒
     */
    public static void doTask(Runnable task, long delay) {
        service.schedule(task, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * @param task     定时任务，指定时间后执行
     * @param delay    首次执行的延迟时间，单位默认为毫秒
     * @param timeUnit 时间单位
     */
    public static void doTask(Runnable task, long delay, TimeUnit timeUnit) {
        service.schedule(task, delay, timeUnit);
    }


}
