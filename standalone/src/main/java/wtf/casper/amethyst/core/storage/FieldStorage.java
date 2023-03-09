package wtf.casper.amethyst.core.storage;

import com.github.benmanes.caffeine.cache.Cache;

public interface FieldStorage<K, V> extends StatelessFieldStorage<K, V> {

    /**
     * @return the cache used by this storage.
     */
    Cache<K, V> cache();

    /**
     * @param cache the new cache to use.
     */
    void cache(Cache<K, V> cache);
}