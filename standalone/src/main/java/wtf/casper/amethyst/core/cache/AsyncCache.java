package wtf.casper.amethyst.core.cache;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;

public interface AsyncCache<K, V> {

    CompletableFuture<V> getIfPresent(K key);

    CompletableFuture<Void> put(K key, CompletableFuture<V> value);

    CompletableFuture<Void> putAll(Map<K, V> map);

    CompletableFuture<Void> invalidate(K key);

    CompletableFuture<Void> invalidateAll();

    CompletableFuture<Boolean> contains(K key);

    CompletableFuture<Long> size();

    CompletableFuture<Void> cleanUp();

    CompletableFuture<ConcurrentMap<K, CompletableFuture<V>>> asMap();
}
