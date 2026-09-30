package net.rovalio.CosmereAPI.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class CopyOnWriteRegistry<K, V> {

    private volatile Map<K, V> entries = Collections.emptyMap();

    public boolean putIfAbsent(K key, V value) {
        Objects.requireNonNull(key, "Registry key cannot be null");
        Objects.requireNonNull(value, "Registry value cannot be null");

        synchronized (this) {
            if (entries.containsKey(key)) {
                return false;
            }

            Map<K, V> updated = new LinkedHashMap<>(entries);
            updated.put(key, value);
            entries = Collections.unmodifiableMap(updated);
            return true;
        }
    }

    public V get(K key) {
        return key == null ? null : entries.get(key);
    }

    public boolean containsKey(K key) {
        return key != null && entries.containsKey(key);
    }

    public Map<K, V> entries() {
        return entries;
    }
}
