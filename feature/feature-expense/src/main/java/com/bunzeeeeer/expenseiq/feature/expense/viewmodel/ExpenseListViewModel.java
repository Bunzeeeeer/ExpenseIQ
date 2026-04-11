package com.bunzeeeeer.expenseiq.feature.expense.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bunzeeeeer.expenseiq.core.domain.model.Expense;
import com.bunzeeeeer.expenseiq.feature.expense.data.ExpenseFeatureRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-15-2026
 *
 */
public class ExpenseListViewModel extends ViewModel {

    private final ExpenseFeatureRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<List<Expense>> expenses = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ExpenseListViewModel(ExpenseFeatureRepository repository) {
        this.repository = repository;
        loadExpenses();
    }

    // ─── Data Loading ─────────────────────────────────────────────────────────

    private void loadExpenses() {
        disposables.add(
                repository.getAllExpenses()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                expenses::setValue,
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    // ─── Exposed LiveData ─────────────────────────────────────────────────────

    public LiveData<List<Expense>> getExpenses() { return expenses; }
    public LiveData<String> getError() { return error; }

    // ─── Cleanup ──────────────────────────────────────────────────────────────

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}