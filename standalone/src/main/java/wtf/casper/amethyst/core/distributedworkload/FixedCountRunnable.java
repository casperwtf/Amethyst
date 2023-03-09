package wtf.casper.amethyst.core.distributedworkload;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FixedCountRunnable implements ScheduledWorkload {

    private final int delay;
    private final int maxTicks;
    private final Runnable runnable;
    private int ticksAlive = 0;

    @Override
    public void compute() {
        if (this.ticksAlive++ % this.delay == 0) {
            this.runnable.run();
        }
    }

    @Override
    public boolean shouldBeRescheduled() {
        return this.ticksAlive < this.maxTicks;
    }
}
