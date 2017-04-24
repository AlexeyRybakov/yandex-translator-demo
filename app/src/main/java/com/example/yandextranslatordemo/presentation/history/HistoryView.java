package com.example.yandextranslatordemo.presentation.history;


import com.example.yandextranslatordemo.data.realm.model.RealmTranslation;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import io.realm.RealmResults;

public interface HistoryView extends MvpView {

    void setHistory(RealmResults<RealmTranslation> history);

    void setFavorites(RealmResults<RealmTranslation> favorites);

    void showProgress(boolean show);

    void showError(String message);

    void showContent(boolean show);


}
