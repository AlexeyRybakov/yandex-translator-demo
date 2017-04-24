package com.example.yandextranslatordemo.data.prefs_store;


public interface PrefsStore {

    void put(String key, Object object);

    boolean contains(String key);

    <T> T get(String key, Class<T> stored);

    void remove(String key);

    void clear();


}
