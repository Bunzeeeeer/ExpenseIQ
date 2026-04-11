package com.bunzeeeeer.expenseiq.auth.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-29-2026
 *
 */
public class AuthRepository {

    private final FirebaseAuth firebaseAuth;

    public AuthRepository() {
        this.firebaseAuth = FirebaseAuth.getInstance();
    }

    public Single<FirebaseUser> login(String email, String password) {
        return Single.create(emitter ->
                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(result -> emitter.onSuccess(result.getUser()))
                        .addOnFailureListener(emitter::onError)
        );
    }

    public Completable register(String email, String password) {
        return Completable.create(emitter ->
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener(result -> emitter.onComplete())
                        .addOnFailureListener(emitter::onError)
        );
    }

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    public void logout() {
        firebaseAuth.signOut();
    }
}