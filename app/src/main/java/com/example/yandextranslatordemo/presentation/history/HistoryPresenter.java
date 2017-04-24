package com.example.yandextranslatordemo.presentation.history;


import com.example.yandextranslatordemo.data.realm.model.RealmTranslation;
import com.example.yandextranslatordemo.domain.DataManager;
import com.example.yandextranslatordemo.domain.DataManagerImpl;
import com.example.yandextranslatordemo.domain.DataRequestListener;
import com.example.yandextranslatordemo.presentation.event_bus.OpenTranslation;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import org.greenrobot.eventbus.EventBus;

import io.realm.Realm;
import io.realm.RealmResults;
import timber.log.Timber;

public class HistoryPresenter extends MvpBasePresenter<HistoryView> {


    private RealmResults<RealmTranslation> history;
    private boolean isLoading;
    private DataManager dataManager = DataManagerImpl.getInstance();
    private Realm realm;

    @Override
    public void attachView(HistoryView view) {
        super.attachView(view);
        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        hideAll(view);
        if (history != null) {
            view.setHistory(history);
            view.showContent(true);
        } else if (isLoading) {
            view.showProgress(true);
        } else {
            view.showProgress(true);
            loadHistory();
        }
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        Timber.d("detachView " + retainInstance);
        if (!retainInstance && realm != null) {
            realm.close();
            realm = null;
        }
    }

    public void onRetryPressed() {
        HistoryView view = getView();
        hideAll(view);
        view.showProgress(true);
        if (!isLoading) {
            loadHistory();
        }
    }

    public void changeFavorite(RealmTranslation translation) {
        dataManager.setFavorite(realm, translation, !translation.isFavorite());
    }

    public void onItemClick(RealmTranslation translation) {
        EventBus.getDefault().post(new OpenTranslation(translation.getId()));
    }

    public void clearHistory() {
        dataManager.clearHistory(realm);
    }

    private void loadHistory() {
        Timber.d("Load History");
        isLoading = true;
        dataManager.getHistory(realm, new DataRequestListener<RealmResults<RealmTranslation>>() {
            @Override
            public void onSuccess(RealmResults<RealmTranslation> realmTranslations) {
                isLoading = false;
                HistoryView view = getView();
                if (view != null) {
                    hideAll(view);
                    view.showContent(true);
                    HistoryPresenter.this.history = realmTranslations;
                    view.setHistory(realmTranslations);
                    view.setFavorites(realmTranslations.where().equalTo("isFavorite", true).findAll());
                }
                Timber.d("onSuccess");
            }

            @Override
            public void onError(String s) {
                isLoading = false;
                HistoryView view = getView();
                if (view != null) {
                    hideAll(view);
                    view.showError(s);
                }
                Timber.d("onError");
            }
        });
    }

    private void hideAll(HistoryView view) {
        Timber.d("History hide all");
        view.showContent(false);
        view.showProgress(false);
        view.showError("");
    }
}
