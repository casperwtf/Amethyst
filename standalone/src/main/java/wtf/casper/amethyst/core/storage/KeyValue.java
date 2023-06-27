package wtf.casper.amethyst.core.storage;

public interface KeyValue<K, V> {
    Class<K> key();
    Class<V> value();
}
