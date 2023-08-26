package wtf.casper.amethyst.paper.scheduler;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import wtf.casper.amethyst.core.scheduler.AmethystScheduler;
import wtf.casper.amethyst.paper.AmethystPaper;

import java.util.concurrent.atomic.AtomicLong;

public class AmethystBukkitScheduler extends AmethystScheduler {

    private final BukkitScheduler bukkitScheduler;
    private BukkitTask task;

    public AmethystBukkitScheduler() {
        bukkitScheduler = AmethystPaper.getInstance().getServer().getScheduler();
    }

    @Override
    public AmethystScheduler run(Runnable runnable, Object subject) {
        task = bukkitScheduler.runTask(AmethystPaper.getInstance(), runnable);
        return this;
    }

    @Override
    public AmethystScheduler runAsync(Runnable runnable, Object subject) {
        task = bukkitScheduler.runTaskAsynchronously(AmethystPaper.getInstance(), runnable);
        return this;
    }

    @Override
    public AmethystScheduler runLater(Runnable runnable, Object subject, long ticks) {
        task = bukkitScheduler.runTaskLater(AmethystPaper.getInstance(), runnable, ticks);
        return this;
    }

    @Override
    public AmethystScheduler runLaterAsync(Runnable runnable, Object subject, long ticks) {
        task = bukkitScheduler.runTaskLaterAsynchronously(AmethystPaper.getInstance(), runnable, ticks);
        return this;
    }

    @Override
    public AmethystScheduler runDelayedTimer(Runnable runnable, Object subject, long delay, long ticks) {
        task = bukkitScheduler.runTaskTimer(AmethystPaper.getInstance(), runnable, delay, ticks);
        return this;
    }

    @Override
    public AmethystScheduler runDelayedTimerAsync(Runnable runnable, Object subject, long delay, long ticks) {
        task = bukkitScheduler.runTaskTimerAsynchronously(AmethystPaper.getInstance(), runnable, delay, ticks);
        return this;
    }

    @Override
    public AmethystScheduler runDelayedRepeatedTimer(Runnable runnable, Object subject, long delay, long ticks, long repeats) {
        task = bukkitScheduler.runTaskTimer(AmethystPaper.getInstance(), repeatedRunnable(runnable, repeats), delay, ticks);
        return this;
    }

    @Override
    public AmethystScheduler runDelayedRepeatedTimerAsync(Runnable runnable, Object subject, long delay, long ticks, long repeats) {
        task = bukkitScheduler.runTaskTimerAsynchronously(AmethystPaper.getInstance(), repeatedRunnable(runnable, repeats), delay, ticks);
        return this;
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    protected Runnable repeatedRunnable(Runnable runnable, long repeats) {
        AtomicLong counter = new AtomicLong(0);
        return () -> {
            runnable.run();
            if (counter.incrementAndGet() >= repeats) {
                cancel();
            }
        };
    }
}
