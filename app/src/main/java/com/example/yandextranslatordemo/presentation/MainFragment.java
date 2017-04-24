package com.example.yandextranslatordemo.presentation;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.yandextranslatordemo.R;
import com.example.yandextranslatordemo.databinding.FragmentMainBinding;
import com.example.yandextranslatordemo.presentation.event_bus.MessageReceiver;
import com.example.yandextranslatordemo.presentation.event_bus.OpenTranslation;
import com.example.yandextranslatordemo.presentation.history.HistoryFragment;
import com.example.yandextranslatordemo.presentation.translator.TranslatorFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainFragment extends Fragment implements MessageReceiver {


    private FragmentMainBinding binding;

    public MainFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_main, container, false);
        binding.tabPager.setAdapter(new TabPagerAdapter(getChildFragmentManager()));
        binding.tabPager.setOffscreenPageLimit(3);
        binding.tabPager.setSwipeLocked(true);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.tab_translate) {
                    binding.tabPager.setCurrentItem(0);
                } else if (item.getItemId() == R.id.tab_history) {
                    binding.tabPager.setCurrentItem(1);
               /* }else if(item.getItemId()==R.id.tab_settings){
                    binding.tabPager.setCurrentItem(2);*/
                } else {
                    return false;
                }
                return true;
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Subscribe
    @Override
    public void onMessageEvent(OpenTranslation event) {
        if (isResumed()) {
            binding.tabPager.setCurrentItem(0);
            binding.bottomNavigation.setSelectedItemId(R.id.tab_translate);
        }
    }

    private static class TabPagerAdapter extends FragmentPagerAdapter {
        TabPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new TranslatorFragment();
                case 1:
                    return new HistoryFragment();

            }
            return null;
        }
    }


}
