package wtf.casper.amethyst.paper.reflections;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import wtf.casper.amethyst.core.utils.AmethystLogger;
import wtf.casper.amethyst.core.utils.LazyReference;
import wtf.casper.amethyst.paper.utils.FoliaUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class FoliaReflections {

    // Server#getGlobalRegionScheduler
    public static final LazyReference<Method> GLOBAL_REGION_SCHEDULER = new LazyReference<>(() -> {
        if (!FoliaUtil.isFolia()) {
            return null;
        }
        try {
            return Bukkit.getServer().getClass().getMethod("getGlobalRegionScheduler");
        } catch (NoSuchMethodException e) {
            AmethystLogger.log("GlobalRegionScheduler is null! Folia was not found!");
            e.printStackTrace();
        }
        return null;
    });

    // GlobalRegionScheduler#run(Plugin, Consumer<ScheduledTask>)
    public static final LazyReference<Method> GLOBAL_REGION_SCHEDULER_RUN = new LazyReference<>(() -> {
        if (!FoliaUtil.isFolia()) {
            return null;
        }
        try {
            return GLOBAL_REGION_SCHEDULER.get().getReturnType().getMethod("run", Plugin.class, Consumer.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            AmethystLogger.log("GlobalRegionScheduler#run is null! Folia was not found!");
        }
        return null;
    });

    // GlobalRegionScheduler#runAtFixedRate(Plugin, Consumer<ScheduledTask>, long, long)
    public static final LazyReference<Method> GLOBAL_REGION_SCHEDULER_RUN_AT_FIXED_RATE = new LazyReference<>(() -> {
        if (!FoliaUtil.isFolia()) {
            return null;
        }
        try {
            return GLOBAL_REGION_SCHEDULER.get().getReturnType().getMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            AmethystLogger.log("GlobalRegionScheduler#runAtFixedRate is null! Folia was not found!");
        }
        return null;
    });

    // GlobalRegionScheduler#runDelayed(Plugin, Consumer<ScheduledTask>, long)
    public static final LazyReference<Method> GLOBAL_SCHEDULER_RUN_DELAYED = new LazyReference<>(() -> {
        if (!FoliaUtil.isFolia()) {
            return null;
        }
        try {
            return GLOBAL_REGION_SCHEDULER.get().getReturnType().getMethod("runDelayed", Plugin.class, Consumer.class, long.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            AmethystLogger.log("GlobalRegionScheduler#runDelayed is null! Folia was not found!");
        }
        return null;
    });

    // Server#getAsyncScheduler
    public static final LazyReference<Method> ASYNC_SCHEDULER = new LazyReference<>(() -> {
        if (!FoliaUtil.isFolia()) {
            return null;
        }
        try {
            return Bukkit.getServer().getClass().getMethod("getAsyncScheduler");
        } catch (NoSuchMethodException e) {
            AmethystLogger.log("AsyncScheduler is null! Folia was not found!");
            e.printStackTrace();
        }
        return null;
    });

    // AsyncScheduler#runNow(Plugin, Consumer<ScheduledTask>)
    public static final LazyReference<Method> ASYNC_SCHEDULER_RUN_NOW = new LazyReference<>(() -> {
        if (!FoliaUtil.isFolia()) {
            return null;
        }
        try {
            return ASYNC_SCHEDULER.get().getReturnType().getMethod("runNow", Plugin.class, Consumer.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            AmethystLogger.log("AsyncScheduler#runNow is null! Folia was not found!");
        }
        return null;
    });

    // AsyncScheduler#runDelayed(Plugin, Consumer<ScheduledTask>, long, TimeUnit)
    public static final LazyReference<Method> ASYNC_SCHEDULER_RUN_DELAYED = new LazyReference<>(() -> {
        if (!FoliaUtil.isFolia()) {
            return null;
        }
        try {
            return ASYNC_SCHEDULER.get().getReturnType().getMethod("runDelayed", Plugin.class, Consumer.class, long.class, TimeUnit.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            AmethystLogger.log("AsyncScheduler#runDelayed is null! Folia was not found!");
        }
        return null;
    });

    // AsyncScheduler#runAtFixedRate(Plugin, Consumer<ScheduledTask>, long, long, TimeUnit)
    public static final LazyReference<Method> ASYNC_SCHEDULER_RUN_AT_FIXED_RATE = new LazyReference<>(() -> {
        if (!FoliaUtil.isFolia()) {
            return null;
        }
        try {
            return ASYNC_SCHEDULER.get().getReturnType().getMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class, TimeUnit.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            AmethystLogger.log("AsyncScheduler#runAtFixedRate is null! Folia was not found!");
        }
        return null;
    });

    public static Object dummyAsyncSchedulerRunAtFixedRate(Plugin plugin, Consumer<?> task, long initialDelay, long period, TimeUnit unit) {
        if (!FoliaUtil.isFolia()) {
            return null;
        }
        try {
            return ASYNC_SCHEDULER_RUN_AT_FIXED_RATE.get().invoke(ASYNC_SCHEDULER.get().invoke(Bukkit.getServer()), plugin, task, initialDelay, period, unit);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            AmethystLogger.log("AsyncScheduler#runAtFixedRate is null! Folia was not found!");
        }
        return null;
    }

    public static Object dummyAsyncSchedulerRunDelayed(Plugin plugin, Consumer<?> task, long delay, TimeUnit unit) {
        if (!FoliaUtil.isFolia()) {
            return null;
        }
        try {
            return ASYNC_SCHEDULER_RUN_DELAYED.get().invoke(ASYNC_SCHEDULER.get().invoke(Bukkit.getServer()), plugin, task, delay, unit);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            AmethystLogger.log("AsyncScheduler#runDelayed is null! Folia was not found!");
        }
        return null;
    }

    public static Object dummyAsyncSchedulerRunNow(Plugin plugin, Consumer<?> task) {
        if (!FoliaUtil.isFolia()) {
            return null;
        }
        try {
            return ASYNC_SCHEDULER_RUN_NOW.get().invoke(ASYNC_SCHEDULER.get().invoke(Bukkit.getServer()), plugin, task);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            AmethystLogger.log("AsyncScheduler#runNow is null! Folia was not found!");
        }
        return null;
    }

    public static Object dummyGlobalRegionSchedulerRunDelayed(Plugin plugin, Consumer<?> task, long delay) {
        if (!FoliaUtil.isFolia()) {
            return null;
        }
        try {
            return GLOBAL_SCHEDULER_RUN_DELAYED.get().invoke(GLOBAL_REGION_SCHEDULER.get().invoke(Bukkit.getServer()), plugin, task, delay);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            AmethystLogger.log("GlobalRegionScheduler#runDelayed is null! Folia was not found!");
        }
        return null;
    }

    public static Object dummyGlobalRegionSchedulerRunAtFixedRate(Plugin plugin, Consumer<?> task, long initialDelay, long period) {
        if (!FoliaUtil.isFolia()) {
            return null;
        }
        try {
            return GLOBAL_REGION_SCHEDULER_RUN_AT_FIXED_RATE.get().invoke(GLOBAL_REGION_SCHEDULER.get().invoke(Bukkit.getServer()), plugin, task, initialDelay, period);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            AmethystLogger.log("GlobalRegionScheduler#runAtFixedRate is null! Folia was not found!");
        }
        return null;
    }

    public static Object dummyGlobalRegionSchedulerRun(Plugin plugin, Consumer<?> task) {
        if (!FoliaUtil.isFolia()) {
            return null;
        }
        try {
            return GLOBAL_REGION_SCHEDULER_RUN.get().invoke(GLOBAL_REGION_SCHEDULER.get().invoke(Bukkit.getServer()), plugin, task);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            AmethystLogger.log("GlobalRegionScheduler#run is null! Folia was not found!");
        }
        return null;
    }
}
