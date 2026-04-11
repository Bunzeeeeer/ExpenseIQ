package com.bunzeeeeer.expenseiq.feature.expense.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;
import com.bunzeeeeer.expenseiq.feature.expense.data.ExpenseFeatureRepository;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-15-2026
 *
 */
public class AddEditExpenseViewModel extends ViewModel {

    private final ExpenseFeatureRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<Expense> expenseToEdit = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public AddEditExpenseViewModel(ExpenseFeatureRepository repository) {
        this.repository = repository;
        loadCategories();
    }

    // ─── Data Loading ─────────────────────────────────────────────────────────

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

    public void loadExpenseById(long id) {
        disposables.add(
                repository.getExpenseById(id)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                expenseToEdit::setValue,
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    // ─── Actions ──────────────────────────────────────────────────────────────

    public void saveExpense(Expense expense) {
        if (expense.getId() == 0) {
            addExpense(expense);
        } else {
            updateExpense(expense);
        }
    }

    private void addExpense(Expense expense) {
        disposables.add(
                repository.addExpense(expense)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> saveSuccess.setValue(true),
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    private void updateExpense(Expense expense) {
        disposables.add(
                repository.updateExpense(expense)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> saveSuccess.setValue(true),
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    public void deleteExpense(Expense expense) {
        disposables.add(
                repository.deleteExpense(expense)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> deleteSuccess.setValue(true),
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    // ─── Exposed LiveData ─────────────────────────────────────────────────────

    public LiveData<List<Category>> getCategories() { return categories; }
    public LiveData<Expense> getExpenseToEdit() { return expenseToEdit; }
    public LiveData<Boolean> getSaveSuccess() { return saveSuccess; }
    public LiveData<Boolean> getDeleteSuccess() { return deleteSuccess; }
    public LiveData<String> getError() { return error; }

    // ─── Cleanup ──────────────────────────────────────────────────────────────

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }
}