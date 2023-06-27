package wtf.casper.amethyst.core.storage.direct.fstorage;

import wtf.casper.amethyst.core.storage.ConstructableValue;
import wtf.casper.amethyst.core.storage.Credentials;
import wtf.casper.amethyst.core.storage.KeyValue;
import wtf.casper.amethyst.core.storage.impl.fstorage.MariaDBFStorage;
import wtf.casper.amethyst.core.storage.impl.fstorage.SQLiteFStorage;

import java.io.File;
import java.util.function.Function;
import java.util.function.Supplier;

public class DirectSQLiteFStorage<K, V> extends SQLiteFStorage<K, V> implements ConstructableValue<K, V>, KeyValue<K, V> {

    private final Function<K, V> function;
    private final Supplier<V> supplier;

    public DirectSQLiteFStorage(Class<K> keyClass, Class<V> valueClass, File file, String table, Function<K, V> function, Supplier<V> supplier) {
        super(keyClass, valueClass, file, table);
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
