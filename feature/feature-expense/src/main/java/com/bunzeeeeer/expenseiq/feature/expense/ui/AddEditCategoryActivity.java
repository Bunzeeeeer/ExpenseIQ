package com.bunzeeeeer.expenseiq.feature.expense.ui;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bunzeeeeer.expenseiq.core.data.AppDatabase;
import com.bunzeeeeer.expenseiq.core.data.repository.CategoryRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.data.repository.ExpenseRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.ui.base.BaseActivity;
import com.bunzeeeeer.expenseiq.feature.expense.R;
import com.bunzeeeeer.expenseiq.feature.expense.data.ExpenseFeatureRepository;
import com.bunzeeeeer.expenseiq.feature.expense.viewmodel.AddEditCategoryViewModel;
import com.bunzeeeeer.expenseiq.feature.expense.viewmodel.AddEditCategoryViewModelFactory;
import com.google.android.material.button.MaterialButton;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-22-2026
 *
 */
@SuppressWarnings("java:S1450")
public class AddEditCategoryActivity extends BaseActivity {

    private AddEditCategoryViewModel viewModel;

    private EditText etName;
    private EditText etIcon;
    private TextView tvColorPreview;
    private EditText etColor;
    private MaterialButton btnSave;
    private MaterialButton btnDelete;

    private long categoryId = -1;
    private String categoryName = null;
    private String categoryIcon = null;
    private String categoryColor = null;

    // ─── BaseActivity contract ────────────────────────────────────────────────

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_edit_category;
    }

    @Override
    protected void initDesign() {
        initIntentData();
        initToolbar();
        initViewComponents();
        initColorPreview();
        initSaveButton();
        initDeleteButton();
    }

    @Override
    protected void initViewModel() {
        initViewModelConstructor();
    }

    @Override
    protected void initObservers() {
        initSaveSuccessObserver();
        initDeleteSuccessObserver();
        initErrorObserver();
    }

    // ─── Design ──────────────────────────────────────────────────────────────

    private void initIntentData() {
        categoryId = getIntent().getLongExtra(
                CategoryListActivity.EXTRA_CATEGORY_ID, -1);
        categoryName = getIntent().getStringExtra(
                CategoryListActivity.EXTRA_CATEGORY_NAME);
        categoryIcon = getIntent().getStringExtra(
                CategoryListActivity.EXTRA_CATEGORY_ICON);
        categoryColor = getIntent().getStringExtra(
                CategoryListActivity.EXTRA_CATEGORY_COLOR);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(categoryId == -1
                    ? getString(R.string.category_add_title)
                    : getString(R.string.category_edit_title));
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViewComponents() {
        etName = findViewById(R.id.etName);
        etIcon = findViewById(R.id.etIcon);
        tvColorPreview = findViewById(R.id.tvColorPreview);
        etColor = findViewById(R.id.etColor);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        if (categoryId != -1) {
            etName.setText(categoryName);
            etIcon.setText(categoryIcon);
            etColor.setText(categoryColor);
            btnDelete.setVisibility(View.VISIBLE);
            updateColorPreview(categoryColor);
        }
    }

    private void initColorPreview() {
        etColor.addTextChangedListener(
                new android.text.TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s,
                                                  int start, int count, int after) {
                        // Not needed — only onTextChanged is used
                    }

                    @Override
                    public void onTextChanged(CharSequence s,
                                              int start, int before, int count) {
                        updateColorPreview(s.toString());
                    }

                    @Override
                    public void afterTextChanged(
                            android.text.Editable s) {
                        // Not needed — only onTextChanged is used
                    }
                }
        );
    }

    private void updateColorPreview(String hex) {
        try {
            int color = android.graphics.Color.parseColor(
                    hex.startsWith("#") ? hex : "#" + hex);
            android.graphics.drawable.GradientDrawable bg =
                    new android.graphics.drawable.GradientDrawable();
            bg.setColor(color);
            bg.setCornerRadius(getResources().getDimension(
                    com.bunzeeeeer.expenseiq.core.ui.R.dimen.radius_md));
            tvColorPreview.setBackground(bg);
        } catch (Exception e) {
            tvColorPreview.setBackground(null);
        }
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
        ExpenseFeatureRepository repository = new ExpenseFeatureRepository(
                new ExpenseRepositoryImpl(db.expenseDao()),
                new CategoryRepositoryImpl(db.categoryDao())
        );
        viewModel = new ViewModelProvider(this,
                new AddEditCategoryViewModelFactory(repository))
                .get(AddEditCategoryViewModel.class);
    }

    // ─── Observers ───────────────────────────────────────────────────────────

    private void initSaveSuccessObserver() {
        viewModel.getSaveSuccess().observe(this, success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(this,
                        getString(R.string.category_saved_message),
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

    private void validateAndSave() {
        String name = etName.getText().toString().trim();
        String icon = etIcon.getText().toString().trim();
        String color = etColor.getText().toString().trim();

        if (name.isEmpty()) {
            etName.setError(getString(R.string.category_error_empty_name));
            return;
        }

        if (icon.isEmpty()) {
            etIcon.setError(getString(R.string.category_error_empty_icon));
            return;
        }

        if (color.isEmpty()) {
            etColor.setError(getString(R.string.category_error_empty_color));
            return;
        }

        String colorHex = color.startsWith("#") ? color : "#" + color;
        try {
            android.graphics.Color.parseColor(colorHex);
        } catch (Exception e) {
            etColor.setError(getString(R.string.category_error_invalid_color));
            return;
        }

        Category category = new Category(name, colorHex, icon);
        if (categoryId != -1) {
            category.setId(categoryId);
        }

        viewModel.saveCategory(category);
    }

    private void showDeleteConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.category_delete_confirm_title))
                .setMessage(getString(R.string.category_delete_confirm_message))
                .setPositiveButton(getString(R.string.category_delete_confirm_yes),
                        (dialog, which) -> {
                            if (categoryId != -1) {
                                Category category = new Category(
                                        categoryName, categoryColor, categoryIcon);
                                category.setId(categoryId);
                                viewModel.deleteCategory(category);
                            }
                        })
                .setNegativeButton(getString(R.string.category_delete_confirm_no),
                        null)
                .show();
    }
}