package com.bunzeeeeer.expenseiq.feature.expense.ui;

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
import com.bunzeeeeer.expenseiq.core.data.repository.CategoryRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.data.repository.ExpenseRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;
import com.bunzeeeeer.expenseiq.core.ui.base.BaseActivity;
import com.bunzeeeeer.expenseiq.feature.expense.R;
import com.bunzeeeeer.expenseiq.feature.expense.data.ExpenseFeatureRepository;
import com.bunzeeeeer.expenseiq.feature.expense.ui.adapter.CategorySpinnerAdapter;
import com.bunzeeeeer.expenseiq.feature.expense.viewmodel.AddEditExpenseViewModel;
import com.bunzeeeeer.expenseiq.feature.expense.viewmodel.AddEditExpenseViewModelFactory;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-15-2026
 *
 */
@SuppressWarnings("java:S1450")
public class AddEditExpenseActivity extends BaseActivity {

    private AddEditExpenseViewModel viewModel;

    private EditText etTitle;
    private EditText etAmount;
    private EditText etNote;
    private TextView tvDate;
    private Spinner spinnerCategory;
    private MaterialButton btnSave;
    private MaterialButton btnDelete;

    private CategorySpinnerAdapter categoryAdapter;
    private final List<Category> categoryList = new ArrayList<>();
    private final Calendar selectedDate = Calendar.getInstance();
    private Expense expenseToEdit = null;
    private long expenseId = -1;
    private String currentUserId = "";

    // ─── BaseActivity contract ────────────────────────────────────────────────

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_edit_expense;
    }

    @Override
    protected void initDesign() {
        initIntentData();
        initToolbar();
        initViewComponents();
        initDatePicker();
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
        initExpenseToEditObserver();
        initSaveSuccessObserver();
        initDeleteSuccessObserver();
        initErrorObserver();
    }

    // ─── Design ──────────────────────────────────────────────────────────────

    private void initIntentData() {
        expenseId = getIntent().getLongExtra(
                ExpenseListActivity.EXTRA_EXPENSE_ID, -1);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(expenseId == -1
                    ? getString(R.string.expense_add_title)
                    : getString(R.string.expense_edit_title));
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViewComponents() {
        etTitle = findViewById(R.id.etTitle);
        etAmount = findViewById(R.id.etAmount);
        etNote = findViewById(R.id.etNote);
        tvDate = findViewById(R.id.tvDate);
        spinnerCategory = findViewById(R.id.spinnerCategory);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        categoryAdapter = new CategorySpinnerAdapter(this, categoryList);
        spinnerCategory.setAdapter(categoryAdapter);

        updateDateDisplay();
    }

    private void initDatePicker() {
        tvDate.setOnClickListener(v -> showDatePicker());
    }

    private void initSaveButton() {
        btnSave.setOnClickListener(v -> validateAndSave());
    }

    private void initDeleteButton() {
        btnDelete.setOnClickListener(v -> showDeleteConfirmation());
    }

    // ─── ViewModel ───────────────────────────────────────────────────────────

    private void initViewModelConstructor() {
        currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null
                ? com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser().getUid()
                : "";
        AppDatabase db = AppDatabase.getInstance(this);
        ExpenseFeatureRepository repository = new ExpenseFeatureRepository(
                currentUserId,
                new ExpenseRepositoryImpl(db.expenseDao()),
                new CategoryRepositoryImpl(db.categoryDao())
        );
        viewModel = new ViewModelProvider(this,
                new AddEditExpenseViewModelFactory(repository))
                .get(AddEditExpenseViewModel.class);

        if (expenseId != -1) {
            viewModel.loadExpenseById(expenseId);
        }
    }

    // ─── Observers ───────────────────────────────────────────────────────────

    private void initCategoriesObserver() {
        viewModel.getCategories().observe(this, categories -> {
            categoryList.clear();
            categoryList.addAll(categories);
            categoryAdapter.notifyDataSetChanged();
        });
    }

    private void initExpenseToEditObserver() {
        viewModel.getExpenseToEdit().observe(this, expense -> {
            if (expense == null) return;
            expenseToEdit = expense;
            populateFields(expense);
            btnDelete.setVisibility(View.VISIBLE);
        });
    }

    private void initSaveSuccessObserver() {
        viewModel.getSaveSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this,
                        getString(R.string.expense_saved_message),
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

    private void populateFields(Expense expense) {
        etTitle.setText(expense.getTitle());
        etAmount.setText(String.valueOf(expense.getAmount()));
        etNote.setText(expense.getNote());
        selectedDate.setTimeInMillis(expense.getDate());
        updateDateDisplay();

        long catId = expense.getCategoryId();
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getId() == catId) {
                spinnerCategory.setSelection(i);
                break;
            }
        }
    }

    private void updateDateDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat(
                getString(R.string.expense_date_format), Locale.getDefault());
        tvDate.setText(sdf.format(selectedDate.getTime()));
    }

    private void showDatePicker() {
        new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    updateDateDisplay();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void validateAndSave() {
        String title = etTitle.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        if (title.isEmpty()) {
            etTitle.setError(getString(R.string.expense_error_empty_title));
            return;
        }

        if (amountStr.isEmpty()) {
            etAmount.setError(getString(R.string.expense_error_empty_amount));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            etAmount.setError(getString(R.string.expense_error_invalid_amount));
            return;
        }

        if (categoryList.isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.expense_error_no_category),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        Long categoryId = null;
        if (selectedCategory != null) {
            categoryId = selectedCategory.getId();
        }

        viewModel.saveExpense(buildExpense(title, amount, note, categoryId));
    }

    private Expense buildExpense(String title, double amount, String note, Long categoryId) {
        Expense expense = new Expense(
                currentUserId,
                title,
                amount,
                selectedDate.getTimeInMillis(),
                note,
                categoryId
        );
        if (expenseToEdit != null) {
            expense.setId(expenseToEdit.getId());
        }
        return expense;
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.expense_delete_confirm_title))
                .setMessage(getString(R.string.expense_delete_confirm_message))
                .setPositiveButton(getString(R.string.expense_delete_confirm_yes),
                        (dialog, which) -> {
                            if (expenseToEdit != null) {
                                viewModel.deleteExpense(expenseToEdit);
                            }
                        })
                .setNegativeButton(getString(R.string.expense_delete_confirm_no), null)
                .show();
    }
}