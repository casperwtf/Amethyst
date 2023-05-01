package wtf.casper.amethyst.paper.utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import wtf.casper.amethyst.core.distributedworkload.WorkloadRunnable;
import wtf.casper.amethyst.paper.AmethystPaper;
import wtf.casper.amethyst.paper.AmethystPlugin;

import java.util.function.Consumer;

public class RunnableUtil {

    public static WorkloadRunnable getWorkload(AmethystPlugin plugin) {
        WorkloadRunnable workloadRunnable = new WorkloadRunnable();
        plugin.getServer().getScheduler().runTaskTimer(plugin, workloadRunnable, 1, 1);
        return workloadRunnable;
    }

    @NotNull
    public static BukkitRunnable runSync(Consumer<BukkitRunnable> task) {
        if (!isEnabled()) {
            BukkitRunnable runnable = createSimpleTask(task);
            JavaPlugin plugin = AmethystPaper.getCallingPlugin();
            if (plugin == null) {
                runnable.runTask(AmethystPaper.getInstance());
                return runnable;
            }
            runnable.runTask(plugin);
            return runnable;
        }

        BukkitRunnable runnable = createSimpleTask(task);
        runnable.runTask(AmethystPaper.getInstance());
        return runnable;
    }

    @NotNull
    public static BukkitRunnable runAsync(Consumer<BukkitRunnable> task) {
        if (!isEnabled()) {
            BukkitRunnable runnable = createSimpleTask(task);
            JavaPlugin plugin = AmethystPaper.getCallingPlugin();
            if (plugin == null) {
                runnable.runTaskAsynchronously(AmethystPaper.getInstance());
                return runnable;
            }
            runnable.runTaskAsynchronously(plugin);
            return runnable;
        }

        BukkitRunnable runnable = createSimpleTask(task);
        runnable.runTaskAsynchronously(AmethystPaper.getInstance());
        return runnable;
    }

    @NotNull
    public static BukkitRunnable delay(Consumer<BukkitRunnable> task, long delay) {
        if (!isEnabled()) {
            BukkitRunnable runnable = createSimpleTask(task);
            JavaPlugin plugin = AmethystPaper.getCallingPlugin();
            if (plugin == null) {
                runnable.runTaskLater(AmethystPaper.getInstance(), delay);
                return runnable;
            }
            runnable.runTaskLater(plugin, delay);
            return runnable;
        }

        BukkitRunnable runnable = createSimpleTask(task);
        runnable.runTaskLater(AmethystPaper.getInstance(), delay);
        return runnable;
    }

    @NotNull
    public static BukkitRunnable delayAsync(Consumer<BukkitRunnable> task, long delay) {
        if (!isEnabled()) {
            BukkitRunnable runnable = createSimpleTask(task);
            JavaPlugin plugin = AmethystPaper.getCallingPlugin();
            if (plugin == null) {
                runnable.runTaskLaterAsynchronously(AmethystPaper.getInstance(), delay);
                return runnable;
            }
            runnable.runTaskLaterAsynchronously(plugin, delay);
            return runnable;
        }

        BukkitRunnable runnable = createSimpleTask(task);
        runnable.runTaskLaterAsynchronously(AmethystPaper.getInstance(), delay);
        return runnable;
    }

    @NotNull
    public static BukkitRunnable repeat(Consumer<BukkitRunnable> task, long initialDelay, long repeatDelay) {
        if (!isEnabled()) {
            BukkitRunnable runnable = createSimpleTask(task);
            JavaPlugin plugin = AmethystPaper.getCallingPlugin();
            if (plugin == null) {
                runnable.runTaskTimer(AmethystPaper.getInstance(), initialDelay, repeatDelay);
                return runnable;
            }
            runnable.runTask(plugin);
            runnable.runTaskTimer(plugin, initialDelay, repeatDelay);
            return runnable;
        }

        BukkitRunnable runnable = createSimpleTask(task);
        runnable.runTaskTimer(AmethystPaper.getInstance(), initialDelay, repeatDelay);
        return runnable;
    }

    @NotNull
    public static BukkitRunnable repeat(final Consumer<BukkitRunnable> task, long initialDelay, long repeatDelay, final int limit) {

        if (!isEnabled()) {
            BukkitRunnable runnable = new BukkitRunnable() {
                int repeats = 0;

                public void run() {
                    if (this.repeats > limit) {
                        return;
                    }

                    task.accept(this);
                    this.repeats++;
                }
            };

            runnable.runTaskTimer(AmethystPaper.getCallingPlugin(), initialDelay, repeatDelay);
            return runnable;
        }

        BukkitRunnable runnable = new BukkitRunnable() {
            int repeats = 0;

            public void run() {
                if (this.repeats > limit) {
                    return;
                }

                task.accept(this);
                this.repeats++;
            }
        };

        runnable.runTaskTimer(AmethystPaper.getInstance(), initialDelay, repeatDelay);
        return runnable;
    }

    @NotNull
    public static BukkitRunnable repeatAsync(Consumer<BukkitRunnable> task, long initialDelay, long repeatDelay) {
        if (!isEnabled()) {
            BukkitRunnable runnable = createSimpleTask(task);
            runnable.runTaskTimerAsynchronously(AmethystPaper.getCallingPlugin(), initialDelay, repeatDelay);
            return runnable;
        }

        BukkitRunnable runnable = createSimpleTask(task);
        runnable.runTaskTimerAsynchronously(AmethystPaper.getInstance(), initialDelay, repeatDelay);
        return runnable;
    }

    @NotNull
    public static BukkitRunnable repeatAsync(final Consumer<BukkitRunnable> task, long initialDelay, long repeatDelay, final int limit) {

        if (!isEnabled()) {
            BukkitRunnable runnable = new BukkitRunnable() {
                int repeats = 0;

                public void run() {
                    if (this.repeats > limit) {
                        return;
                    }

                    task.accept(this);
                    this.repeats++;
                }
            };

            runnable.runTaskTimerAsynchronously(AmethystPaper.getCallingPlugin(), initialDelay, repeatDelay);
            return runnable;
        }

        BukkitRunnable runnable = new BukkitRunnable() {
            int repeats = 0;

            public void run() {
                if (this.repeats > limit) {
                    return;
                }

                task.accept(this);
                this.repeats++;
            }
        };

        runnable.runTaskTimerAsynchronously(AmethystPaper.getInstance(), initialDelay, repeatDelay);
        return runnable;
    }

    private static boolean isEnabled() {
        return AmethystPaper.getInstance().isEnabled();
    }


    private static BukkitRunnable createSimpleTask(final Consumer<BukkitRunnable> task) {
        return new BukkitRunnable() {
            public void run() {
                task.accept(this);
            }
        };
    }
}


