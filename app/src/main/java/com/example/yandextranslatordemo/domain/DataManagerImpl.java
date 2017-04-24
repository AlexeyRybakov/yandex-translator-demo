package com.example.yandextranslatordemo.domain;


import android.os.Handler;
import android.os.Looper;

import com.example.yandextranslatordemo.MyApp;
import com.example.yandextranslatordemo.data.DataMapper;
import com.example.yandextranslatordemo.data.network.NetworkDataManager;
import com.example.yandextranslatordemo.data.network.NetworkDataManagerImpl;
import com.example.yandextranslatordemo.data.network.exceptions.NetworkException;
import com.example.yandextranslatordemo.data.prefs_store.PrefsStore;
import com.example.yandextranslatordemo.data.prefs_store.PrefsStoreImpl;
import com.example.yandextranslatordemo.data.realm.model.RealmTranslation;
import com.google.gson.Gson;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

import static android.content.Context.MODE_PRIVATE;
import static com.example.yandextranslatordemo.Const.PREFS_LANGS;
import static com.example.yandextranslatordemo.Const.PREFS_NAME;

public class DataManagerImpl implements DataManager {

    private ExecutorService executorService;
    private Handler uiHandler;
    private NetworkDataManager networkDataManager;
    private PrefsStore prefsStore;

    private static DataManagerImpl instansce;

    public static DataManagerImpl getInstance() {
        if (instansce == null) {
            instansce = new DataManagerImpl(
                    new PrefsStoreImpl(
                            MyApp.context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE),
                            new Gson()),
                    new NetworkDataManagerImpl(),
                    Executors.newSingleThreadExecutor()
            );
        }
        return instansce;
    }

    private DataManagerImpl(PrefsStore prefsStore, NetworkDataManager networkManager, ExecutorService executorService) {
        this.executorService = executorService;
        this.uiHandler = new Handler(Looper.getMainLooper());
        this.prefsStore = prefsStore;
        this.networkDataManager = networkManager;
    }


    @Override
    public boolean containsLangs(String ui) {
        String key = PREFS_LANGS + ui;
        return prefsStore.contains(key);
    }

    @Override
    public void getLangs(final String ui, final DataRequestListener<Languages> listener) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                String key = PREFS_LANGS + ui;
                if (prefsStore.contains(key)) {
                    final Languages languages = prefsStore.get(key, Languages.class);
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onSuccess(languages);
                        }
                    });
                } else {
                    try {
                        final Languages languages = networkDataManager.getLangs(ui);
                        prefsStore.put(key, languages);
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onSuccess(languages);
                            }
                        });
                    } catch (final NetworkException e) {
                        uiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onError(e.getMessage());
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void translate(final Realm uiRealm, final String direction, final String originText, final DataRequestListener<RealmTranslation> listener) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Translation translation = networkDataManager.translate(direction, originText);
                    final RealmTranslation realmTranslation = DataMapper.transform(translation);
                    final Realm realm = Realm.getDefaultInstance();
                    try {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealm(realmTranslation);
                            }
                        });
                    } finally {
                        realm.close();
                    }
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (uiRealm.isClosed()) {
                                listener.onError("Realm closed");
                                return;
                            }
                            final RealmTranslation attached = uiRealm.where(RealmTranslation.class).equalTo("id", realmTranslation.getId()).findFirst();
                            if (attached != null) {
                                listener.onSuccess(attached);
                            } else {
                                uiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        listener.onSuccess(realmTranslation);
                                    }
                                });
                            }
                        }
                    });
                } catch (final NetworkException e) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(e.getMessage());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void getById(Realm uiRealm, String id, DataRequestListener<RealmTranslation> listener) {
        //Should be fast operation
        RealmTranslation translation = uiRealm.where(RealmTranslation.class).equalTo("id", id).findFirst();
        listener.onSuccess(translation);
    }

    /**
     * prevent the results from being garbage collected
     * https://github.com/realm/realm-java/issues/2853
     */
    private RealmResults<RealmTranslation> temp;

    @Override
    public void getHistory(Realm uiRealm, final DataRequestListener<RealmResults<RealmTranslation>> listener) {
        temp = uiRealm.where(RealmTranslation.class).findAllSortedAsync("createdAt", Sort.DESCENDING);
        temp.addChangeListener(new RealmChangeListener<RealmResults<RealmTranslation>>() {
            @Override
            public void onChange(RealmResults<RealmTranslation> element) {
                listener.onSuccess(temp);
                temp.removeChangeListener(this);
                temp = null;
            }
        });
    }

    @Override
    public void setFavorite(Realm uiRealm, final RealmTranslation realmTranslation, final boolean favorite) {
        uiRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realmTranslation.setFavorite(favorite);
            }
        });
    }

    @Override
    public void remove(Realm uiRealm, final RealmTranslation translation) {
        uiRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                translation.deleteFromRealm();
            }
        });
    }

    @Override
    public void clearHistory(Realm uiRealm) {
        uiRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(RealmTranslation.class);
            }
        });

    }

}
