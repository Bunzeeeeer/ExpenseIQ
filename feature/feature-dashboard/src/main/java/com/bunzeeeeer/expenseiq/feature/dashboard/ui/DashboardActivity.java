package com.bunzeeeeer.expenseiq.feature.dashboard.ui;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bunzeeeeer.expenseiq.core.data.AppDatabase;
import com.bunzeeeeer.expenseiq.core.data.repository.BudgetRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.data.repository.CategoryRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.data.repository.ExpenseRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.domain.model.Budget;
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;
import com.bunzeeeeer.expenseiq.core.ui.base.BaseActivity;
import com.bunzeeeeer.expenseiq.feature.dashboard.R;
import com.bunzeeeeer.expenseiq.feature.dashboard.data.DashboardRepository;
import com.bunzeeeeer.expenseiq.feature.dashboard.ui.adapter.RecentExpenseAdapter;
import com.bunzeeeeer.expenseiq.feature.dashboard.viewmodel.DashboardViewModel;
import com.bunzeeeeer.expenseiq.feature.dashboard.viewmodel.DashboardViewModelFactory;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-15-2026
 *
 */
@SuppressWarnings("java:S1450")
public class DashboardActivity extends BaseActivity {

    private DashboardViewModel viewModel;

    private TextView tvTotalAmount;
    private TextView tvExpenseCount;
    private GridLayout gridCategories;
    private LinearLayout llBreakdownContainer;
    private LinearLayout llBudgetContainer;
    private RecentExpenseAdapter adapter;

    // Cached categories for budget section
    private List<Category> currentCategories = new ArrayList<>();
    private double currentTotalBudget = 0.0;

    // Pastel palette
    private static final int[] PASTEL_BG = {
            0xFFFFF0F5, 0xFFF0FAF5, 0xFFFFF4EE,
            0xFFF3F0FF, 0xFFEEF4FF, 0xFFFFFBEE
    };
    private static final int[] PASTEL_ACCENT = {
            0xFFC4547A, 0xFF1A7F5A, 0xFFC05E30,
            0xFF6040B0, 0xFF2E5FA3, 0xFF8B6914
    };
    private static final int[] BAR_COLORS = {
            0xFFFFB3C6, 0xFFA8E6CF, 0xFFFFCBA4,
            0xFFB5D0FF, 0xFFD4BBFF, 0xFFD3D1C7
    };
    private static final String[] CATEGORY_EMOJIS = {
            "🍜", "🚌", "🛍️", "💊", "💡", "🎯"
    };

    // ─── BaseActivity contract ────────────────────────────────────────────────

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dashboard;
    }

    @Override
    protected void initDesign() {
        initViewComponents();
        initGreeting();
        initDateLabel();
    }

    @Override
    protected void initViewModel() {
        initViewModelConstructor();
    }

    @Override
    protected void initObservers() {
        initTotalObserver();
        initDashboardDataObserver();
        initBudgetsObserver();
        initErrorObserver();
    }

    // ─── Design ──────────────────────────────────────────────────────────────

    private void initViewComponents() {
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvExpenseCount = findViewById(R.id.tvExpenseCount);
        gridCategories = findViewById(R.id.gridCategories);
        llBreakdownContainer = findViewById(R.id.llBreakdownContainer);
        llBudgetContainer = findViewById(R.id.llBudgetContainer);

        RecyclerView rvRecentExpenses = findViewById(R.id.rvRecentExpenses);
        adapter = new RecentExpenseAdapter();
        rvRecentExpenses.setLayoutManager(new LinearLayoutManager(this));
        rvRecentExpenses.setAdapter(adapter);
    }

    private void initGreeting() {
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            tvGreeting.setText(getString(R.string.greeting_morning));
        } else if (hour < 18) {
            tvGreeting.setText(getString(R.string.greeting_afternoon));
        } else {
            tvGreeting.setText(getString(R.string.greeting_evening));
        }
    }

    private void initDateLabel() {
        TextView tvDate = findViewById(R.id.tvDate);
        TextView tvMonthLabel = findViewById(R.id.tvMonthLabel);
        SimpleDateFormat dateFmt = new SimpleDateFormat(
                getString(R.string.dashboard_date_format), Locale.getDefault());
        tvDate.setText(dateFmt.format(new Date()));
        SimpleDateFormat monthFmt = new SimpleDateFormat(
                getString(R.string.dashboard_month_format), Locale.getDefault());
        tvMonthLabel.setText(monthFmt.format(new Date()));
    }

    // ─── ViewModel ───────────────────────────────────────────────────────────

    private void initViewModelConstructor() {
        AppDatabase db = AppDatabase.getInstance(this);
        DashboardRepository repository = new DashboardRepository(
                new ExpenseRepositoryImpl(db.expenseDao()),
                new CategoryRepositoryImpl(db.categoryDao()),
                new BudgetRepositoryImpl(db.budgetDao())
        );
        viewModel = new ViewModelProvider(this,
                new DashboardViewModelFactory(repository))
                .get(DashboardViewModel.class);
    }

    // ─── Observers ───────────────────────────────────────────────────────────

    private void initTotalObserver() {
        viewModel.getTotalThisMonth().observe(this, total -> {
            tvTotalAmount.setText(
                    getString(R.string.dashboard_amount_format, total));
            updateBudgetLeft();
        });
    }

    private void initDashboardDataObserver() {
        viewModel.getDashboardData().observe(this, data -> {
            currentCategories = data.getCategories();
            adapter.setExpenses(data.getExpenses());
            tvExpenseCount.setText(String.valueOf(data.getExpenses().size()));
            updateCategoryGrid(data.getCategories());
            updateBreakdown(data.getExpenses(), data.getCategories());
            updateCategoryAmounts(data.getExpenses());
        });
    }

    private void initBudgetsObserver() {
        viewModel.getBudgets().observe(this, budgets -> {
            currentTotalBudget = 0.0;
            for (Budget budget : budgets) {
                currentTotalBudget += budget.getLimitAmount();
            }
            updateBudgetLeft();
            updateBudgetSection(budgets, currentCategories);
        });
    }

    private void initErrorObserver() {
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── UI Updaters ─────────────────────────────────────────────────────────

    private void updateCategoryGrid(List<Category> categories) {
        gridCategories.removeAllViews();

        int count = categories.size();
        gridCategories.setColumnCount(2);
        gridCategories.setRowCount((int) Math.ceil(count / 2.0));

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int padding = (int) getResources().getDimension(
                com.bunzeeeeer.expenseiq.core.ui.R.dimen.spacing_md) * 2;
        int gap = (int) getResources().getDimension(
                com.bunzeeeeer.expenseiq.core.ui.R.dimen.spacing_sm);
        int cardWidth = (screenWidth - padding - gap) / 2;

        for (int i = 0; i < count; i++) {
            Category cat = categories.get(i);
            int colorBg = PASTEL_BG[i % PASTEL_BG.length];
            int colorAccent = PASTEL_ACCENT[i % PASTEL_ACCENT.length];
            String emoji = CATEGORY_EMOJIS[i % CATEGORY_EMOJIS.length];

            MaterialCardView card = new MaterialCardView(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = cardWidth;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i % 2, 1f);
            params.rowSpec = GridLayout.spec(i / 2, 1f);
            params.setMargins(
                    i % 2 == 0 ? 0 : gap / 2,
                    i < 2 ? 0 : gap,
                    i % 2 == 0 ? gap / 2 : 0,
                    0
            );
            card.setLayoutParams(params);
            card.setRadius(getResources().getDimension(
                    com.bunzeeeeer.expenseiq.core.ui.R.dimen.radius_lg));
            card.setCardElevation(0);
            card.setCardBackgroundColor(colorBg);

            LinearLayout inner = new LinearLayout(this);
            inner.setOrientation(LinearLayout.VERTICAL);
            int p = (int) getResources().getDimension(
                    com.bunzeeeeer.expenseiq.core.ui.R.dimen.spacing_md);
            inner.setPadding(p, p, p, p);

            TextView tvEmoji = new TextView(this);
            tvEmoji.setText(emoji);
            tvEmoji.setTextSize(20);

            TextView tvName = new TextView(this);
            tvName.setText(cat.getName());
            tvName.setTextSize(11);
            tvName.setTextColor(0xFF6B6575);
            LinearLayout.LayoutParams tvNameParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvNameParams.topMargin = 4;
            tvName.setLayoutParams(tvNameParams);

            TextView tvAmount = new TextView(this);
            tvAmount.setText(getString(R.string.dashboard_amount_format, 0.0));
            tvAmount.setTextSize(17);
            tvAmount.setTextColor(colorAccent);
            tvAmount.setTag(getString(R.string.dashboard_category_amount_tag, cat.getId()));
            LinearLayout.LayoutParams tvAmtParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvAmtParams.topMargin = 2;
            tvAmount.setLayoutParams(tvAmtParams);

            inner.addView(tvEmoji);
            inner.addView(tvName);
            inner.addView(tvAmount);
            card.addView(inner);
            gridCategories.addView(card);
        }
    }

    private void updateCategoryAmounts(List<Expense> expenses) {
        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear = cal.get(Calendar.YEAR);

        Map<Long, Double> catTotals = new HashMap<>();
        for (Expense e : expenses) {
            Calendar expCal = Calendar.getInstance();
            expCal.setTimeInMillis(e.getDate());
            if (expCal.get(Calendar.MONTH) == currentMonth
                    && expCal.get(Calendar.YEAR) == currentYear) {
                Long catId = e.getCategoryId();
                if (catId != null) {
                    catTotals.merge(catId, e.getAmount(), Double::sum);
                }
            }
        }

        for (Map.Entry<Long, Double> entry : catTotals.entrySet()) {
            String tag = getString(R.string.dashboard_category_amount_tag, entry.getKey());
            TextView tvAmount = gridCategories.findViewWithTag(tag);
            if (tvAmount != null) {
                tvAmount.setText(getString(R.string.dashboard_amount_format, entry.getValue()));
            }
        }
    }

    private void updateBreakdown(List<Expense> expenses, List<Category> categories) {
        llBreakdownContainer.removeAllViews();

        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH);
        int currentYear = cal.get(Calendar.YEAR);

        Map<Long, Float> catTotals = new HashMap<>();
        float grandTotal = 0f;

        for (Expense e : expenses) {
            Calendar expCal = Calendar.getInstance();
            expCal.setTimeInMillis(e.getDate());
            if (expCal.get(Calendar.MONTH) == currentMonth
                    && expCal.get(Calendar.YEAR) == currentYear) {
                Long catId = e.getCategoryId();
                if (catId != null) {
                    catTotals.merge(catId, (float) e.getAmount(), Float::sum);
                    grandTotal += (float) e.getAmount();
                }
            }
        }

        if (catTotals.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText(getString(R.string.dashboard_empty_expenses));
            empty.setTextColor(0xFF9B95A3);
            empty.setTextSize(13);
            llBreakdownContainer.addView(empty);
            return;
        }

        Map<Long, String> categoryNames = new HashMap<>();
        for (Category cat : categories) {
            categoryNames.put(cat.getId(), cat.getName());
        }

        int spacingSm = (int) getResources().getDimension(
                com.bunzeeeeer.expenseiq.core.ui.R.dimen.spacing_sm);
        int spacingXxl = (int) getResources().getDimension(
                com.bunzeeeeer.expenseiq.core.ui.R.dimen.spacing_xxl);
        int spacingMd = (int) getResources().getDimension(
                com.bunzeeeeer.expenseiq.core.ui.R.dimen.spacing_md);

        int idx = 0;
        List<Map.Entry<Long, Float>> sorted = new ArrayList<>(catTotals.entrySet());
        sorted.sort((a, b) -> Float.compare(b.getValue(), a.getValue()));

        for (Map.Entry<Long, Float> entry : sorted) {
            float pct = grandTotal > 0 ? entry.getValue() / grandTotal : 0f;
            int barColor = BAR_COLORS[idx % BAR_COLORS.length];

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            rowParams.bottomMargin = spacingSm;
            row.setLayoutParams(rowParams);

            String catName = categoryNames.containsKey(entry.getKey())
                    ? categoryNames.get(entry.getKey())
                    : getString(R.string.dashboard_category_label, entry.getKey());

            TextView tvLabel = new TextView(this);
            tvLabel.setText(catName);
            tvLabel.setTextSize(12);
            tvLabel.setTextColor(0xFF6B6575);
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                    spacingXxl + 20,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tvLabel.setLayoutParams(labelParams);

            android.graphics.drawable.GradientDrawable barBgDrawable =
                    new android.graphics.drawable.GradientDrawable();
            barBgDrawable.setColor(0xFFF3EFF8);
            barBgDrawable.setCornerRadius(20f);

            int barH = spacingSm + 2;
            LinearLayout barBg = new LinearLayout(this);
            LinearLayout.LayoutParams barBgParams = new LinearLayout.LayoutParams(
                    0, barH, 1f);
            barBgParams.setMargins(spacingSm, 0, spacingSm, 0);
            barBg.setLayoutParams(barBgParams);
            barBg.setBackground(barBgDrawable);
            barBg.setClipChildren(true);

            android.graphics.drawable.GradientDrawable fillDrawable =
                    new android.graphics.drawable.GradientDrawable();
            fillDrawable.setColor(barColor);
            fillDrawable.setCornerRadius(20f);

            int fillW = (int) (pct * (getResources().getDisplayMetrics().widthPixels
                    - spacingMd * 2 - spacingXxl - 20 - 60));
            View barFill = new View(this);
            LinearLayout.LayoutParams fillParams = new LinearLayout.LayoutParams(
                    Math.max(fillW, 8), barH);
            barFill.setLayoutParams(fillParams);
            barFill.setBackground(fillDrawable);
            barBg.addView(barFill);

            TextView tvAmt = new TextView(this);
            tvAmt.setText(getString(R.string.dashboard_amount_short_format, entry.getValue()));
            tvAmt.setTextSize(12);
            tvAmt.setTextColor(0xFF1C1B1F);
            LinearLayout.LayoutParams amtParams = new LinearLayout.LayoutParams(
                    spacingXxl + 10,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            amtParams.gravity = Gravity.END;
            tvAmt.setLayoutParams(amtParams);
            tvAmt.setGravity(Gravity.END);

            row.addView(tvLabel);
            row.addView(barBg);
            row.addView(tvAmt);
            llBreakdownContainer.addView(row);
            idx++;
        }
    }

    private void updateBudgetSection(List<Budget> budgets, List<Category> categories) {
        llBudgetContainer.removeAllViews();

        if (budgets.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText(getString(R.string.dashboard_empty_budget));
            empty.setTextColor(0xFF9B95A3);
            empty.setTextSize(13);
            llBudgetContainer.addView(empty);
            return;
        }

        Map<Long, Category> categoryMap = new HashMap<>();
        for (Category cat : categories) {
            categoryMap.put(cat.getId(), cat);
        }

        int spacingMd = (int) getResources().getDimension(
                com.bunzeeeeer.expenseiq.core.ui.R.dimen.spacing_md);
        int spacingXs = (int) getResources().getDimension(
                com.bunzeeeeer.expenseiq.core.ui.R.dimen.spacing_xs);

        int idx = 0;
        for (Budget budget : budgets) {
            int barColor = BAR_COLORS[idx % BAR_COLORS.length];

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            rowParams.bottomMargin = idx < budgets.size() - 1 ? spacingMd : 0;
            row.setLayoutParams(rowParams);

            LinearLayout labelRow = buildBudgetLabelRow(
                    budget, spacingXs, idx, categoryMap);
            LinearLayout track = buildBudgetTrack(barColor);

            row.addView(labelRow);
            row.addView(track);
            llBudgetContainer.addView(row);
            idx++;
        }
    }

    private LinearLayout buildBudgetLabelRow(Budget budget, int spacingXs,
                                             int idx, Map<Long, Category> categoryMap) {
        LinearLayout labelRow = new LinearLayout(this);
        labelRow.setOrientation(LinearLayout.HORIZONTAL);
        labelRow.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lrParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lrParams.bottomMargin = spacingXs;
        labelRow.setLayoutParams(lrParams);

        Category cat = budget.getCategoryId() != null
                ? categoryMap.get(budget.getCategoryId())
                : null;
        String emoji = cat != null
                ? cat.getIcon()
                : CATEGORY_EMOJIS[idx % CATEGORY_EMOJIS.length];
        String name = cat != null
                ? cat.getName()
                : getString(R.string.dashboard_category_label, budget.getCategoryId());

        TextView tvName = new TextView(this);
        tvName.setText(getString(R.string.dashboard_budget_name_format, emoji, name));
        tvName.setTextSize(13);
        tvName.setTextColor(0xFF1C1B1F);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tvName.setLayoutParams(nameParams);

        TextView tvLimit = new TextView(this);
        tvLimit.setText(getString(R.string.dashboard_budget_limit, budget.getLimitAmount()));
        tvLimit.setTextSize(12);
        tvLimit.setTextColor(0xFF9B95A3);
        tvLimit.setGravity(Gravity.END);

        labelRow.addView(tvName);
        labelRow.addView(tvLimit);
        return labelRow;
    }

    private LinearLayout buildBudgetTrack(int barColor) {
        android.graphics.drawable.GradientDrawable trackBg =
                new android.graphics.drawable.GradientDrawable();
        trackBg.setColor(0xFFF3EFF8);
        trackBg.setCornerRadius(20f);

        LinearLayout track = new LinearLayout(this);
        LinearLayout.LayoutParams trackParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 10);
        track.setLayoutParams(trackParams);
        track.setBackground(trackBg);
        track.setClipChildren(true);
        track.addView(buildBudgetFill(barColor));

        return track;
    }

    private View buildBudgetFill(int barColor) {
        android.graphics.drawable.GradientDrawable fillBg =
                new android.graphics.drawable.GradientDrawable();
        fillBg.setColor(barColor);
        fillBg.setCornerRadius(20f);

        View fill = new View(this);
        LinearLayout.LayoutParams fillParams = new LinearLayout.LayoutParams(0, 10);
        fill.setLayoutParams(fillParams);
        fill.setBackground(fillBg);
        return fill;
    }

    private void updateBudgetLeft() {
        TextView tvBudgetLeft = findViewById(R.id.tvBudgetLeft);
        Double total = viewModel.getTotalThisMonth().getValue();
        double spent = total != null ? total : 0.0;
        double left = currentTotalBudget - spent;
        tvBudgetLeft.setText(getString(R.string.dashboard_amount_format, left));
    }
}