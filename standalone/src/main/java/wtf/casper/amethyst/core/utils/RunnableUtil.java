package wtf.casper.amethyst.core.utils;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;

public class RunnableUtil {
    private final static Timer timer = new Timer();

    public static void delay(Runnable task, long delay) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, delay);
    }

    public static void delayAsync(Runnable task, long delay) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                CompletableFuture.runAsync(task);
            }
        }, delay);
    }

    public static void repeat(Runnable task, long initialDelay, long repeatDelay) {

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                task.run();
            }
        }, initialDelay, repeatDelay);
    }

    public static void repeat(final Runnable task, long initialDelay, long repeatDelay, final int limit) {
        timer.scheduleAtFixedRate(new TimerTask() {
            int repeats = 0;

            @Override
            public void run() {
                if (this.repeats > limit) {
                    return;
                }

                task.run();
                this.repeats++;
            }
        }, initialDelay, repeatDelay);
    }

    public static void repeatAsync(Runnable task, long initialDelay, long repeatDelay) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                CompletableFuture.runAsync(task);
            }
        }, initialDelay, repeatDelay);
    }

    public static void repeatAsync(final Runnable task, long initialDelay, long repeatDelay, final int limit) {
        timer.scheduleAtFixedRate(new TimerTask() {
            int repeats = 0;

            @Override
            public void run() {
                if (this.repeats > limit) {
                    return;
                }

                CompletableFuture.runAsync(task);
                this.repeats++;
            }
        }, initialDelay, repeatDelay);
    }
}


