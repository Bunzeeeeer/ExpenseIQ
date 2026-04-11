package com.bunzeeeeer.expenseiq.auth.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bunzeeeeer.expenseiq.auth.data.AuthRepository;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-29-2026
 *
 */
public class RegisterViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Boolean> registerSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> registerError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public RegisterViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<Boolean> getRegisterSuccess() { return registerSuccess; }
    public LiveData<String> getRegisterError() { return registerError; }
    public LiveData<Boolean> getLoading() { return loading; }

    public void register(String email, String password) {
        loading.setValue(true);
        compositeDisposable.add(
                authRepository.register(email, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    loading.setValue(false);
                                    registerSuccess.setValue(true);
                                },
                                error -> {
                                    loading.setValue(false);
                                    registerError.setValue(error.getMessage());
                                }
                        )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}