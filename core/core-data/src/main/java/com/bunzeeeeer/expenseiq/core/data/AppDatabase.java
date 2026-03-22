package com.bunzeeeeer.expenseiq.core.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.bunzeeeeer.expenseiq.core.data.dao.BudgetDao;
import com.bunzeeeeer.expenseiq.core.data.dao.CategoryDao;
import com.bunzeeeeer.expenseiq.core.data.dao.ExpenseDao;
import com.bunzeeeeer.expenseiq.core.domain.model.Budget;
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;

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

    // ─── Seed Data ────────────────────────────────────────────────────────────

    private static final RoomDatabase.Callback DATABASE_CALLBACK = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            db.execSQL("INSERT INTO categories (name, colorHex, icon) VALUES ('Food', '#FF6B6B', '🍜')");
            db.execSQL("INSERT INTO categories (name, colorHex, icon) VALUES ('Transport', '#4ECDC4', '🚌')");
            db.execSQL("INSERT INTO categories (name, colorHex, icon) VALUES ('Shopping', '#FFEAA7', '🛍️')");
            db.execSQL("INSERT INTO categories (name, colorHex, icon) VALUES ('Health', '#DDA0DD', '💊')");
            db.execSQL("INSERT INTO categories (name, colorHex, icon) VALUES ('Bills', '#45B7D1', '💡')");
            db.execSQL("INSERT INTO categories (name, colorHex, icon) VALUES ('Others', '#B0B0B0', '🎯')");
        }
    };

    // ─── Singleton ────────────────────────────────────────────────────────────

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
                        )
                        .addCallback(DATABASE_CALLBACK)
                        .build();
                INSTANCE.set(current);
            }
            return current;
        }
    }
}