package org.example.gamelogic.multithreading;

public final class AsyncExecutor {
    private AsyncExecutor() { }

    public static void runAsync(Runnable task) {
        Thread t = new Thread(task);
        t.setDaemon(true);
        t.start();
    }
}