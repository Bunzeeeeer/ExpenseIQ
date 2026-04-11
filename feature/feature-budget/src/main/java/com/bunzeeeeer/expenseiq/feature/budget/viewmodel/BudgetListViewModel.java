package com.bunzeeeeer.expenseiq.feature.budget.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bunzeeeeer.expenseiq.core.domain.model.Budget;
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.feature.budget.data.BudgetFeatureRepository;

import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-22-2026
 *
 */
public class BudgetListViewModel extends ViewModel {

    private final BudgetFeatureRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<List<Budget>> budgets = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();

    public BudgetListViewModel(BudgetFeatureRepository repository) {
        this.repository = repository;
        loadBudgets();
        loadCategories();
    }

    // ─── Data Loading ─────────────────────────────────────────────────────────

    private void loadBudgets() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        disposables.add(
                repository.getBudgetsByMonth(month, year)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                budgets::setValue,
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    private void loadCategories() {
        disposables.add(
                repository.getAllCategories()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                categories::setValue,
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    // ─── Exposed LiveData ─────────────────────────────────────────────────────

    public LiveData<List<Budget>> getBudgets() { return budgets; }
    public LiveData<String> getError() { return error; }
    public LiveData<List<Category>> getCategories() { return categories; }

    // ─── Cleanup ──────────────────────────────────────────────────────────────

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}