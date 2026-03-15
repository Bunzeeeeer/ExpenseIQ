package com.bunzeeeeer.expenseiq.core.data.repository;

import com.bunzeeeeer.expenseiq.core.data.dao.CategoryDao;
import com.bunzeeeeer.expenseiq.core.data.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.repository.CategoryRepository;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryDao categoryDao;

    public CategoryRepositoryImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public Completable addCategory(Category category) {
        return categoryDao.insert(category);
    }

    @Override
    public Completable updateCategory(Category category) {
        return categoryDao.update(category);
    }

    @Override
    public Completable deleteCategory(Category category) {
        return categoryDao.delete(category);
    }

    @Override
    public Flowable<List<Category>> getAllCategories() {
        return categoryDao.getAllCategories();
    }

    @Override
    public Single<Category> getCategoryById(long id) {
        return categoryDao.getCategoryById(id);
    }
}