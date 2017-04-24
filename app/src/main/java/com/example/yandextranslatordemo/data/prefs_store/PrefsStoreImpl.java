package com.example.yandextranslatordemo.data.prefs_store;


import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

public class PrefsStoreImpl implements PrefsStore {

    private SharedPreferences preferences;
    private Gson gson;

    public PrefsStoreImpl(SharedPreferences preferences, Gson gson) {
        this.preferences = preferences;
        this.gson = gson;
    }

    @Override
    public void put(@NonNull String key, @NonNull Object object) {
        String s = gson.toJson(object);
        preferences.edit()
                .putString(key, s)
                .apply();
    }

    @Override
    public boolean contains(@NonNull String key) {
        return preferences.contains(key);
    }

    @Override
    public <T> T get(@NonNull String key, Class<T> stored) {
        String s = preferences.getString(key, null);
        if (s != null) {
            return (T) gson.fromJson(s, stored);
        }
        return null;
    }

    @Override
    public void remove(@NonNull String key) {
        preferences.edit()
                .remove(key)
                .apply();
    }

    @Override
    public void clear() {
        preferences.edit()
                .clear()
                .apply();
    }
}
