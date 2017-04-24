package com.example.yandextranslatordemo.presentation;


import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.example.yandextranslatordemo.R;

import timber.log.Timber;

public class ContentLayout extends FrameLayout {

    public enum ContentView {
        CONTENT,
        PROGRESS,
        RETRY
    }

    public <T> T getContent() {
        return (T) content;
    }

    private View content;
    private View contentLayout;
    private View progress;
    private View retry;
    private View empty;

    public ContentLayout(@NonNull Context context) {
        super(context);
    }

    public ContentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ContentLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ContentLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnRetryButtonClickListener(View.OnClickListener listener) {
        findViewById(R.id.retry_button).setOnClickListener(listener);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        content = findViewById(R.id.content);
        contentLayout = findViewById(R.id.content_layout);
        if (content == null) {
            content = contentLayout;
        }
        progress = findViewById(R.id.progress);
        retry = findViewById(R.id.error_retry);
        empty = findViewById(R.id.empty_view);
        //content.setVisibility(VISIBLE);
        showEmptyView(true);
    }

    public void showEmptyView(boolean show) {
        Timber.d("showEmptyView %s", show);
        if (empty != null) {
            empty.setVisibility(show ? VISIBLE : GONE);
            content.setVisibility(show ? GONE : VISIBLE);
        }
    }

    public void showContent(boolean show) {
        Timber.d("showContent %s", show);
        contentLayout.setVisibility(show ? VISIBLE : GONE);
    }

    public void showProgress(boolean show) {
        Timber.d("showProgress %s", show);
        progress.setVisibility(show ? VISIBLE : GONE);
    }

    public void showRetry(boolean show) {
        Timber.d("showRetry %s", show);
        retry.setVisibility(show ? VISIBLE : GONE);
    }

    public void setRetryText(String text) {
        //TODO:implement
    }
}
