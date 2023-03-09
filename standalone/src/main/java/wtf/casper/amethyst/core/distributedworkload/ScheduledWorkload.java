package wtf.casper.amethyst.core.distributedworkload;

public interface ScheduledWorkload extends Workload {
    void compute();

    default boolean shouldBeRescheduled() {
        return false;
    }
}
