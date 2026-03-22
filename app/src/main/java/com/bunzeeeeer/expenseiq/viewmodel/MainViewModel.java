package com.bunzeeeeer.expenseiq.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bunzeeeeer.expenseiq.core.domain.model.Budget;
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;
import com.bunzeeeeer.expenseiq.core.domain.repository.BudgetRepository;
import com.bunzeeeeer.expenseiq.core.domain.repository.CategoryRepository;
import com.bunzeeeeer.expenseiq.core.domain.repository.ExpenseRepository;

import java.util.Calendar;
import java.util.List;

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
public class MainViewModel extends ViewModel {

    private final ExpenseRepository expenseRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Double> totalThisMonth = new MutableLiveData<>(0.0);
    private final MutableLiveData<Double> totalLastMonth = new MutableLiveData<>(0.0);
    private final MutableLiveData<List<Expense>> expensesThisMonth = new MutableLiveData<>();
    private final MutableLiveData<List<Budget>> budgets = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public MainViewModel(
            ExpenseRepository expenseRepository,
            BudgetRepository budgetRepository,
            CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        loadData();
    }

    // ─── Data Loading ─────────────────────────────────────────────────────────

    private void loadData() {
        loadTotalThisMonth();
        loadTotalLastMonth();
        loadExpensesAndCategories();
        loadBudgets();
    }

    private void loadTotalThisMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startDate = cal.getTimeInMillis();
        long endDate = System.currentTimeMillis();

        disposables.add(
                expenseRepository.getTotalExpensesBetween(startDate, endDate)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturnItem(0.0)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                total -> totalThisMonth.setValue(
                                        total != null ? total : 0.0),
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    private void loadTotalLastMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long endDate = cal.getTimeInMillis();
        cal.add(Calendar.MONTH, -1);
        long startDate = cal.getTimeInMillis();

        disposables.add(
                expenseRepository.getTotalExpensesBetween(startDate, endDate)
                        .subscribeOn(Schedulers.io())
                        .onErrorReturnItem(0.0)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                total -> totalLastMonth.setValue(
                                        total != null ? total : 0.0),
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    private void loadExpensesAndCategories() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startDate = cal.getTimeInMillis();
        long endDate = System.currentTimeMillis();

        disposables.add(
                Flowable.combineLatest(
                                expenseRepository.getExpensesByDateRange(
                                        startDate, endDate),
                                categoryRepository.getAllCategories(),
                                (expenses, cats) -> {
                                    expensesThisMonth.postValue(expenses);
                                    categories.postValue(cats);
                                    return true;
                                }
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                result -> { },
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    private void loadBudgets() {
        Calendar cal = Calendar.getInstance();
        int month = cal.get(Calendar.MONTH) + 1;
        int year = cal.get(Calendar.YEAR);

        disposables.add(
                budgetRepository.getBudgetsByMonth(month, year)
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
    public LiveData<Double> getTotalLastMonth() { return totalLastMonth; }
    public LiveData<List<Expense>> getExpensesThisMonth() { return expensesThisMonth; }
    public LiveData<List<Budget>> getBudgets() { return budgets; }
    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<String> getError() { return error; }

    // ─── Cleanup ──────────────────────────────────────────────────────────────

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}