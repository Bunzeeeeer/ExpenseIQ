package com.bunzeeeeer.expenseiq.feature.dashboard.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bunzeeeeer.expenseiq.core.domain.model.Budget;
import com.bunzeeeeer.expenseiq.feature.dashboard.data.DashboardData;
import com.bunzeeeeer.expenseiq.feature.dashboard.data.DashboardRepository;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-15-2026
 *
 */
public class DashboardViewModel extends ViewModel {

    private final DashboardRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Double> totalThisMonth = new MutableLiveData<>(0.0);
    private final MutableLiveData<DashboardData> dashboardData = new MutableLiveData<>();
    private final MutableLiveData<List<Budget>> budgets = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public DashboardViewModel(DashboardRepository repository) {
        this.repository = repository;
        loadDashboardData();
    }

    // ─── Data Loading ─────────────────────────────────────────────────────────

    private void loadDashboardData() {
        loadTotalThisMonth();
        loadExpensesAndCategories();
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

    private void loadExpensesAndCategories() {
        disposables.add(
                Flowable.combineLatest(
                                repository.getRecentExpenses(),
                                repository.getAllCategories(),
                                DashboardData::new
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                dashboardData::setValue,
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
    public LiveData<DashboardData> getDashboardData() { return dashboardData; }
    public LiveData<List<Budget>> getBudgets() { return budgets; }
    public LiveData<String> getError() { return error; }

    // ─── Cleanup ──────────────────────────────────────────────────────────────

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}