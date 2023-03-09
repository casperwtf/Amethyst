package wtf.casper.amethyst.core.distributedworkload;

import java.util.ArrayDeque;
import java.util.Deque;

public class ScheduledWorkloadRunnable implements Runnable {

    private final double MAX_MILLIS_PER_TICK = 2.5;
    private final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

    private final Deque<ScheduledWorkload> workloadDeque = new ArrayDeque<>();

    public void addWorkload(ScheduledWorkload workload) {
        workloadDeque.add(workload);
    }

    @Override
    public void run() {
        long stopTime = System.nanoTime() + MAX_NANOS_PER_TICK;

        ScheduledWorkload lastElement = this.workloadDeque.peekLast();
        ScheduledWorkload nextLoad = null;

        while (System.nanoTime() < stopTime && !this.workloadDeque.isEmpty() && nextLoad != lastElement) {
            nextLoad = this.workloadDeque.poll();
            nextLoad.compute();
            if (nextLoad.shouldBeRescheduled()) {
                this.addWorkload(nextLoad);
            }
        }
    }
}
