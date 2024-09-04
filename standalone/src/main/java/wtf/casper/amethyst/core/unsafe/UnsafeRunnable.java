package wtf.casper.amethyst.core.unsafe;

@FunctionalInterface
public interface UnsafeRunnable extends Runnable {

    @Override
    default void run() {
        try {
            this.runThrow();
        } catch (Exception ignored) {
        }
    }

    void runThrow() throws Exception;

}
