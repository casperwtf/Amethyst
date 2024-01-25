package wtf.casper.amethyst.core.cache.async;

import wtf.casper.amethyst.core.cache.AsyncCache;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;

public class CaffeineAsyncCache<K, V> implements AsyncCache<K, V> {

    private final com.github.benmanes.caffeine.cache.AsyncCache<K, V> cache;

    public CaffeineAsyncCache(Object cache) {
        if (cache instanceof com.github.benmanes.caffeine.cache.AsyncCache<?, ?>) {
            this.cache = (com.github.benmanes.caffeine.cache.AsyncCache<K, V>) cache;
        } else {
            throw new IllegalArgumentException("Cache must be a com.github.benmanes.caffeine.cache.Cache");
        }
    }

    @Override
    public CompletableFuture<V> getIfPresent(K key) {
        return cache.getIfPresent(key);
    }

    @Override
    public CompletableFuture<Void> put(K key, CompletableFuture<V> value) {
        return CompletableFuture.runAsync(() -> {
            try {
                cache.put(key, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public CompletableFuture<Void> putAll(Map<K, V> map) {
        return CompletableFuture.runAsync(() -> {
            for (Map.Entry<K, V> kvEntry : map.entrySet()) {
                try {
                    cache.put(kvEntry.getKey(), CompletableFuture.completedFuture(kvEntry.getValue()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public CompletableFuture<Void> invalidate(K key) {
        return CompletableFuture.runAsync(() -> {
            cache.put(key, null);
        });
    }

    @Override
    public CompletableFuture<Void> invalidateAll() {
        return CompletableFuture.runAsync(() -> {
            cache.synchronous().invalidateAll();
        });
    }

    @Override
    public CompletableFuture<Boolean> contains(K key) {
        return getIfPresent(key).thenCompose(v -> {
            return CompletableFuture.completedFuture(v != null);
        });
    }

    @Override
    public CompletableFuture<Long> size() {
        return CompletableFuture.completedFuture(cache.synchronous().estimatedSize());
    }

    @Override
    public CompletableFuture<Void> cleanUp() {
        return CompletableFuture.runAsync(() -> {
            cache.synchronous().cleanUp();
        });
    }

    @Override
    public CompletableFuture<ConcurrentMap<K, CompletableFuture<V>>> asMap() {
        return CompletableFuture.supplyAsync(cache::asMap);
    }
}
