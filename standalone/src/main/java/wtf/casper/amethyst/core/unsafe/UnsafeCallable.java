package wtf.casper.amethyst.core.unsafe;

import java.util.concurrent.Callable;

@FunctionalInterface
public interface UnsafeCallable<R> extends Callable<R> {

    @Override
    default R call() {
        try {
            return this.callThrows();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    R callThrows() throws Exception;

}
