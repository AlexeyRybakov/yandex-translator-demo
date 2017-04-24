package com.example.yandextranslatordemo.presentation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.example.yandextranslatordemo.MyApp;
import com.example.yandextranslatordemo.R;
import com.example.yandextranslatordemo.domain.DataManagerImpl;
import com.example.yandextranslatordemo.presentation.langs_load.LangsLoadActivity;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        listenLifecycle();
        super.onCreate(savedInstanceState);
        if (!DataManagerImpl.getInstance().containsLangs(LacaleUtil.getUiLanguage(MyApp.context))) {
            startActivity(new Intent(getApplicationContext(), LangsLoadActivity.class));
            finish();
        }
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            Timber.d("FragmentCreatedManually");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.root, new MainFragment())
                    .commit();

        } else {
            Timber.d("FragmentCreatedBySystem");
        }
    }

    public void listenLifecycle() {
        getSupportFragmentManager().registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {

            @Override
            public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
                super.onFragmentActivityCreated(fm, f, savedInstanceState);
                Timber.d("onFragmentActivityCreated %s", f);
            }


            @Override
            public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
                super.onFragmentAttached(fm, f, context);
                Timber.d("onFragmentAttached %s", f);
            }

            @Override
            public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
                super.onFragmentCreated(fm, f, savedInstanceState);
                Timber.d("onFragmentCreated %s", f);
            }

            @Override
            public void onFragmentStarted(FragmentManager fm, Fragment f) {
                super.onFragmentStarted(fm, f);
                Timber.d("onFragmentStarted %s", f);
            }

            @Override
            public void onFragmentResumed(FragmentManager fm, Fragment f) {
                super.onFragmentResumed(fm, f);
                Timber.d("onFragmentResumed %s", f);
            }

            @Override
            public void onFragmentPaused(FragmentManager fm, Fragment f) {
                super.onFragmentPaused(fm, f);
                Timber.d("onFragmentPaused %s", f);
            }

            @Override
            public void onFragmentStopped(FragmentManager fm, Fragment f) {
                super.onFragmentStopped(fm, f);
                Timber.d("onFragmentStopped %s", f);
            }

            @Override
            public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
                super.onFragmentDestroyed(fm, f);
                Timber.d("onFragmentDestroyed %s", f);
            }

            @Override
            public void onFragmentDetached(FragmentManager fm, Fragment f) {
                super.onFragmentDetached(fm, f);
                Timber.d("onFragmentDetached %s", f);
            }
        }, false);
    }
}
