package com.bunzeeeeer.expenseiq.feature.dashboard.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bunzeeeeer.expenseiq.core.domain.model.Budget;
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;
import com.bunzeeeeer.expenseiq.feature.dashboard.data.DashboardRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DashboardViewModel extends ViewModel {

    private final DashboardRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Double> totalThisMonth = new MutableLiveData<>(0.0);
    private final MutableLiveData<List<Expense>> recentExpenses = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<List<Budget>> budgets = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public DashboardViewModel(DashboardRepository repository) {
        this.repository = repository;
        loadDashboardData();
    }

    // ─── Data Loading ─────────────────────────────────────────────────────────

    private void loadDashboardData() {
        loadTotalThisMonth();
        loadRecentExpenses();
        loadCategories();
        loadBudgets();
    }

    private void loadTotalThisMonth() {
        disposables.add(
                repository.getTotalExpensesThisMonth()
                        .subscribeOn(Schedulers.io())
                        .onErrorReturnItem(0.0)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                total -> totalThisMonth.setValue(total != null ? total : 0.0),
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    private void loadRecentExpenses() {
        disposables.add(
                repository.getRecentExpenses()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                recentExpenses::setValue,
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

    private void loadBudgets() {
        disposables.add(
                repository.getBudgetsThisMonth()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                budgets::setValue,
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    // ─── Exposed LiveData ─────────────────────────────────────────────────────

    public LiveData<Double> getTotalThisMonth() { return totalThisMonth; }
    public LiveData<List<Expense>> getRecentExpenses() { return recentExpenses; }
    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<List<Budget>> getBudgets() { return budgets; }
    public LiveData<String> getError() { return error; }

    // ─── Cleanup ──────────────────────────────────────────────────────────────

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}