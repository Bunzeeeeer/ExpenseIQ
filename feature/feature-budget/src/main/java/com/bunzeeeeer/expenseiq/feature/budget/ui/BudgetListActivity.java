package com.bunzeeeeer.expenseiq.feature.budget.ui;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bunzeeeeer.expenseiq.core.data.AppDatabase;
import com.bunzeeeeer.expenseiq.core.data.repository.BudgetRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.data.repository.CategoryRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.domain.model.Budget;
import com.bunzeeeeer.expenseiq.core.ui.base.BaseActivity;
import com.bunzeeeeer.expenseiq.feature.budget.R;
import com.bunzeeeeer.expenseiq.feature.budget.data.BudgetFeatureRepository;
import com.bunzeeeeer.expenseiq.feature.budget.ui.adapter.BudgetListAdapter;
import com.bunzeeeeer.expenseiq.feature.budget.viewmodel.BudgetListViewModel;
import com.bunzeeeeer.expenseiq.feature.budget.viewmodel.BudgetListViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-22-2026
 *
 */
@SuppressWarnings("java:S1450")
public class BudgetListActivity extends BaseActivity {

    public static final String EXTRA_BUDGET_ID = "extra_budget_id";
    public static final String EXTRA_BUDGET_CATEGORY_ID = "extra_budget_category_id";
    public static final String EXTRA_BUDGET_MONTH = "extra_budget_month";
    public static final String EXTRA_BUDGET_YEAR = "extra_budget_year";
    public static final String EXTRA_BUDGET_AMOUNT = "extra_budget_amount";

    private BudgetListViewModel viewModel;
    private BudgetListAdapter adapter;
    private LinearLayout llEmptyState;
    private RecyclerView rvBudgets;

    // ─── BaseActivity contract ────────────────────────────────────────────────

    @Override
    protected int getLayoutId() {
        return R.layout.activity_budget_list;
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
        initBudgetsObserver();
        initCategoriesObserver();
        initErrorObserver();
    }

    // ─── Design ──────────────────────────────────────────────────────────────

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initViewComponents() {
        llEmptyState = findViewById(R.id.llEmptyState);
        rvBudgets = findViewById(R.id.rvBudgets);
        adapter = new BudgetListAdapter();
        rvBudgets.setLayoutManager(new LinearLayoutManager(this));
        rvBudgets.setAdapter(adapter);
        adapter.setOnBudgetClickListener(this::navigateToEditBudget);
    }

    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.fabAddBudget);
        fab.setOnClickListener(v -> navigateToAddBudget());
    }

    // ─── ViewModel ───────────────────────────────────────────────────────────

    private void initViewModelConstructor() {
        String uid = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null
                ? com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";
        AppDatabase db = AppDatabase.getInstance(this);
        BudgetFeatureRepository repository = new BudgetFeatureRepository(
                uid,
                new BudgetRepositoryImpl(db.budgetDao()),
                new CategoryRepositoryImpl(db.categoryDao())
        );
        viewModel = new ViewModelProvider(this,
                new BudgetListViewModelFactory(repository))
                .get(BudgetListViewModel.class);
    }

    // ─── Observers ───────────────────────────────────────────────────────────

    private void initBudgetsObserver() {
        viewModel.getBudgets().observe(this, budgets -> {
            adapter.setBudgets(budgets);
            llEmptyState.setVisibility(budgets.isEmpty() ? View.VISIBLE : View.GONE);
            rvBudgets.setVisibility(budgets.isEmpty() ? View.GONE : View.VISIBLE);
        });
    }

    private void initCategoriesObserver() {
        viewModel.getCategories().observe(this, adapter::setCategories);
    }

    private void initErrorObserver() {
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_budget_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_categories) {
            navigateToCategories();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ─── Navigation ──────────────────────────────────────────────────────────

    private void navigateToAddBudget() {
        Intent intent = new Intent(this, AddEditBudgetActivity.class);
        startActivity(intent);
    }

    private void navigateToEditBudget(Budget budget) {
        Intent intent = new Intent(this, AddEditBudgetActivity.class);
        intent.putExtra(EXTRA_BUDGET_ID, budget.getId());
        intent.putExtra(EXTRA_BUDGET_CATEGORY_ID,
                budget.getCategoryId() != null ? budget.getCategoryId() : -1L);
        intent.putExtra(EXTRA_BUDGET_MONTH, budget.getMonth());
        intent.putExtra(EXTRA_BUDGET_YEAR, budget.getYear());
        intent.putExtra(EXTRA_BUDGET_AMOUNT, budget.getLimitAmount());
        startActivity(intent);
    }

    private void navigateToCategories() {
        Intent intent = new Intent(this,
                com.bunzeeeeer.expenseiq.feature.expense.ui.CategoryListActivity.class);
        startActivity(intent);
    }
}