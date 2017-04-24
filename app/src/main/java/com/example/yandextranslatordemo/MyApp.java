package com.example.yandextranslatordemo;


import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import io.realm.Realm;
import timber.log.Timber;

public class MyApp extends Application {


    @SuppressLint("StaticFieldLeak")
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        initTimber();
        Realm.init(context);
    }

    private void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

}
