package wtf.casper.amethyst.core.utils;

import java.util.function.Supplier;

public class Lazy<T> {

    protected T value = null;
    private final Supplier<T> supplier;

    public Lazy(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        if (value == null) {
            value = supplier.get();
            if (value == null) {
                throw new NullPointerException("Lazy supplier returned null");
            }
        }
        return value;
    }
}
