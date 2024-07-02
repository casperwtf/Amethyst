package wtf.casper.amethyst.paper.scheduler;

import java.util.function.Consumer;

public abstract class AmethystScheduler {

    public abstract AmethystScheduler run(Consumer<AmethystScheduler> runnable, Object subject);

    public abstract AmethystScheduler runAsync(Consumer<AmethystScheduler> runnable, Object subject);

    public abstract AmethystScheduler runLater(Consumer<AmethystScheduler> runnable, Object subject, long ticks);

    public abstract AmethystScheduler runLaterAsync(Consumer<AmethystScheduler> runnable, Object subject, long ticks);

    public abstract AmethystScheduler runDelayedTimer(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks);

    public abstract AmethystScheduler runDelayedTimerAsync(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks);

    public abstract AmethystScheduler runDelayedRepeatedTimer(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks, long repeats);

    public abstract AmethystScheduler runDelayedRepeatedTimerAsync(Consumer<AmethystScheduler> runnable, Object subject, long delay, long ticks, long repeats);

    public abstract void cancel();

    public abstract boolean isCancelled();
}
