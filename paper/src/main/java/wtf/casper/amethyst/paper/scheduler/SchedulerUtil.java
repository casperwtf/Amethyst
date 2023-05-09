package wtf.casper.amethyst.paper.scheduler;

import org.jetbrains.annotations.ApiStatus;
import wtf.casper.amethyst.core.scheduler.AmethystScheduler;
import wtf.casper.amethyst.paper.utils.FoliaUtil;

@ApiStatus.Experimental
public class SchedulerUtil {

    public static AmethystScheduler run(Runnable runnable) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().run(runnable);
        } else {
            return new AmethystBukkitScheduler().run(runnable);
        }
    }

    
    public static AmethystScheduler runAsync(Runnable runnable) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runAsync(runnable);
        } else {
            return new AmethystBukkitScheduler().runAsync(runnable);
        }
    }

    
    public static AmethystScheduler runLater(Runnable runnable, long ticks) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runLater(runnable, ticks);
        } else {
            return new AmethystBukkitScheduler().runLater(runnable, ticks);
        }
    }

    
    public static AmethystScheduler runLaterAsync(Runnable runnable, long ticks) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runLaterAsync(runnable, ticks);
        } else {
            return new AmethystBukkitScheduler().runLaterAsync(runnable, ticks);
        }
    }

    
    public static AmethystScheduler runDelayedTimer(Runnable runnable, long delay, long ticks) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runDelayedTimer(runnable, delay, ticks);
        } else {
            return new AmethystBukkitScheduler().runDelayedTimer(runnable, delay, ticks);
        }
    }

    
    public static AmethystScheduler runDelayedTimerAsync(Runnable runnable, long delay, long ticks) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runDelayedTimerAsync(runnable, delay, ticks);
        } else {
            return new AmethystBukkitScheduler().runDelayedTimerAsync(runnable, delay, ticks);
        }
    }

    
    public AmethystScheduler runDelayedRepeatedTimer(Runnable runnable, long delay, long ticks, long repeats) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runDelayedRepeatedTimer(runnable, delay, ticks, repeats);
        } else {
            return new AmethystBukkitScheduler().runDelayedRepeatedTimer(runnable, delay, ticks, repeats);
        }
    }

    
    public AmethystScheduler runDelayedRepeatedTimerAsync(Runnable runnable, long delay, long ticks, long repeats) {
        if (FoliaUtil.isFolia()) {
            return new AmethystFoliaScheduler().runDelayedRepeatedTimerAsync(runnable, delay, ticks, repeats);
        } else {
            return new AmethystBukkitScheduler().runDelayedRepeatedTimerAsync(runnable, delay, ticks, repeats);
        }
    }
}
