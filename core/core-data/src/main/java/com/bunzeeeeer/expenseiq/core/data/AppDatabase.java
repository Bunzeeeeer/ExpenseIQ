package com.bunzeeeeer.expenseiq.core.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.bunzeeeeer.expenseiq.core.data.dao.BudgetDao;
import com.bunzeeeeer.expenseiq.core.data.dao.CategoryDao;
import com.bunzeeeeer.expenseiq.core.data.dao.ExpenseDao;
import com.bunzeeeeer.expenseiq.core.data.model.Budget;
import com.bunzeeeeer.expenseiq.core.data.model.Category;
import com.bunzeeeeer.expenseiq.core.data.model.Expense;

import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-15-2026
 *
 */
@Database(
        entities = {Expense.class, Category.class, Budget.class},
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "expenseiq.db";
    private static final AtomicReference<AppDatabase> INSTANCE = new AtomicReference<>();

    public abstract ExpenseDao expenseDao();
    public abstract CategoryDao categoryDao();
    public abstract BudgetDao budgetDao();

    public static AppDatabase getInstance(Context context) {
        AppDatabase current = INSTANCE.get();
        if (current != null) {
            return current;
        }
        synchronized (AppDatabase.class) {
            current = INSTANCE.get();
            if (current == null) {
                current = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        DATABASE_NAME
                ).build();
                INSTANCE.set(current);
            }
            return current;
        }
    }
}