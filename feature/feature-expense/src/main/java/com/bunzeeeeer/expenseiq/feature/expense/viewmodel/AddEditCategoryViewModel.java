package com.bunzeeeeer.expenseiq.feature.expense.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.feature.expense.data.ExpenseFeatureRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-22-2026
 *
 */
public class AddEditCategoryViewModel extends ViewModel {

    private final ExpenseFeatureRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Boolean> saveSuccess = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public AddEditCategoryViewModel(ExpenseFeatureRepository repository) {
        this.repository = repository;
    }

    // ─── Actions ──────────────────────────────────────────────────────────────

    public void saveCategory(Category category) {
        if (category.getId() == 0) {
            addCategory(category);
        } else {
            updateCategory(category);
        }
    }

    private void addCategory(Category category) {
        disposables.add(
                repository.addCategory(category)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> saveSuccess.setValue(true),
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    private void updateCategory(Category category) {
        disposables.add(
                repository.updateCategory(category)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> saveSuccess.setValue(true),
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    public void deleteCategory(Category category) {
        disposables.add(
                repository.deleteCategory(category)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> deleteSuccess.setValue(true),
                                e -> error.setValue(e.getMessage())
                        )
        );
    }

    // ─── Exposed LiveData ─────────────────────────────────────────────────────

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