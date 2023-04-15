package wtf.casper.amethyst.paper.utils;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.core.distributedworkload.WorkloadRunnable;
import wtf.casper.amethyst.paper.AmethystFolia;
import wtf.casper.amethyst.paper.AmethystPlugin;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class RunnableUtil {

    public static WorkloadRunnable getWorkload(AmethystPlugin plugin) {
        WorkloadRunnable workloadRunnable = new WorkloadRunnable();
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> workloadRunnable.run(), 1, 1);
        return workloadRunnable;
    }

    @NotNull
    public static ScheduledTask runSync(Consumer<ScheduledTask> task) {
        if (!isEnabled()) {
            AmethystPlugin plugin = AmethystFolia.getInstance().getCallingPlugin();
            return Bukkit.getGlobalRegionScheduler().run(plugin, task);
        }

        return Bukkit.getGlobalRegionScheduler().run(AmethystFolia.getInstance(), task);
    }

    @NotNull
    public static ScheduledTask runAsync(Consumer<ScheduledTask> task) {
        if (!isEnabled()) {
            AmethystPlugin plugin = AmethystFolia.getInstance().getCallingPlugin();
            return Bukkit.getAsyncScheduler().runNow(plugin, task);
        }

        return Bukkit.getAsyncScheduler().runNow(AmethystFolia.getInstance(), task);
    }

    @NotNull
    public static ScheduledTask delay(Consumer<ScheduledTask> task, long delay) {
        if (!isEnabled()) {
            AmethystPlugin plugin = AmethystFolia.getInstance().getCallingPlugin();
            return Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task, delay);
        }

        return Bukkit.getGlobalRegionScheduler().runDelayed(AmethystFolia.getInstance(), task, delay);
    }

    @NotNull
    public static ScheduledTask delayAsync(Consumer<ScheduledTask> task, long delay, TimeUnit timeUnit) {
        if (!isEnabled()) {
            AmethystPlugin plugin = AmethystFolia.getInstance().getCallingPlugin();
            return Bukkit.getAsyncScheduler().runDelayed(plugin, task, delay, timeUnit);
        }

        return Bukkit.getAsyncScheduler().runDelayed(AmethystFolia.getInstance(), task, delay, timeUnit);
    }

    @NotNull
    public static ScheduledTask repeat(Consumer<ScheduledTask> task, long initialDelay, long repeatDelay) {
        if (!isEnabled()) {
            AmethystPlugin plugin = AmethystFolia.getInstance().getCallingPlugin();
            return Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, task, initialDelay, repeatDelay);
        }

        return Bukkit.getGlobalRegionScheduler().runAtFixedRate(AmethystFolia.getInstance(), task, initialDelay, repeatDelay);
    }

    @NotNull
    public static ScheduledTask repeat(final Consumer<ScheduledTask> task, long initialDelay, long repeatDelay, final int limit) {
        if (!isEnabled()) {
            AmethystPlugin plugin = AmethystFolia.getInstance().getCallingPlugin();
            AtomicInteger repeats = new AtomicInteger();
            return Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> {
                if (repeats.get() > limit) {
                    return;
                }

                repeats.getAndIncrement();
                task.accept(scheduledTask);
            }, initialDelay, repeatDelay);
        }

        AtomicInteger repeats = new AtomicInteger();
        return Bukkit.getGlobalRegionScheduler().runAtFixedRate(AmethystFolia.getInstance(), scheduledTask -> {
            if (repeats.get() > limit) {
                return;
            }

            repeats.getAndIncrement();
            task.accept(scheduledTask);
        }, initialDelay, repeatDelay);
    }

    @NotNull
    public static ScheduledTask repeatAsync(Consumer<ScheduledTask> task, long initialDelay, long repeatDelay, TimeUnit timeUnit) {
        if (!isEnabled()) {
            AmethystPlugin plugin = AmethystFolia.getInstance().getCallingPlugin();
            return Bukkit.getAsyncScheduler().runAtFixedRate(plugin, task, initialDelay, repeatDelay, timeUnit);
        }

        return Bukkit.getAsyncScheduler().runAtFixedRate(AmethystFolia.getInstance(), task, initialDelay, repeatDelay, timeUnit);
    }

    @NotNull
    public static ScheduledTask repeatAsync(final Consumer<ScheduledTask> task, long initialDelay, long repeatDelay, final int limit, TimeUnit timeUnit) {
        if (!isEnabled()) {
            AmethystPlugin plugin = AmethystFolia.getInstance().getCallingPlugin();
            AtomicInteger repeats = new AtomicInteger();
            return Bukkit.getAsyncScheduler().runAtFixedRate(plugin, scheduledTask -> {
                if (repeats.get() > limit) {
                    return;
                }

                repeats.getAndIncrement();
                task.accept(scheduledTask);
            }, initialDelay, repeatDelay, timeUnit);
        }

        AtomicInteger repeats = new AtomicInteger();
        return Bukkit.getAsyncScheduler().runAtFixedRate(AmethystFolia.getInstance(), scheduledTask -> {
            if (repeats.get() > limit) {
                return;
            }

            repeats.getAndIncrement();
            task.accept(scheduledTask);
        }, initialDelay, repeatDelay, timeUnit);
    }

    private static boolean isEnabled() {
        return AmethystFolia.getInstance().isEnabled();
    }
}


