package com.example.yandextranslatordemo.domain;


import com.example.yandextranslatordemo.data.realm.model.RealmTranslation;

import io.realm.Realm;
import io.realm.RealmResults;

public interface DataManager {

    boolean containsLangs(String ui);

    void getLangs(final String ui, DataRequestListener<Languages> listener);

    void translate(Realm uiRealm, String direction, String originText, DataRequestListener<RealmTranslation> listener);

    void getById(Realm uiRealm, String id, DataRequestListener<RealmTranslation> listener);

    void getHistory(Realm uiRealm, DataRequestListener<RealmResults<RealmTranslation>> listener);

    void setFavorite(Realm uiRealm, final RealmTranslation realmTranslation, boolean favorite);

    void remove(Realm uiRealm, RealmTranslation translation);

    void clearHistory(Realm uiRealm);
}
