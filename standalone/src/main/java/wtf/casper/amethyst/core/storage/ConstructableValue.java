package wtf.casper.amethyst.core.storage;

public interface ConstructableValue<K, V> {

    default V constructValue(final K key) {
        return null;
    }

    default V constructValue() {
        return null;
    }

}
