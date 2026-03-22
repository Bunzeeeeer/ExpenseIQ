package com.bunzeeeeer.expenseiq.feature.budget.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bunzeeeeer.expenseiq.core.domain.model.Budget;
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.feature.budget.data.BudgetFeatureRepository;

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
public class AddEditBudgetViewModel extends ViewModel {

    private final BudgetFeatureRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public AddEditBudgetViewModel(BudgetFeatureRepository repository) {
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

    // ─── Actions ──────────────────────────────────────────────────────────────

    public void saveBudget(Budget budget) {
        if (budget.getId() == 0) {
            addBudget(budget);
        } else {
            updateBudget(budget);
        }
    }

    private void addBudget(Budget budget) {
        disposables.add(
                repository.addBudget(budget)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> saveSuccess.setValue(true),
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    private void updateBudget(Budget budget) {
        disposables.add(
                repository.updateBudget(budget)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> saveSuccess.setValue(true),
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    public void deleteBudget(Budget budget) {
        disposables.add(
                repository.deleteBudget(budget)
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