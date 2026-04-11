package com.bunzeeeeer.expenseiq.auth.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bunzeeeeer.expenseiq.auth.data.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-29-2026
 *
 */
public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<FirebaseUser> loginSuccess = new MutableLiveData<>();
    private final MutableLiveData<String> loginError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public LiveData<FirebaseUser> getLoginSuccess() { return loginSuccess; }
    public LiveData<String> getLoginError() { return loginError; }
    public LiveData<Boolean> getLoading() { return loading; }

    public void login(String email, String password) {
        loading.setValue(true);
        compositeDisposable.add(
                authRepository.login(email, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                user -> {
                                    loading.setValue(false);
                                    loginSuccess.setValue(user);
                                },
                                error -> {
                                    loading.setValue(false);
                                    loginError.setValue(error.getMessage());
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