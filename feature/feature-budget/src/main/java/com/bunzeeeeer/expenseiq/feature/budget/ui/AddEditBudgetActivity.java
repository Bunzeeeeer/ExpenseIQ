package com.bunzeeeeer.expenseiq.feature.budget.ui;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bunzeeeeer.expenseiq.core.data.AppDatabase;
import com.bunzeeeeer.expenseiq.core.data.repository.BudgetRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.data.repository.CategoryRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.domain.model.Budget;
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.ui.base.BaseActivity;
import com.bunzeeeeer.expenseiq.feature.budget.R;
import com.bunzeeeeer.expenseiq.feature.budget.data.BudgetFeatureRepository;
import com.bunzeeeeer.expenseiq.feature.budget.ui.adapter.CategorySpinnerAdapter;
import com.bunzeeeeer.expenseiq.feature.budget.viewmodel.AddEditBudgetViewModel;
import com.bunzeeeeer.expenseiq.feature.budget.viewmodel.AddEditBudgetViewModelFactory;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-22-2026
 *
 */
@SuppressWarnings("java:S1450")
public class AddEditBudgetActivity extends BaseActivity {

    private AddEditBudgetViewModel viewModel;

    private EditText etAmount;
    private Spinner spinnerCategory;
    private TextView tvMonthYear;
    private MaterialButton btnSave;
    private MaterialButton btnDelete;

    private CategorySpinnerAdapter categoryAdapter;
    private final List<Category> categoryList = new ArrayList<>();
    private final Calendar selectedMonth = Calendar.getInstance();
    private long budgetId = -1;
    private long budgetCategoryId = -1;
    private int budgetMonth = -1;
    private int budgetYear = -1;
    private double budgetAmount = 0;

    // ─── BaseActivity contract ────────────────────────────────────────────────

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_edit_budget;
    }

    @Override
    protected void initDesign() {
        initIntentData();
        initToolbar();
        initViewComponents();
        initMonthPicker();
        initSaveButton();
        initDeleteButton();
    }

    @Override
    protected void initViewModel() {
        initViewModelConstructor();
    }

    @Override
    protected void initObservers() {
        initCategoriesObserver();
        initSaveSuccessObserver();
        initDeleteSuccessObserver();
        initErrorObserver();
    }

    // ─── Design ──────────────────────────────────────────────────────────────

    private void initIntentData() {
        budgetId = getIntent().getLongExtra(BudgetListActivity.EXTRA_BUDGET_ID, -1);
        budgetCategoryId = getIntent().getLongExtra(
                BudgetListActivity.EXTRA_BUDGET_CATEGORY_ID, -1);
        budgetMonth = getIntent().getIntExtra(BudgetListActivity.EXTRA_BUDGET_MONTH, -1);
        budgetYear = getIntent().getIntExtra(BudgetListActivity.EXTRA_BUDGET_YEAR, -1);
        budgetAmount = getIntent().getDoubleExtra(BudgetListActivity.EXTRA_BUDGET_AMOUNT, 0);

        if (budgetMonth != -1 && budgetYear != -1) {
            selectedMonth.set(Calendar.MONTH, budgetMonth - 1);
            selectedMonth.set(Calendar.YEAR, budgetYear);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(budgetId == -1
                    ? getString(R.string.budget_add_title)
                    : getString(R.string.budget_edit_title));
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViewComponents() {
        etAmount = findViewById(R.id.etAmount);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        categoryAdapter = new CategorySpinnerAdapter(this, categoryList);
        spinnerCategory.setAdapter(categoryAdapter);

        updateMonthYearDisplay();

        if (budgetId != -1) {
            btnDelete.setVisibility(View.VISIBLE);
            if (budgetAmount > 0) {
                etAmount.setText(String.valueOf(budgetAmount));
            }
        }
    }

    private void initMonthPicker() {
        tvMonthYear.setOnClickListener(v -> showMonthYearPicker());
    }

    private void initSaveButton() {
        btnSave.setOnClickListener(v -> validateAndSave());
    }

    private void initDeleteButton() {
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    // ─── ViewModel ───────────────────────────────────────────────────────────

    private void initViewModelConstructor() {
        AppDatabase db = AppDatabase.getInstance(this);
        BudgetFeatureRepository repository = new BudgetFeatureRepository(
                new BudgetRepositoryImpl(db.budgetDao()),
                new CategoryRepositoryImpl(db.categoryDao())
        );
        viewModel = new ViewModelProvider(this,
                new AddEditBudgetViewModelFactory(repository))
                .get(AddEditBudgetViewModel.class);
    }

    // ─── Observers ───────────────────────────────────────────────────────────

    private void initCategoriesObserver() {
        viewModel.getCategories().observe(this, categories -> {
            categoryList.clear();
            categoryList.addAll(categories);
            categoryAdapter.notifyDataSetChanged();

            if (budgetCategoryId != -1) {
                for (int i = 0; i < categoryList.size(); i++) {
                    if (categoryList.get(i).getId() == budgetCategoryId) {
                        spinnerCategory.setSelection(i);
                        break;
                    }
                }
            }
        });
    }

    private void initSaveSuccessObserver() {
        viewModel.getSaveSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this,
                        getString(R.string.budget_saved_message),
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void initDeleteSuccessObserver() {
        viewModel.getDeleteSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                finish();
            }
        });
    }

    private void initErrorObserver() {
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void updateMonthYearDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat(
                getString(R.string.budget_month_year_picker_format), Locale.getDefault());
        tvMonthYear.setText(sdf.format(selectedMonth.getTime()));
    }

    private void showMonthYearPicker() {
        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedMonth.set(Calendar.YEAR, year);
                    selectedMonth.set(Calendar.MONTH, month);
                    updateMonthYearDisplay();
                },
                selectedMonth.get(Calendar.YEAR),
                selectedMonth.get(Calendar.MONTH),
                1
        ).show();
    }

    private void validateAndSave() {
        String amountStr = etAmount.getText().toString().trim();

        if (amountStr.isEmpty()) {
            etAmount.setError(getString(R.string.budget_error_empty_amount));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etAmount.setError(getString(R.string.budget_error_invalid_amount));
            return;
        }

        if (amount <= 0) {
            etAmount.setError(getString(R.string.budget_error_zero_amount));
            return;
        }

        if (categoryList.isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.budget_error_no_category),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        if (selectedCategory == null) {
            Toast.makeText(this,
                    getString(R.string.budget_error_no_category),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int month = selectedMonth.get(Calendar.MONTH) + 1;
        int year = selectedMonth.get(Calendar.YEAR);

        Budget budget = new Budget(selectedCategory.getId(), amount, month, year);

        if (budgetId != -1) {
            budget.setId(budgetId);
        }

        viewModel.saveBudget(budget);
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.budget_delete_confirm_title))
                .setMessage(getString(R.string.budget_delete_confirm_message))
                .setPositiveButton(getString(R.string.budget_delete_confirm_yes),
                        (dialog, which) -> {
                            if (budgetId != -1) {
                                Budget budget = new Budget(
                                        budgetCategoryId != -1 ? budgetCategoryId : null,
                                        0,
                                        budgetMonth,
                                        budgetYear
                                );
                                budget.setId(budgetId);
                                viewModel.deleteBudget(budget);
                            }
                        })
                .setNegativeButton(getString(R.string.budget_delete_confirm_no), null)
                .show();
    }
}