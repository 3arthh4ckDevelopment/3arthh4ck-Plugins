package me.earth.crystalauraplugin.module.util;

import me.earth.earthhack.impl.Earthhack;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BooleanSupplier;

public class TaskThread
        extends Thread {
    private final BlockingQueue<Runnable> tasks = new LinkedBlockingQueue<Runnable>();
    private final BooleanSupplier condition;

    public TaskThread(String name) {
        this(name, Earthhack::isRunning);
    }

    public TaskThread(String name, BooleanSupplier condition) {
        super(name);
        this.setDaemon(true);
        this.condition = condition;
    }

    @Override
    public void run() {
        while (this.condition.getAsBoolean()) {
            Runnable runnable = null;
            try {
                runnable = this.tasks.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (runnable == null) continue;
            try {
                runnable.run();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void submit(Runnable runnable) {
        this.tasks.add(runnable);
    }
}

