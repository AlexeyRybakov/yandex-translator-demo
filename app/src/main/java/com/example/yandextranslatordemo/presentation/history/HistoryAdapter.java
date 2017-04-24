package com.example.yandextranslatordemo.presentation.history;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yandextranslatordemo.R;
import com.example.yandextranslatordemo.data.realm.model.RealmTranslation;
import com.example.yandextranslatordemo.databinding.HistoryItemBinding;

import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;
import timber.log.Timber;

public class HistoryAdapter extends RealmRecyclerViewAdapter<RealmTranslation, HistoryAdapter.ViewHolder> {

    private LayoutInflater inflater;
    private OnItemClick onItemClick;
    private OnItemClick onFavClick;


    public HistoryAdapter(RealmResults<RealmTranslation> data, OnItemClick onFavClick, OnItemClick onItemClick, Context context) {
        super(data, true);
        inflater = LayoutInflater.from(context);
        this.onFavClick = onFavClick;
        this.onItemClick = onItemClick;
        setHasStableIds(false);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HistoryItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.history_item, parent, false);
        Timber.d("onCreateViewHolder ");
        return new ViewHolder(binding, onFavClick, onItemClick);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RealmTranslation translation = getItem(position);
        holder.binding.direction.setText(translation.getDirection());
        holder.binding.originTextView.setText(translation.getOriginText());
        holder.binding.translationTextView.setText(translation.getTranslation());
        holder.binding.favorite.setActivated(translation.isFavorite());
        Timber.d("OnBind " + position);
    }

    interface OnItemClick {
        void onClick(int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public HistoryItemBinding binding;

        public ViewHolder(HistoryItemBinding binding, final OnItemClick onFavClick, final OnItemClick onItemClick) {
            super(binding.getRoot());
            this.binding = binding;
            binding.favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFavClick.onClick(getAdapterPosition());
                }
            });
            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onClick(getAdapterPosition());
                }
            });
        }
    }
}
