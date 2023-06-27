package wtf.casper.amethyst.core.storage.direct.fstorage;

import wtf.casper.amethyst.core.storage.ConstructableValue;
import wtf.casper.amethyst.core.storage.KeyValue;
import wtf.casper.amethyst.core.storage.impl.fstorage.JsonFStorage;

import java.io.File;
import java.util.function.Function;
import java.util.function.Supplier;

public class DirectJsonFStorage<K, V> extends JsonFStorage<K, V> implements ConstructableValue<K, V>, KeyValue<K, V> {

    private final Function<K, V> function;
    private final Supplier<V> supplier;
    private final Class<K> keyClass;
    private final Class<V> valueClass;

    public DirectJsonFStorage(Class<K> keyClass, Class<V> valueClass, File file, Function<K, V> function, Supplier<V> supplier) {
        super(file, valueClass);
        this.function = function;
        this.supplier = supplier;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
    }

    @Override
    public V constructValue(K key) {
        return function.apply(key);
    }

    @Override
    public V constructValue() {
        return supplier.get();
    }

    @Override
    public Class<K> key() {
        return keyClass;
    }

    @Override
    public Class<V> value() {
        return valueClass;
    }
}
