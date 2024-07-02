package wtf.casper.amethyst.paper.scheduler;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import wtf.casper.amethyst.paper.AmethystPaper;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

public class AmethystBukkitScheduler extends AmethystScheduler {

    private final BukkitScheduler bukkitScheduler;
    private BukkitTask task;

    public AmethystBukkitScheduler() {
        bukkitScheduler = AmethystPaper.getInstance().getServer().getScheduler();
    }

    @Override
    public AmethystScheduler run(Consumer<AmethystScheduler> runnable, Object subject) {
        task = bukkitScheduler.runTask(AmethystPaper.getInstance(), () -> {
            runnable.accept(this);
        });
        return this;
    }

    @Override
    public AmethystScheduler runAsync(Consumer<AmethystScheduler> runnable, Object subject) {
        task = bukkitScheduler.runTaskAsynchronously(AmethystPaper.getInstance(), () -> {
            runnable.accept(this);
        });
        return this;
    }

    @Override
    public AmethystScheduler runLater(Consumer<AmethystScheduler> runnable, Object subject, long ticks) {
        task = bukkitScheduler.runTaskLater(AmethystPaper.getInstance(), () -> {
            runnable.accept(this);
        }, ticks);
        return this;
    }

    @Override
    public AmethystScheduler runLaterAsync(Consumer<AmethystScheduler> runnable, Object subject, long ticks) {
        task = bukkitScheduler.runTaskLaterAsynchronously(AmethystPaper.getInstance(), () -> {
            runnable.accept(this);
        }, ticks);
        return this;
    }

    @Override
    public AmethystScheduler runDelayedTimer(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks) {
        task = bukkitScheduler.runTaskTimer(AmethystPaper.getInstance(), () -> {
            runnable.accept(this);
        }, delay, ticks);
        return this;
    }

    @Override
    public AmethystScheduler runDelayedTimerAsync(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks) {
        task = bukkitScheduler.runTaskTimerAsynchronously(AmethystPaper.getInstance(), () -> {
            runnable.accept(this);
        }, delay, ticks);
        return this;
    }

    @Override
    public AmethystScheduler runDelayedRepeatedTimer(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks, long repeats) {
        task = bukkitScheduler.runTaskTimer(AmethystPaper.getInstance(), repeatedRunnable(() -> {
            runnable.accept(this);
        }, repeats), delay, ticks);
        return this;
    }

    @Override
    public AmethystScheduler runDelayedRepeatedTimerAsync(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks, long repeats) {
        task = bukkitScheduler.runTaskTimerAsynchronously(AmethystPaper.getInstance(), repeatedRunnable(() -> {
            runnable.accept(this);
        }, repeats), delay, ticks);
        return this;
    }

    @Override
    public void cancel() {
        task.cancel();
    }

    @Override
    public boolean isCancelled() {
        return task.isCancelled();
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
