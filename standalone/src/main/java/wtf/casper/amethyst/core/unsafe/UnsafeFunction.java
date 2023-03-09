package wtf.casper.amethyst.core.unsafe;

import lombok.SneakyThrows;

import java.util.function.Function;

@FunctionalInterface
public interface UnsafeFunction<T, R> extends Function<T, R> {

    static <T> Function<T, T> identity() {
        return t -> t;
    }

    @SneakyThrows
    @Override
    default R apply(final T t) {
        return this.applyThrows(t);
    }

    R applyThrows(final T t) throws Exception;

    default <V> Function<V, R> compose(final Function<? super V, ? extends T> before) {
        return (V v) -> apply(before.apply(v));
    }

    default <V> Function<T, V> andThen(final Function<? super R, ? extends V> after) {
        return (T t) -> after.apply(apply(t));
    }

}
