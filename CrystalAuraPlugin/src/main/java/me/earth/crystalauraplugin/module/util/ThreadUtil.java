package me.earth.crystalauraplugin.module.util;

import me.earth.earthhack.api.util.interfaces.Globals;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ThreadUtil
        implements Globals {
    public static ScheduledExecutorService newSingleThreadDaemonExecutor() {
        return Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory());
    }

    public static ExecutorService run(Runnable runnable) {
        ScheduledExecutorService executor = ThreadUtil.newSingleThreadDaemonExecutor();
        executor.submit(runnable);
        executor.shutdown();
        return executor;
    }

    public static ExecutorService keepRunning(Runnable runnable) {
        ScheduledExecutorService executor = ThreadUtil.newSingleThreadDaemonExecutor();
        executor.submit(runnable);
        return executor;
    }

    public static ExecutorService run(Runnable runnable, long delay) {
        ScheduledExecutorService executor = ThreadUtil.newSingleThreadDaemonExecutor();
        executor.schedule(runnable, delay, TimeUnit.MILLISECONDS);
        executor.shutdown();
        return executor;
    }
}

