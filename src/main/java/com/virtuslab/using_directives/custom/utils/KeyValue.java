package com.virtuslab.using_directives.custom.utils;

import java.util.Map;

public class KeyValue<K, V> implements Map.Entry<K, V> {
    private final K key;
    private V value;

    public KeyValue(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        this.value = value;
        return value;
    }

    public KeyValue<K, V> withNewKey(K key) {
        return new KeyValue<>(key, value);
    }
}
