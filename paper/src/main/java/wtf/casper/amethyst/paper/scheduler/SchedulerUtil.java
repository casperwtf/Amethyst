package wtf.casper.amethyst.paper.scheduler;

import wtf.casper.amethyst.core.scheduler.AmethystScheduler;
import wtf.casper.amethyst.paper.utils.FoliaUtil;

public class SchedulerUtil {

    public static AmethystScheduler run(Runnable runnable, Object subject) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().run(runnable, subject);
        } else {
            return new AmethystBukkitScheduler().run(runnable, subject);
        }
    }


    public static AmethystScheduler runAsync(Runnable runnable, Object subject) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runAsync(runnable, subject);
        } else {
            return new AmethystBukkitScheduler().runAsync(runnable, subject);
        }
    }


    public static AmethystScheduler runLater(Runnable runnable, Object subject, long ticks) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runLater(runnable, subject, ticks);
        } else {
            return new AmethystBukkitScheduler().runLater(runnable, subject, ticks);
        }
    }


    public static AmethystScheduler runLaterAsync(Runnable runnable, Object subject, long ticks) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runLaterAsync(runnable, subject, ticks);
        } else {
            return new AmethystBukkitScheduler().runLaterAsync(runnable, subject, ticks);
        }
    }


    public static AmethystScheduler runDelayedTimer(Runnable runnable, Object subject, long delay, long ticks) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runDelayedTimer(runnable, subject, delay, ticks);
        } else {
            return new AmethystBukkitScheduler().runDelayedTimer(runnable, subject, delay, ticks);
        }
    }


    public static AmethystScheduler runDelayedTimerAsync(Runnable runnable, Object subject, long delay, long ticks) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runDelayedTimerAsync(runnable, subject, delay, ticks);
        } else {
            return new AmethystBukkitScheduler().runDelayedTimerAsync(runnable, subject, delay, ticks);
        }
    }


    public AmethystScheduler runDelayedRepeatedTimer(Runnable runnable, Object subject, long delay, long ticks, long repeats) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runDelayedRepeatedTimer(runnable, subject, delay, ticks, repeats);
        } else {
            return new AmethystBukkitScheduler().runDelayedRepeatedTimer(runnable, subject, delay, ticks, repeats);
        }
    }


    public AmethystScheduler runDelayedRepeatedTimerAsync(Runnable runnable, Object subject, long delay, long ticks, long repeats) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runDelayedRepeatedTimerAsync(runnable, subject, delay, ticks, repeats);
        } else {
            return new AmethystBukkitScheduler().runDelayedRepeatedTimerAsync(runnable, subject, delay, ticks, repeats);
        }
    }

    public static AmethystScheduler run(Runnable runnable) {
        return run(runnable, null);
    }

    public static AmethystScheduler runAsync(Runnable runnable) {
        return runAsync(runnable, null);
    }

    public static AmethystScheduler runLater(Runnable runnable, long ticks) {
        return runLater(runnable, null, ticks);
    }

    public static AmethystScheduler runLaterAsync(Runnable runnable, long ticks) {
        return runLaterAsync(runnable, null, ticks);
    }

    public static AmethystScheduler runDelayedTimer(Runnable runnable, long delay, long ticks) {
        return runDelayedTimer(runnable, null, delay, ticks);
    }

    public static AmethystScheduler runDelayedTimerAsync(Runnable runnable, long delay, long ticks) {
        return runDelayedTimerAsync(runnable, null, delay, ticks);
    }

    public AmethystScheduler runDelayedRepeatedTimer(Runnable runnable, long delay, long ticks, long repeats) {
        return runDelayedRepeatedTimer(runnable, null, delay, ticks, repeats);
    }

    public AmethystScheduler runDelayedRepeatedTimerAsync(Runnable runnable, long delay, long ticks, long repeats) {
        return runDelayedRepeatedTimerAsync(runnable, null, delay, ticks, repeats);
    }

}
