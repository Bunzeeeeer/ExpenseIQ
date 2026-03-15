package com.bunzeeeeer.expenseiq.core.domain.repository;

import com.bunzeeeeer.expenseiq.core.domain.model.Category;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-15-2026
 *
 */
public interface CategoryRepository {

    Completable addCategory(Category category);

    Completable updateCategory(Category category);

    Completable deleteCategory(Category category);

    Flowable<List<Category>> getAllCategories();

    Single<Category> getCategoryById(long id);
}