package com.example.yandextranslatordemo.presentation.langs_load;


import com.hannesdorfmann.mosby3.mvp.MvpView;

public interface LangsLoadView extends MvpView {

    void setLoading(boolean show);

    void setRetry(boolean show);

    void startMain();

}
