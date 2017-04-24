package com.example.yandextranslatordemo.presentation.langs_load;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.example.yandextranslatordemo.presentation.MainActivity;
import com.example.yandextranslatordemo.R;
import com.example.yandextranslatordemo.databinding.ActivityLangsLoadBinding;
import com.hannesdorfmann.mosby3.mvp.MvpActivity;

public class LangsLoadActivity extends MvpActivity<LangsLoadView, LangsLoadPresenter> implements LangsLoadView {

    ActivityLangsLoadBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_langs_load);
        binding.errorRetry.retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onRetryClick();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        presenter.onPostCreate();
    }

    @NonNull
    @Override
    public LangsLoadPresenter createPresenter() {
        return new LangsLoadPresenter();
    }


    @Override
    public void setLoading(boolean show) {
        binding.progress.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setRetry(boolean show) {
        binding.errorRetry.errorRetry.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void startMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
