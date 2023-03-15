package wtf.casper.amethyst.core.storage;

import wtf.casper.amethyst.core.exceptions.AmethystException;

public interface ConstructableValue<K, V> {

    default V constructValue(final K key) {
        try {
            throw new AmethystException("ConstructableValue#constructValue(K) is not implemented! Please implement this method or use ConstructableValue#constructValue() instead.");
        } catch (AmethystException e) {
            throw new RuntimeException(e);
        }
    }

    default V constructValue() {
        try {
            throw new AmethystException("ConstructableValue#constructValue() is not implemented! Please implement this method or use ConstructableValue#constructValue() instead.");
        } catch (AmethystException e) {
            throw new RuntimeException(e);
        }
    }

}
