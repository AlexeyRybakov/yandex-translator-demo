package com.example.yandextranslatordemo.presentation.history;


import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yandextranslatordemo.R;
import com.example.yandextranslatordemo.data.realm.model.RealmTranslation;
import com.example.yandextranslatordemo.databinding.FragmentHistoryBinding;
import com.example.yandextranslatordemo.presentation.ContentLayout;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;

import io.realm.RealmResults;


public class HistoryFragment extends MvpFragment<HistoryView, HistoryPresenter> implements HistoryView {

    FragmentHistoryBinding binding;
    HistoryAdapter historyAdapter;
    HistoryAdapter favoritesAdapter;
    ContentLayout[] items;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public HistoryPresenter createPresenter() {
        return new HistoryPresenter();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false);
        binding.toolbar.inflateMenu(R.menu.history_toolbar);
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.clear) {
                    presenter.clearHistory();
                    return true;
                }
                return false;
            }
        });
        initViewPager(inflater);
        return binding.getRoot();
    }

    public void initViewPager(LayoutInflater inflater) {
        Resources resources = getResources();
        items = new ContentLayout[2];
        items[0] = (ContentLayout) inflater.inflate(R.layout.history_tab_view, binding.viewpager, false);
        items[1] = (ContentLayout) inflater.inflate(R.layout.history_tab_view, binding.viewpager, false);
        setOnRetryClickListener(items[0]);
        setOnRetryClickListener(items[1]);
        RecyclerView historyRecyclerView = items[0].getContent();
        RecyclerView favoritesRecyclerView = items[1].getContent();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        historyRecyclerView.setLayoutManager(linearLayoutManager);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        historyAdapter = new HistoryAdapter(null, new HistoryAdapter.OnItemClick() {
            @Override
            public void onClick(int position) {
                presenter.changeFavorite(historyAdapter.getItem(position));
            }
        }, new HistoryAdapter.OnItemClick() {
            @Override
            public void onClick(int position) {
                presenter.onItemClick(historyAdapter.getItem(position));
            }
        }, getContext());
        favoritesAdapter = new HistoryAdapter(null, new HistoryAdapter.OnItemClick() {
            @Override
            public void onClick(int position) {
                presenter.changeFavorite(favoritesAdapter.getItem(position));
            }
        }, new HistoryAdapter.OnItemClick() {
            @Override
            public void onClick(int position) {
                presenter.onItemClick(favoritesAdapter.getItem(position));
            }
        }, getContext());
        registerAdapterEmptyObserver(historyAdapter, items[0]);
        registerAdapterEmptyObserver(favoritesAdapter, items[1]);
        historyRecyclerView.setAdapter(historyAdapter);
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        String[] titles = new String[]{resources.getString(R.string.history), resources.getString(R.string.favorites)};
        binding.viewpager.setAdapter(new ViewPagerAdapter(items, titles));
        binding.tabs.setupWithViewPager(binding.viewpager);

        binding.tabs.setTabTextColors(resources.getColor(R.color.normal_tab_color), resources.getColor(R.color.selected_tab_color));


    }

    private void registerAdapterEmptyObserver(final RecyclerView.Adapter adapter, final ContentLayout contentLayout) {
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                contentLayout.showEmptyView(adapter.getItemCount() == 0);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                contentLayout.showEmptyView(adapter.getItemCount() == 0);
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                contentLayout.showEmptyView(adapter.getItemCount() == 0);
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                contentLayout.showEmptyView(adapter.getItemCount() == 0);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                contentLayout.showEmptyView(adapter.getItemCount() == 0);
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                contentLayout.showEmptyView(adapter.getItemCount() == 0);
            }
        });
    }

    private void setOnRetryClickListener(final ContentLayout contentLayout) {
        contentLayout.setOnRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onRetryPressed();
            }
        });
    }

    @Override
    public void setHistory(RealmResults<RealmTranslation> history) {
        historyAdapter.updateData(history);
    }

    @Override
    public void setFavorites(RealmResults<RealmTranslation> favorites) {
        favoritesAdapter.updateData(favorites);
    }

    @Override
    public void showProgress(boolean show) {
        for (ContentLayout item : items) {
            item.showProgress(show);
        }
    }

    @Override
    public void showError(String message) {
        if (!TextUtils.isEmpty(message)) {
            Toast.makeText(getContext().getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
        for (ContentLayout item : items) {
            item.showRetry(!TextUtils.isEmpty(message));
        }
    }

    @Override
    public void showContent(boolean show) {
        for (ContentLayout item : items) {
            item.showContent(show);
        }
    }


    private static class ViewPagerAdapter extends PagerAdapter {

        private final String[] titles;
        private final View[] items;

        ViewPagerAdapter(View[] items, String[] titles) {
            this.titles = titles;
            this.items = items;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(items[position]);
            return items[position];
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return items.length;
        }


        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

    }

}
