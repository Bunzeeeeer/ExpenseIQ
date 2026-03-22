package com.bunzeeeeer.expenseiq.core.ui.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseActivity extends AppCompatActivity {

    protected final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initDesign();
        initViewModel();
        initObservers();
    }

    protected abstract int getLayoutId();

    protected abstract void initDesign();

    protected abstract void initViewModel();

    protected abstract void initObservers();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}