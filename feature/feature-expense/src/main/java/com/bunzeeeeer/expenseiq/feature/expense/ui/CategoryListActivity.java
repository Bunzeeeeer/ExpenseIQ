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
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.ui.base.BaseActivity;
import com.bunzeeeeer.expenseiq.feature.expense.R;
import com.bunzeeeeer.expenseiq.feature.expense.data.ExpenseFeatureRepository;
import com.bunzeeeeer.expenseiq.feature.expense.ui.adapter.CategoryListAdapter;
import com.bunzeeeeer.expenseiq.feature.expense.viewmodel.CategoryListViewModel;
import com.bunzeeeeer.expenseiq.feature.expense.viewmodel.CategoryListViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-22-2026
 *
 */
@SuppressWarnings("java:S1450")
public class CategoryListActivity extends BaseActivity {

    public static final String EXTRA_CATEGORY_ID = "extra_category_id";
    public static final String EXTRA_CATEGORY_NAME = "extra_category_name";
    public static final String EXTRA_CATEGORY_ICON = "extra_category_icon";
    public static final String EXTRA_CATEGORY_COLOR = "extra_category_color";

    private CategoryListViewModel viewModel;
    private CategoryListAdapter adapter;
    private LinearLayout llEmptyState;
    private RecyclerView rvCategories;

    // ─── BaseActivity contract ────────────────────────────────────────────────

    @Override
    protected int getLayoutId() {
        return R.layout.activity_category_list;
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
        rvCategories = findViewById(R.id.rvCategories);
        adapter = new CategoryListAdapter();
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        rvCategories.setAdapter(adapter);
        adapter.setOnCategoryClickListener(this::navigateToEditCategory);
    }

    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.fabAddCategory);
        fab.setOnClickListener(v -> navigateToAddCategory());
    }

    // ─── ViewModel ───────────────────────────────────────────────────────────

    private void initViewModelConstructor() {
        AppDatabase db = AppDatabase.getInstance(this);
        ExpenseFeatureRepository repository = new ExpenseFeatureRepository(
                new ExpenseRepositoryImpl(db.expenseDao()),
                new CategoryRepositoryImpl(db.categoryDao())
        );
        viewModel = new ViewModelProvider(this,
                new CategoryListViewModelFactory(repository))
                .get(CategoryListViewModel.class);
    }

    // ─── Observers ───────────────────────────────────────────────────────────

    private void initCategoriesObserver() {
        viewModel.getCategories().observe(this, categories -> {
            adapter.setCategories(categories);
            llEmptyState.setVisibility(
                    categories.isEmpty() ? View.VISIBLE : View.GONE);
            rvCategories.setVisibility(
                    categories.isEmpty() ? View.GONE : View.VISIBLE);
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

    private void navigateToAddCategory() {
        Intent intent = new Intent(this, AddEditCategoryActivity.class);
        startActivity(intent);
    }

    private void navigateToEditCategory(Category category) {
        Intent intent = new Intent(this, AddEditCategoryActivity.class);
        intent.putExtra(EXTRA_CATEGORY_ID, category.getId());
        intent.putExtra(EXTRA_CATEGORY_NAME, category.getName());
        intent.putExtra(EXTRA_CATEGORY_ICON, category.getIcon());
        intent.putExtra(EXTRA_CATEGORY_COLOR, category.getColorHex());
        startActivity(intent);
    }
}