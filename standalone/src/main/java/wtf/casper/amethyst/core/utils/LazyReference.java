package wtf.casper.amethyst.core.utils;

import java.util.function.Supplier;

public class LazyReference<T> {

    protected T value = null;
    private final Supplier<T> supplier;

    public LazyReference(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (value == null) {
            value = supplier.get();
            if (value == null) {
                throw new NullPointerException("LazyReference supplier returned null");
            }
        }
        return value;
    }
}
