package com.example.yandextranslatordemo.presentation.translator;


import com.example.yandextranslatordemo.data.realm.model.RealmTranslation;
import com.hannesdorfmann.mosby3.mvp.MvpView;

import java.util.List;

public interface TranslatorView extends MvpView {

    String getInput();

    void setInput(String s);

    void showProgress(boolean show);

    void showRetry(boolean show);

    void showContent(boolean show);

    void showTranslation(RealmTranslation translation);

    void clearInput();

    String getUILang();

    void setLeftLang(String s);

    void setRightLang(String s);

    void showLeftLangChooseDialog(List<String> items);

    void showRightLangChooseDialog(List<String> items);


}
