package wtf.casper.amethyst.paper.scheduler;

import wtf.casper.amethyst.core.scheduler.AmethystScheduler;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.reflections.FoliaReflections;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class AmethystFoliaScheduler extends AmethystScheduler {

    private Object scheduler;

    @Override
    public AmethystScheduler run(Runnable runnable, Object subject) {
        scheduler = FoliaReflections.dummyGlobalRegionSchedulerRun(AmethystPaper.getInstance(), o -> runnable.run());
        return this;
    }

    @Override
    public AmethystScheduler runAsync(Runnable runnable, Object subject) {
        scheduler = FoliaReflections.dummyAsyncSchedulerRunNow(AmethystPaper.getInstance(), o -> runnable.run());
        return this;
    }

    @Override
    public AmethystScheduler runLater(Runnable runnable, Object subject, long ticks) {
        scheduler = FoliaReflections.dummyGlobalRegionSchedulerRunDelayed(AmethystPaper.getInstance(), o -> runnable.run(), ticks);
        return this;
    }

    @Override
    public AmethystScheduler runLaterAsync(Runnable runnable, Object subject, long ticks) {
        scheduler = FoliaReflections.dummyAsyncSchedulerRunDelayed(AmethystPaper.getInstance(), o -> runnable.run(), ticks * 50, TimeUnit.MILLISECONDS);
        return this;
    }

    @Override
    public AmethystScheduler runDelayedTimer(Runnable runnable, Object subject, long delay, long ticks) {
        scheduler = FoliaReflections.dummyGlobalRegionSchedulerRunAtFixedRate(AmethystPaper.getInstance(), o -> runnable.run(), delay, ticks);
        return this;
    }

    @Override
    public AmethystScheduler runDelayedTimerAsync(Runnable runnable, Object subject, long delay, long ticks) {
        scheduler = FoliaReflections.dummyAsyncSchedulerRunAtFixedRate(AmethystPaper.getInstance(), o -> runnable.run(), delay * 50, ticks * 50, TimeUnit.MILLISECONDS);
        return this;
    }

    @Override
    public AmethystScheduler runDelayedRepeatedTimer(Runnable runnable, Object subject, long delay, long ticks, long repeats) {
        AtomicLong counter = new AtomicLong(0);
        scheduler = FoliaReflections.dummyGlobalRegionSchedulerRunAtFixedRate(AmethystPaper.getInstance(), o -> {
            runnable.run();
            if (counter.incrementAndGet() >= repeats) {
                cancel();
            }
        }, delay, ticks);
        return this;
    }

    @Override
    public AmethystScheduler runDelayedRepeatedTimerAsync(Runnable runnable, Object subject, long delay, long ticks, long repeats) {
        AtomicLong counter = new AtomicLong(0);
        scheduler = FoliaReflections.dummyAsyncSchedulerRunAtFixedRate(AmethystPaper.getInstance(), o -> {
            runnable.run();
            if (counter.incrementAndGet() >= repeats) {
                cancel();
            }
        }, delay * 50, ticks * 50, TimeUnit.MILLISECONDS);
        return this;
    }

    @Override
    public void cancel() {
        try {
            scheduler.getClass().getMethod("cancel").invoke(scheduler);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
