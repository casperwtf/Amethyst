package wtf.casper.amethyst.core.unsafe;

import lombok.SneakyThrows;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface UnsafeCallable<R> extends Callable<R> {

    @Override
    @SneakyThrows
    default R call() {
        return this.callThrows();
    }

    R callThrows() throws Exception;

}
