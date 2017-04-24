package com.example.yandextranslatordemo.presentation.translator;


import android.text.TextUtils;

import com.example.yandextranslatordemo.data.realm.model.RealmTranslation;
import com.example.yandextranslatordemo.domain.DataManager;
import com.example.yandextranslatordemo.domain.DataManagerImpl;
import com.example.yandextranslatordemo.domain.DataRequestListener;
import com.example.yandextranslatordemo.domain.Languages;
import com.example.yandextranslatordemo.presentation.event_bus.MessageReceiver;
import com.example.yandextranslatordemo.presentation.event_bus.OpenTranslation;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import timber.log.Timber;

public class TranslatorPresenter extends MvpBasePresenter<TranslatorView> implements MessageReceiver {

    private DataManager dataManager = DataManagerImpl.getInstance();
    private String lang;
    private List<UiLang> langs;
    private int originLang;
    private int translationLang;
    private Realm realm;
    private RealmTranslation translation;
    private boolean loading;

    @Override
    public void attachView(TranslatorView view) {
        super.attachView(view);
        EventBus.getDefault().register(this);
        hideAll();

        if (realm == null) {
            realm = Realm.getDefaultInstance();
        }
        if (lang == null ||
                !lang.equals(view.getUILang())) {
            view.showProgress(true);
            loadLangs(view.getUILang());
        } else {
            if (loading) {
                view.showProgress(true);
            } else if (translation == null && !TextUtils.isEmpty(view.getInput())) {
                view.showProgress(true);
                onInputIvent(view.getInput());
            } else {
                view.showContent(true);
                view.showTranslation(translation);
            }
        }
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        EventBus.getDefault().unregister(this);
        if (!retainInstance && realm != null) {
            realm.close();
            realm = null;
            translation = null;
        }
    }

    public void changeFavorite() {
        if (translation != null) {
            dataManager.setFavorite(realm, translation, !translation.isFavorite());
            getView().showTranslation(translation);
        }
    }

    private void setLocalizedLangs(Languages lang) {
        this.lang = lang.uiLanguage;
        langs = new ArrayList<>();
        for (Map.Entry<String, String> stringStringEntry : lang.langs.entrySet()) {
            langs.add(new UiLang(stringStringEntry.getKey(), stringStringEntry.getValue()));
        }
        final Collator collator = Collator.getInstance();
        Collections.sort(langs, new Comparator<UiLang>() {
            @Override
            public int compare(UiLang c1, UiLang c2) {
                return collator.compare(c1.uiLand, c2.uiLand);
            }
        });
        originLang = translationLang = 0;
        onLeftLangChoice(originLang);
        onRightLangChoice(translationLang);
    }

    public void onClearInput() {
        translation = null;
        getView().showTranslation(null);
        getView().clearInput();
    }

    public void onSwapClick() {
        int temp = originLang;
        originLang = translationLang;
        translationLang = temp;
        getView().setLeftLang(langs.get(originLang).uiLand);
        getView().setRightLang(langs.get(translationLang).uiLand);
    }

    public void onLeftLangClick() {
        List<String> uiLangs = new ArrayList<>(langs.size());
        for (UiLang uiLang : langs) {
            uiLangs.add(uiLang.uiLand);
        }
        getView().showLeftLangChooseDialog(uiLangs);
    }

    public void onRightLangClick() {
        List<String> uiLangs = new ArrayList<>(langs.size());
        for (UiLang uiLang : langs) {
            uiLangs.add(uiLang.uiLand);
        }
        getView().showRightLangChooseDialog(uiLangs);
    }

    public void onLeftLangChoice(int position) {
        originLang = position;
        getView().setLeftLang(langs.get(position).uiLand);
    }

    public void onRightLangChoice(int position) {
        translationLang = position;
        getView().setRightLang(langs.get(position).uiLand);
    }

    private void loadLangs(String ui) {
        dataManager.getLangs(ui, new DataRequestListener<Languages>() {
            @Override
            public void onSuccess(Languages languages) {
                TranslatorView view = getView();
                if (view != null) {
                    setLocalizedLangs(languages);
                    hideAll();
                    view.showContent(true);
                }
            }

            @Override
            public void onError(String s) {
                Timber.d("error");
            }
        });
    }

    public void onRetry(String s) {
        onInputIvent(s);
    }

    public void onInputIvent(String s) {
        if (TextUtils.isEmpty(s)) return;
        TranslatorPresenter.this.translation = null;
        loading = true;
        hideAll();
        getView().showProgress(true);
        dataManager.translate(realm, generateDirection(), s, new DataRequestListener<RealmTranslation>() {
            @Override
            public void onSuccess(RealmTranslation translation) {
                loading = false;
                TranslatorView view = getView();
                if (view != null) {
                    setTranslation(translation);
                    hideAll();
                    view.showContent(true);
                    view.showTranslation(translation);

                }
            }

            @Override
            public void onError(String s) {
                loading = false;
                TranslatorView view = getView();
                if (view != null) {
                    hideAll();
                    view.showRetry(true);
                }
            }
        });
    }

    private void setTranslation(final RealmTranslation realmTranslation) {
        translation = realmTranslation;
        realmTranslation.addChangeListener(new RealmChangeListener<RealmTranslation>() {
            @Override
            public void onChange(RealmTranslation element) {
                if (!element.isValid()) {
                    translation = null;
                }
                TranslatorView view = getView();
                if (view != null) {
                    view.showTranslation(translation);
                }
            }
        });
    }

    private String generateDirection() {
        return langs.get(originLang).lang + "-" + langs.get(translationLang).lang;
    }

    private void hideAll() {
        TranslatorView view = getView();
        if (view != null) {
            view.showRetry(false);
            view.showProgress(false);
            view.showContent(false);
        }
    }

    private int getLangPosition(String lang) {
        for (int i = 0; i < langs.size(); i++) {
            if (langs.get(i).lang.equals(lang)) {
                return i;
            }
        }
        return -1;
    }

    @Subscribe
    @Override
    public void onMessageEvent(OpenTranslation event) {
        if (isViewAttached()) {
            dataManager.getById(realm, event.id, new DataRequestListener<RealmTranslation>() {
                @Override
                public void onSuccess(RealmTranslation translation) {
                    TranslatorView view = getView();
                    if (view != null) {
                        setTranslation(translation);
                        hideAll();
                        view.showContent(true);
                        view.showTranslation(translation);
                        view.setInput(translation.getOriginText());

                        if (langs != null) {
                            String[] directions = translation.getDirection().split("-");
                            int left = getLangPosition(directions[0]);
                            int right = getLangPosition(directions[1]);
                            if (left > 0 && right > 0) {
                                originLang = left;
                                translationLang = right;
                                view.setLeftLang(langs.get(originLang).uiLand);
                                view.setRightLang(langs.get(translationLang).uiLand);
                            }
                        }


                    }
                }

                @Override
                public void onError(String s) {

                }
            });
        }
    }
}
