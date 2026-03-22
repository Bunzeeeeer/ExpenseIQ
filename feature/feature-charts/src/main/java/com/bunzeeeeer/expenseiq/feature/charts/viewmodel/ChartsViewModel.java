package com.bunzeeeeer.expenseiq.feature.charts.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bunzeeeeer.expenseiq.feature.charts.data.ChartsData;
import com.bunzeeeeer.expenseiq.feature.charts.data.ChartsRepository;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-22-2026
 *
 */
public class ChartsViewModel extends ViewModel {

    private final ChartsRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<ChartsData> chartsData = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ChartsViewModel(ChartsRepository repository) {
        this.repository = repository;
        loadChartsData();
    }

    // ─── Data Loading ─────────────────────────────────────────────────────────

    private void loadChartsData() {
        disposables.add(
                Flowable.combineLatest(
                                repository.getAllExpenses(),
                                repository.getAllCategories(),
                                ChartsData::new
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                chartsData::setValue,
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    // ─── Exposed LiveData ─────────────────────────────────────────────────────

    public LiveData<ChartsData> getChartsData() { return chartsData; }
    public LiveData<String> getError() { return error; }

    // ─── Cleanup ──────────────────────────────────────────────────────────────

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}