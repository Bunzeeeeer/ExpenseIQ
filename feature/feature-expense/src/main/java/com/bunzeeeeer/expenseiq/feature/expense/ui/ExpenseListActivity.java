package com.bunzeeeeer.expenseiq.feature.expense.ui;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bunzeeeeer.expenseiq.core.data.AppDatabase;
import com.bunzeeeeer.expenseiq.core.data.repository.CategoryRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.data.repository.ExpenseRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.ui.base.BaseActivity;
import com.bunzeeeeer.expenseiq.feature.expense.R;
import com.bunzeeeeer.expenseiq.feature.expense.data.ExpenseFeatureRepository;
import com.bunzeeeeer.expenseiq.feature.expense.ui.adapter.ExpenseListAdapter;
import com.bunzeeeeer.expenseiq.feature.expense.viewmodel.ExpenseListViewModel;
import com.bunzeeeeer.expenseiq.feature.expense.viewmodel.ExpenseListViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-15-2026
 *
 */
@SuppressWarnings("java:S1450")
public class ExpenseListActivity extends BaseActivity {

    public static final String EXTRA_EXPENSE_ID = "extra_expense_id";

    private ExpenseListViewModel viewModel;
    private ExpenseListAdapter adapter;
    private LinearLayout llEmptyState;
    private RecyclerView rvExpenses;

    // ─── BaseActivity contract ────────────────────────────────────────────────

    @Override
    protected int getLayoutId() {
        return R.layout.activity_expense_list;
    }

    @Override
    protected void initDesign() {
        initToolbar();
        initViewComponents();
        initFab();
    }

    @Override
    protected void initViewModel() {
        initViewModelConstructor();
    }

    @Override
    protected void initObservers() {
        initExpensesObserver();
        initErrorObserver();
    }

    // ─── Design ──────────────────────────────────────────────────────────────

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initViewComponents() {
        llEmptyState = findViewById(R.id.llEmptyState);
        rvExpenses = findViewById(R.id.rvExpenses);
        adapter = new ExpenseListAdapter();
        rvExpenses.setLayoutManager(new LinearLayoutManager(this));
        rvExpenses.setAdapter(adapter);
        adapter.setOnExpenseClickListener(expense ->
                navigateToEditExpense(expense.getId())
        );
    }

    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.fabAddExpense);
        fab.setOnClickListener(v -> navigateToAddExpense());
    }

    // ─── ViewModel ───────────────────────────────────────────────────────────

    private void initViewModelConstructor() {
        AppDatabase db = AppDatabase.getInstance(this);
        ExpenseFeatureRepository repository = new ExpenseFeatureRepository(
                new ExpenseRepositoryImpl(db.expenseDao()),
                new CategoryRepositoryImpl(db.categoryDao())
        );
        viewModel = new ViewModelProvider(this,
                new ExpenseListViewModelFactory(repository))
                .get(ExpenseListViewModel.class);
    }

    // ─── Observers ───────────────────────────────────────────────────────────

    private void initExpensesObserver() {
        viewModel.getExpenses().observe(this, expenses -> {
            adapter.setExpenses(expenses);
            llEmptyState.setVisibility(expenses.isEmpty() ? View.VISIBLE : View.GONE);
            rvExpenses.setVisibility(expenses.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    private void initErrorObserver() {
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Navigation ──────────────────────────────────────────────────────────

    private void navigateToAddExpense() {
        Intent intent = new Intent(this, AddEditExpenseActivity.class);
        startActivity(intent);
    }

    private void navigateToEditExpense(long expenseId) {
        Intent intent = new Intent(this, AddEditExpenseActivity.class);
        intent.putExtra(EXTRA_EXPENSE_ID, expenseId);
        startActivity(intent);
    }
}