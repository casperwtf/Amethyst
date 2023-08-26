package wtf.casper.amethyst.core.scheduler;

public abstract class AmethystScheduler {

    public abstract AmethystScheduler run(Runnable runnable, Object subject);

    public abstract AmethystScheduler runAsync(Runnable runnable, Object subject);

    public abstract AmethystScheduler runLater(Runnable runnable, Object subject, long ticks);

    public abstract AmethystScheduler runLaterAsync(Runnable runnable, Object subject, long ticks);

    public abstract AmethystScheduler runDelayedTimer(Runnable runnable, Object subject, long delay, long ticks);

    public abstract AmethystScheduler runDelayedTimerAsync(Runnable runnable, Object subject, long delay, long ticks);

    public abstract AmethystScheduler runDelayedRepeatedTimer(Runnable runnable, Object subject, long delay, long ticks, long repeats);

    public abstract AmethystScheduler runDelayedRepeatedTimerAsync(Runnable runnable, Object subject, long delay, long ticks, long repeats);

    public abstract void cancel();
}
