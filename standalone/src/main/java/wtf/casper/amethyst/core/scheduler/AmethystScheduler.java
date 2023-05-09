package wtf.casper.amethyst.core.scheduler;

public abstract class AmethystScheduler {

    public abstract AmethystScheduler run(Runnable runnable);

    public abstract AmethystScheduler runAsync(Runnable runnable);

    public abstract AmethystScheduler runLater(Runnable runnable, long ticks);

    public abstract AmethystScheduler runLaterAsync(Runnable runnable, long ticks);

    public abstract AmethystScheduler runDelayedTimer(Runnable runnable, long delay, long ticks);

    public abstract AmethystScheduler runDelayedTimerAsync(Runnable runnable, long delay, long ticks);

    public abstract AmethystScheduler runDelayedRepeatedTimer(Runnable runnable, long delay, long ticks, long repeats);

    public abstract AmethystScheduler runDelayedRepeatedTimerAsync(Runnable runnable, long delay, long ticks, long repeats);

    public abstract void cancel();
}
