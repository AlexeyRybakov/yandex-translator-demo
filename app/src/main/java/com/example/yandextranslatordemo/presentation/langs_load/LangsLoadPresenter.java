package com.example.yandextranslatordemo.presentation.langs_load;


import com.example.yandextranslatordemo.MyApp;
import com.example.yandextranslatordemo.domain.DataManager;
import com.example.yandextranslatordemo.domain.DataManagerImpl;
import com.example.yandextranslatordemo.domain.DataRequestListener;
import com.example.yandextranslatordemo.domain.Languages;
import com.example.yandextranslatordemo.presentation.LacaleUtil;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;

public class LangsLoadPresenter extends MvpBasePresenter<LangsLoadView> {

    private DataManager dataManager = DataManagerImpl.getInstance();
    private boolean loading = false;
    private boolean retry = false;

    private void hideAll() {
        LangsLoadView view = getView();
        if (view != null) {
            view.setRetry(false);
            view.setLoading(false);
        }
    }

    public void onPostCreate() {
        LangsLoadView view = getView();
        if (view != null) {
            hideAll();
            if (!retry && !loading){
                //first start
                view.setLoading(true);
                loadLangs();
            }else if(loading){
                view.setLoading(true);
            }else{
                view.setRetry(true);
            }
        }
    }

    public void onRetryClick() {
        LangsLoadView view = getView();
        if (view != null && !loading) {
            view.setLoading(true);
            view.setRetry(false);
            loadLangs();
        }
    }


    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
    }

    private void loadLangs() {
        loading = true;
        retry = false;
        dataManager.getLangs(LacaleUtil.getUiLanguage(MyApp.context), new DataRequestListener<Languages>() {
            @Override
            public void onSuccess(Languages languages) {
                loading = false;

                LangsLoadView view = getView();
                if (view != null) {
                    view.startMain();
                }
            }

            @Override
            public void onError(String s) {
                loading = false;
                retry = true;
                LangsLoadView view = getView();
                if (view != null) {
                    view.setLoading(false);
                    view.setRetry(true);
                }
            }
        });
    }
}
