package wtf.casper.amethyst.core.storage.direct.fstorage;

import wtf.casper.amethyst.core.storage.ConstructableValue;
import wtf.casper.amethyst.core.storage.Credentials;
import wtf.casper.amethyst.core.storage.KeyValue;
import wtf.casper.amethyst.core.storage.impl.fstorage.MariaDBFStorage;
import wtf.casper.amethyst.core.storage.impl.fstorage.SQLFStorage;

import java.util.function.Function;
import java.util.function.Supplier;

public class DirectMariaDBFStorage<K, V> extends MariaDBFStorage<K, V> implements ConstructableValue<K, V>, KeyValue<K, V> {

    private final Function<K, V> function;
    private final Supplier<V> supplier;

    public DirectMariaDBFStorage(Class<K> keyClass, Class<V> valueClass, Credentials credentials, Function<K, V> function, Supplier<V> supplier) {
        super(keyClass, valueClass, credentials);
        this.function = function;
        this.supplier = supplier;
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
