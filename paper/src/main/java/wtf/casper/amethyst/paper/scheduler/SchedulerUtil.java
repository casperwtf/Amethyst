package wtf.casper.amethyst.paper.scheduler;

import wtf.casper.amethyst.paper.utils.FoliaUtil;

import java.util.function.Consumer;

public class SchedulerUtil {

    private SchedulerUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Run a task on the main thread
     * @param runnable The task to run
     * @param subject The subject to run the task on, can be null & is not used in Bukkit. Valid subjects are Entity, Block or Location. Will default to global if null.
     * @return The scheduler instance
     */
    public static AmethystScheduler run(Consumer<AmethystScheduler> runnable, Object subject) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().run(runnable, subject);
        } else {
            return new AmethystBukkitScheduler().run(runnable, null);
        }
    }

    /**
     * Run a task off the main thread
     * @param runnable The task to run
     * @param subject The subject to run the task on, can be null & is not used in Bukkit. Valid subjects are Entity, Block or Location. Will default to global if null.
     * @return The scheduler instance
     */
    public static AmethystScheduler runAsync(Consumer<AmethystScheduler> runnable, Object subject) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runAsync(runnable, subject);
        } else {
            return new AmethystBukkitScheduler().runAsync(runnable, null);
        }
    }

    /**
     * Run a task on the main thread after a delay
     * @param runnable The task to run
     * @param subject The subject to run the task on, can be null & is not used in Bukkit. Valid subjects are Entity, Block or Location. Will default to global if null.
     * @param ticks The delay in ticks
     * @return The scheduler instance
     */
    public static AmethystScheduler runLater(Consumer<AmethystScheduler> runnable, Object subject, long ticks) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runLater(runnable, subject, ticks);
        } else {
            return new AmethystBukkitScheduler().runLater(runnable, null, ticks);
        }
    }

    /**
     * Run a task off the main thread after a delay
     * @param runnable The task to run
     * @param subject The subject to run the task on, can be null & is not used in Bukkit. Valid subjects are Entity, Block or Location. Will default to global if null.
     * @param ticks The delay in ticks
     * @return The scheduler instance
     */
    public static AmethystScheduler runLaterAsync(Consumer<AmethystScheduler> runnable, Object subject, long ticks) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runLaterAsync(runnable, subject, ticks);
        } else {
            return new AmethystBukkitScheduler().runLaterAsync(runnable, null, ticks);
        }
    }

    /**
     * Run a task on the main thread after a delay
     * @param runnable The task to run
     * @param subject The subject to run the task on, can be null & is not used in Bukkit. Valid subjects are Entity, Block or Location. Will default to global if null.
     * @param delay The delay in ticks
     * @param ticks The interval in ticks
     * @return The scheduler instance
     */
    public static AmethystScheduler runDelayedTimer(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runDelayedTimer(runnable, subject, delay, ticks);
        } else {
            return new AmethystBukkitScheduler().runDelayedTimer(runnable, null, delay, ticks);
        }
    }

    /**
     * Run a task off the main thread after a delay
     * @param runnable The task to run
     * @param subject The subject to run the task on, can be null & is not used in Bukkit. Valid subjects are Entity, Block or Location. Will default to global if null.
     * @param delay The delay in ticks
     * @param ticks The interval in ticks
     * @return The scheduler instance
     */
    public static AmethystScheduler runDelayedTimerAsync(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runDelayedTimerAsync(runnable, subject, delay, ticks);
        } else {
            return new AmethystBukkitScheduler().runDelayedTimerAsync(runnable, null, delay, ticks);
        }
    }

    /**
     * Run a task on the main thread after a delay
     * @param runnable The task to run
     * @param subject The subject to run the task on, can be null & is not used in Bukkit. Valid subjects are Entity, Block or Location. Will default to global if null.
     * @param delay The delay in ticks
     * @param ticks The interval in ticks
     * @param repeats The number of times to repeat the task
     * @return The scheduler instance
     */
    public static AmethystScheduler runDelayedRepeatedTimer(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks, long repeats) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runDelayedRepeatedTimer(runnable, subject, delay, ticks, repeats);
        } else {
            return new AmethystBukkitScheduler().runDelayedRepeatedTimer(runnable, null, delay, ticks, repeats);
        }
    }

    /**
     * Run a task off the main thread after a delay
     * @param runnable The task to run
     * @param subject The subject to run the task on, can be null & is not used in Bukkit. Valid subjects are Entity, Block or Location. Will default to global if null.
     * @param delay The delay in ticks
     * @param ticks The interval in ticks
     * @param repeats The number of times to repeat the task
     * @return The scheduler instance
     */
    public static AmethystScheduler runDelayedRepeatedTimerAsync(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks, long repeats) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runDelayedRepeatedTimerAsync(runnable, subject, delay, ticks, repeats);
        } else {
            return new AmethystBukkitScheduler().runDelayedRepeatedTimerAsync(runnable, null, delay, ticks, repeats);
        }
    }

    /**
     * Run a task on the main thread after a delay
     * @param runnable The task to run
     * @return The scheduler instance
     */
    public static AmethystScheduler run(Consumer<AmethystScheduler> runnable) {
        return run(runnable, null);
    }

    /**
     * Run a task off the main thread after a delay
     * @param runnable The task to run
     * @return The scheduler instance
     */
    public static AmethystScheduler runAsync(Consumer<AmethystScheduler> runnable) {
        return runAsync(runnable, null);
    }

    /**
     * Run a task on the main thread after a delay
     * @param runnable The task to run
     * @param ticks The delay in ticks
     * @return The scheduler instance
     */
    public static AmethystScheduler runLater(Consumer<AmethystScheduler> runnable, long ticks) {
        return runLater(runnable, null, ticks);
    }

    /**
     * Run a task off the main thread after a delay
     * @param runnable The task to run
     * @param ticks The delay in ticks
     * @return The scheduler instance
     */
    public static AmethystScheduler runLaterAsync(Consumer<AmethystScheduler> runnable, long ticks) {
        return runLaterAsync(runnable, null, ticks);
    }

    /**
     * Run a task on the main thread after a delay
     * @param runnable The task to run
     * @param delay The delay in ticks
     * @param ticks The interval in ticks
     * @return The scheduler instance
     */
    public static AmethystScheduler runDelayedTimer(Consumer<AmethystScheduler> runnable, long delay, long ticks) {
        return runDelayedTimer(runnable, null, delay, ticks);
    }

    /**
     * Run a task off the main thread after a delay
     * @param runnable The task to run
     * @param delay The delay in ticks
     * @param ticks The interval in ticks
     * @return The scheduler instance
     */
    public static AmethystScheduler runDelayedTimerAsync(Consumer<AmethystScheduler> runnable, long delay, long ticks) {
        return runDelayedTimerAsync(runnable, null, delay, ticks);
    }

    /**
     * Run a task on the main thread after a delay
     * @param runnable The task to run
     * @param delay The delay in ticks
     * @param ticks The interval in ticks
     * @param repeats The number of times to repeat the task
     * @return The scheduler instance
     */
    public static AmethystScheduler runDelayedRepeatedTimer(Consumer<AmethystScheduler> runnable, long delay, long ticks, long repeats) {
        return runDelayedRepeatedTimer(runnable, null, delay, ticks, repeats);
    }

    /**
     * Run a task off the main thread after a delay
     * @param runnable The task to run
     * @param delay The delay in ticks
     * @param ticks The interval in ticks
     * @param repeats The number of times to repeat the task
     * @return The scheduler instance
     */
    public static AmethystScheduler runDelayedRepeatedTimerAsync(Consumer<AmethystScheduler> runnable, long delay, long ticks, long repeats) {
        return runDelayedRepeatedTimerAsync(runnable, null, delay, ticks, repeats);
    }

}
