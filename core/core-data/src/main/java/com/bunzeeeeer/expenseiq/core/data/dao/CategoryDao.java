package com.bunzeeeeer.expenseiq.core.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
@Dao
public interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Category category);

    @Update
    Completable update(Category category);

    @Delete
    Completable delete(Category category);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    Flowable<List<Category>> getAllCategories();

    @Query("SELECT * FROM categories WHERE id = :id")
    Single<Category> getCategoryById(long id);
}