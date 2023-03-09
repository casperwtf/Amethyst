package wtf.casper.amethyst.core.unsafe;

import lombok.SneakyThrows;

@FunctionalInterface
public interface UnsafeRunnable extends Runnable {

    @SneakyThrows
    @Override
    default void run() {
        this.runThrow();
    }

    void runThrow() throws Exception;

}
