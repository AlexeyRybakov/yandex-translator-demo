package com.example.yandextranslatordemo.presentation.translator;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.yandextranslatordemo.R;
import com.example.yandextranslatordemo.data.realm.model.RealmTranslation;
import com.example.yandextranslatordemo.databinding.FragmentTranslatorBinding;
import com.example.yandextranslatordemo.presentation.LacaleUtil;
import com.hannesdorfmann.mosby3.mvp.MvpFragment;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TranslatorFragment extends MvpFragment<TranslatorView, TranslatorPresenter> implements TranslatorView {

    FragmentTranslatorBinding binding;
    private static final String INPUT_TEXT_STATE = "INPUT_TEXT_STATE";

    public TranslatorFragment() {
        // Required empty public constructor
    }

    @Override
    public TranslatorPresenter createPresenter() {
        return new TranslatorPresenter();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(INPUT_TEXT_STATE, binding.inputText.getText().toString());
        super.onSaveInstanceState(outState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_translator, container, false);
        if (savedInstanceState != null) {
            //restore state before presenter attachView
            binding.inputText.setText(savedInstanceState.getString(INPUT_TEXT_STATE, ""));
        }
        binding.languageBar.langLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onLeftLangClick();
            }
        });
        binding.languageBar.langRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onRightLangClick();
            }
        });
        binding.languageBar.swapLangs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSwapClick();
            }
        });

        binding.inputText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    presenter.onInputIvent(binding.inputText.getText().toString().trim());
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        binding.favorite.setVisibility(View.GONE);
        binding.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.changeFavorite();
            }
        });
        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onClearInput();
            }
        });
        binding.translationLayout.setOnRetryButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onRetry(binding.inputText.getText().toString());
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public String getInput() {
        return binding.inputText.getText().toString().trim();
    }

    @Override
    public void setInput(String s) {
        binding.inputText.setText(s);
    }

    @Override
    public void showProgress(boolean show) {
        binding.translationLayout.showProgress(show);
    }

    @Override
    public void showRetry(boolean show) {
        binding.translationLayout.showRetry(show);
    }

    @Override
    public void showContent(boolean show) {
        binding.translationLayout.showContent(show);
    }

    @Override
    public void showTranslation(RealmTranslation translation) {
        if (translation == null) {
            binding.translation.setText("");
            binding.favorite.setVisibility(View.GONE);
        } else {
            binding.translation.setText(translation.getTranslation());
            binding.favorite.setVisibility(View.VISIBLE);
            binding.favorite.setActivated(translation.isFavorite());
        }

    }

    @Override
    public void clearInput() {
        binding.inputText.getText().clear();
    }

    @Override
    public String getUILang() {
        return LacaleUtil.getUiLanguage(getContext());
    }

    @Override
    public void setLeftLang(String s) {
        binding.languageBar.langLeft.setText(s);
    }

    @Override
    public void setRightLang(String s) {
        binding.languageBar.langRight.setText(s);
    }

    @Override
    public void showLeftLangChooseDialog(List<String> items) {
        showDialog(items, new OnLangSelected() {
            @Override
            public void onSelected(int position) {
                presenter.onLeftLangChoice(position);
            }
        });
    }

    @Override
    public void showRightLangChooseDialog(List<String> items) {
        showDialog(items, new OnLangSelected() {
            @Override
            public void onSelected(int position) {
                presenter.onRightLangChoice(position);
            }
        });
    }

    private void showDialog(final List<String> items, final OnLangSelected listener) {
        new MaterialDialog.Builder(getContext())
                .items(items)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int position, CharSequence text) {

                        listener.onSelected(position);
                    }
                })
                .title(R.string.choose_lang)
                .show();
    }

    private interface OnLangSelected {
        void onSelected(int position);
    }

}
