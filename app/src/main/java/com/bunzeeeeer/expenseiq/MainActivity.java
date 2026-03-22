package com.bunzeeeeer.expenseiq;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.bunzeeeeer.expenseiq.core.data.AppDatabase;
import com.bunzeeeeer.expenseiq.core.data.repository.BudgetRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.data.repository.CategoryRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.data.repository.ExpenseRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.domain.model.Budget;
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;
import com.bunzeeeeer.expenseiq.core.ui.base.BaseActivity;
import com.bunzeeeeer.expenseiq.feature.budget.ui.BudgetListActivity;
import com.bunzeeeeer.expenseiq.feature.charts.ui.ChartsActivity;
import com.bunzeeeeer.expenseiq.feature.dashboard.ui.DashboardActivity;
import com.bunzeeeeer.expenseiq.feature.expense.ui.ExpenseListActivity;
import com.bunzeeeeer.expenseiq.viewmodel.MainViewModel;
import com.bunzeeeeer.expenseiq.viewmodel.MainViewModelFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-22-2026
 *
 */
@SuppressWarnings("java:S1450")
public class MainActivity extends BaseActivity {

    private MainViewModel viewModel;

    private TextView tvTotalAmount;
    private TextView tvExpenseCount;
    private TextView tvBudgetLeft;
    private TextView tvMonthLabel;
    private TextView tvInsight;
    private TextView tvTopCategory;
    private TextView tvTopCategoryAmount;
    private TextView tvDaysLeft;
    private TextView tvDailyBudget;
    private LinearLayout llBudgetHealthContainer;

    private double currentTotalBudget = 0.0;
    private List<Expense> currentExpenses;
    private List<Budget> currentBudgets;
    private List<Category> currentCategories;

    private static final Random RANDOM = new Random();
    private static final String[] MONEY_TIPS = {
            "💡 Track every peso — small expenses add up fast!",
            "💡 The 50/30/20 rule: 50% needs, 30% wants, 20% savings.",
            "💡 Pay yourself first — save before you spend.",
            "💡 Avoid impulse buying — wait 24 hours before purchasing.",
            "💡 Review your subscriptions — cancel what you don't use.",
            "💡 Cook at home more — it saves more than you think!",
            "💡 Set a weekly spending limit and stick to it.",
            "💡 Every budget you set is a promise to your future self.",
            "💡 Small daily savings compound into big yearly gains.",
            "💡 Knowing where your money goes is the first step to freedom."
    };

    // ─── BaseActivity contract ────────────────────────────────────────────────

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initDesign() {
        initViewComponents();
        initGreeting();
        initDateLabel();
        initMoneyTip();
        initBottomNav();
    }

    @Override
    protected void initViewModel() {
        initViewModelConstructor();
    }

    @Override
    protected void initObservers() {
        initTotalObserver();
        initLastMonthObserver();
        initExpensesObserver();
        initBudgetsObserver();
        initCategoriesObserver();
        initErrorObserver();
    }

    // ─── Design ──────────────────────────────────────────────────────────────

    private void initViewComponents() {
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvExpenseCount = findViewById(R.id.tvExpenseCount);
        tvBudgetLeft = findViewById(R.id.tvBudgetLeft);
        tvMonthLabel = findViewById(R.id.tvMonthLabel);
        tvInsight = findViewById(R.id.tvInsight);
        tvTopCategory = findViewById(R.id.tvTopCategory);
        tvTopCategoryAmount = findViewById(R.id.tvTopCategoryAmount);
        tvDaysLeft = findViewById(R.id.tvDaysLeft);
        tvDailyBudget = findViewById(R.id.tvDailyBudget);
        llBudgetHealthContainer = findViewById(R.id.llBudgetHealthContainer);
    }

    private void initGreeting() {
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) {
            tvGreeting.setText(getString(R.string.home_greeting_morning));
        } else if (hour < 18) {
            tvGreeting.setText(getString(R.string.home_greeting_afternoon));
        } else {
            tvGreeting.setText(getString(R.string.home_greeting_evening));
        }
    }

    private void initDateLabel() {
        TextView tvDate = findViewById(R.id.tvDate);
        SimpleDateFormat dateFmt = new SimpleDateFormat(
                getString(R.string.home_date_format), Locale.getDefault());
        tvDate.setText(dateFmt.format(new Date()));
        SimpleDateFormat monthFmt = new SimpleDateFormat(
                getString(R.string.home_month_format), Locale.getDefault());
        tvMonthLabel.setText(monthFmt.format(new Date()));
    }

    private void initMoneyTip() {
        TextView tvMoneyTip = findViewById(R.id.tvMoneyTip);
        tvMoneyTip.setText(MONEY_TIPS[RANDOM.nextInt(MONEY_TIPS.length)]);
    }

    private void initBottomNav() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setItemActiveIndicatorEnabled(false);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                navigateToDashboard();
                return true;
            } else if (id == R.id.nav_expenses) {
                navigateToExpenses();
                return true;
            } else if (id == R.id.nav_charts) {
                navigateToCharts();
                return true;
            } else if (id == R.id.nav_budget) {
                navigateToBudget();
                return true;
            }
            return false;
        });
    }

    // ─── ViewModel ───────────────────────────────────────────────────────────

    private void initViewModelConstructor() {
        AppDatabase db = AppDatabase.getInstance(this);
        viewModel = new ViewModelProvider(this,
                new MainViewModelFactory(
                        new ExpenseRepositoryImpl(db.expenseDao()),
                        new BudgetRepositoryImpl(db.budgetDao()),
                        new CategoryRepositoryImpl(db.categoryDao())
                )).get(MainViewModel.class);
    }

    // ─── Observers ───────────────────────────────────────────────────────────

    private void initTotalObserver() {
        viewModel.getTotalThisMonth().observe(this, total -> {
            tvTotalAmount.setText(
                    getString(R.string.home_amount_format, total));
            updateBudgetLeft();
            updateUpcoming();
        });
    }

    private void initLastMonthObserver() {
        viewModel.getTotalLastMonth().observe(this,
                total -> updateSpendingInsight());
    }

    private void initExpensesObserver() {
        viewModel.getExpensesThisMonth().observe(this, expenses -> {
            currentExpenses = expenses;
            tvExpenseCount.setText(String.valueOf(expenses.size()));
            updateTopCategory();
            updateBudgetHealth();
        });
    }

    private void initBudgetsObserver() {
        viewModel.getBudgets().observe(this, budgets -> {
            currentBudgets = budgets;
            currentTotalBudget = 0.0;
            for (Budget b : budgets) {
                currentTotalBudget += b.getLimitAmount();
            }
            updateBudgetLeft();
            updateUpcoming();
            updateBudgetHealth();
        });
    }

    private void initCategoriesObserver() {
        viewModel.getCategories().observe(this, categories -> {
            currentCategories = categories;
            updateTopCategory();
            updateBudgetHealth();
        });
    }

    private void initErrorObserver() {
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── UI Updaters ──────────────────────────────────────────────────────────

    private void updateBudgetLeft() {
        Double total = viewModel.getTotalThisMonth().getValue();
        double spent = total != null ? total : 0.0;
        double left = currentTotalBudget - spent;
        tvBudgetLeft.setText(getString(R.string.home_amount_format, left));
    }

    private void updateSpendingInsight() {
        Double thisMonth = viewModel.getTotalThisMonth().getValue();
        Double lastMonth = viewModel.getTotalLastMonth().getValue();
        double current = thisMonth != null ? thisMonth : 0.0;
        double previous = lastMonth != null ? lastMonth : 0.0;

        if (previous == 0.0 && current == 0.0) {
            tvInsight.setText(getString(R.string.home_insight_no_data));
            return;
        }

        if (previous == 0.0) {
            tvInsight.setText(getString(R.string.home_insight_first_month));
            return;
        }

        double pct = ((current - previous) / previous) * 100;
        if (pct > 0) {
            tvInsight.setText(getString(
                    R.string.home_insight_more, Math.abs(pct)));
            tvInsight.setTextColor(0xFFB3261E);
        } else if (pct < 0) {
            tvInsight.setText(getString(
                    R.string.home_insight_less, Math.abs(pct)));
            tvInsight.setTextColor(0xFF386A20);
        } else {
            tvInsight.setText(getString(R.string.home_insight_same));
            tvInsight.setTextColor(0xFF6B6575);
        }
    }

    private void updateTopCategory() {
        if (currentExpenses == null || currentExpenses.isEmpty()) {
            showNoTopCategory();
            return;
        }

        Map<Long, Double> catTotals = buildCatTotalsMap();

        if (catTotals.isEmpty()) {
            showNoTopCategory();
            return;
        }

        Long topCatId = findTopCategoryId(catTotals);
        Double topAmountValue = topCatId != null ? catTotals.get(topCatId) : null;
        double topAmount = topAmountValue != null ? topAmountValue : 0.0;

        String catName = getString(R.string.home_top_category_unknown);
        String catIcon = "🎯";
        if (currentCategories != null && topCatId != null) {
            for (Category cat : currentCategories) {
                if (cat.getId() == topCatId) {
                    catName = cat.getName();
                    catIcon = cat.getIcon();
                    break;
                }
            }
        }

        tvTopCategory.setText(getString(
                R.string.home_top_category_format, catIcon, catName));
        tvTopCategoryAmount.setText(getString(
                R.string.home_amount_format, topAmount));
        tvTopCategoryAmount.setVisibility(View.VISIBLE);
    }

    private void showNoTopCategory() {
        tvTopCategory.setText(getString(R.string.home_top_category_none));
        tvTopCategoryAmount.setVisibility(View.GONE);
    }

    private Map<Long, Double> buildCatTotalsMap() {
        Map<Long, Double> catTotals = new HashMap<>();
        for (Expense e : currentExpenses) {
            if (e.getCategoryId() != null) {
                catTotals.merge(e.getCategoryId(), e.getAmount(), Double::sum);
            }
        }
        return catTotals;
    }

    private Long findTopCategoryId(Map<Long, Double> catTotals) {
        Long topCatId = null;
        double topAmount = 0;
        for (Map.Entry<Long, Double> entry : catTotals.entrySet()) {
            if (entry.getValue() > topAmount) {
                topAmount = entry.getValue();
                topCatId = entry.getKey();
            }
        }
        return topCatId;
    }

    private void updateUpcoming() {
        Calendar cal = Calendar.getInstance();
        int daysLeft = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
                - cal.get(Calendar.DAY_OF_MONTH) + 1;
        tvDaysLeft.setText(getString(R.string.home_days_left_format, daysLeft));

        Double total = viewModel.getTotalThisMonth().getValue();
        double spent = total != null ? total : 0.0;
        double left = currentTotalBudget - spent;

        if (currentTotalBudget > 0 && daysLeft > 0) {
            double dailySuggestion = left / daysLeft;
            tvDailyBudget.setText(getString(
                    R.string.home_daily_budget_format, dailySuggestion));
        } else {
            tvDailyBudget.setText(
                    getString(R.string.home_daily_budget_no_budget));
        }
    }

    private void updateBudgetHealth() {
        llBudgetHealthContainer.removeAllViews();

        if (currentBudgets == null || currentBudgets.isEmpty()) {
            addBudgetHealthMessage(
                    getString(R.string.home_budget_health_empty), 0xFF9B95A3);
            return;
        }

        Map<Long, Double> catSpending = buildCatSpendingMap();
        Map<Long, Category> categoryMap = buildCategoryMap();

        int spacingSm = (int) getResources().getDimension(
                com.bunzeeeeer.expenseiq.core.ui.R.dimen.spacing_sm);
        int spacingXs = (int) getResources().getDimension(
                com.bunzeeeeer.expenseiq.core.ui.R.dimen.spacing_xs);

        boolean anyAdded = false;
        for (Budget budget : currentBudgets) {
            if (budget.getCategoryId() == null) continue;
            boolean added = addBudgetHealthRow(
                    budget, catSpending, categoryMap, spacingSm, spacingXs);
            if (added) anyAdded = true;
        }

        if (!anyAdded) {
            addBudgetHealthMessage(
                    getString(R.string.home_budget_health_good), 0xFF386A20);
        }
    }

    private Map<Long, Double> buildCatSpendingMap() {
        Map<Long, Double> catSpending = new HashMap<>();
        if (currentExpenses != null) {
            for (Expense e : currentExpenses) {
                if (e.getCategoryId() != null) {
                    catSpending.merge(e.getCategoryId(),
                            e.getAmount(), Double::sum);
                }
            }
        }
        return catSpending;
    }

    private Map<Long, Category> buildCategoryMap() {
        Map<Long, Category> categoryMap = new HashMap<>();
        if (currentCategories != null) {
            for (Category cat : currentCategories) {
                categoryMap.put(cat.getId(), cat);
            }
        }
        return categoryMap;
    }

    private boolean addBudgetHealthRow(Budget budget,
                                       Map<Long, Double> catSpending,
                                       Map<Long, Category> categoryMap,
                                       int spacingSm, int spacingXs) {

        Double spentValue = catSpending.get(budget.getCategoryId());
        double spent = spentValue != null ? spentValue : 0.0;
        double limit = budget.getLimitAmount();
        int progress = limit > 0
                ? (int) Math.min((spent / limit) * 100, 100) : 0;

        if (progress < 50) return false;

        Category cat = categoryMap.get(budget.getCategoryId());
        String name = cat != null ? cat.getName()
                : getString(R.string.home_top_category_unknown);
        String icon = cat != null ? cat.getIcon() : "🎯";
        int indicatorColor = resolveIndicatorColor(progress);

        LinearLayout row = buildHealthRow(spacingSm);
        LinearLayout labelRow = buildHealthLabelRow(
                icon, name, progress, indicatorColor, spacingXs);
        LinearProgressIndicator progressBar =
                buildProgressBar(progress, indicatorColor);

        row.addView(labelRow);
        row.addView(progressBar);
        llBudgetHealthContainer.addView(row);
        return true;
    }

    private int resolveIndicatorColor(int progress) {
        if (progress >= 90) return Color.parseColor("#B3261E");
        if (progress >= 75) return Color.parseColor("#7D5700");
        return Color.parseColor("#386A20");
    }

    private LinearLayout buildHealthRow(int spacingSm) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = spacingSm;
        row.setLayoutParams(params);
        return row;
    }

    private LinearLayout buildHealthLabelRow(String icon, String name,
                                             int progress, int indicatorColor, int spacingXs) {
        LinearLayout labelRow = new LinearLayout(this);
        labelRow.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams lrParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lrParams.bottomMargin = spacingXs;
        labelRow.setLayoutParams(lrParams);

        TextView tvName = new TextView(this);
        tvName.setText(getString(
                R.string.home_top_category_format, icon, name));
        tvName.setTextSize(13);
        tvName.setTextColor(0xFF1C1B1F);
        LinearLayout.LayoutParams nameParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        tvName.setLayoutParams(nameParams);

        TextView tvPct = new TextView(this);
        tvPct.setText(getString(R.string.home_budget_pct_format, progress));
        tvPct.setTextSize(12);
        tvPct.setTextColor(indicatorColor);

        labelRow.addView(tvName);
        labelRow.addView(tvPct);
        return labelRow;
    }

    private LinearProgressIndicator buildProgressBar(
            int progress, int indicatorColor) {
        LinearProgressIndicator progressBar =
                new LinearProgressIndicator(this);
        LinearLayout.LayoutParams pbParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(pbParams);
        progressBar.setProgressCompat(progress, false);
        progressBar.setIndicatorColor(indicatorColor);
        progressBar.setTrackColor(0xFFF0EBF8);
        progressBar.setTrackCornerRadius(20);
        return progressBar;
    }

    private void addBudgetHealthMessage(String message, int color) {
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextColor(color);
        tv.setTextSize(13);
        llBudgetHealthContainer.addView(tv);
    }

    // ─── Navigation ──────────────────────────────────────────────────────────

    private void navigateToDashboard() {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void navigateToExpenses() {
        Intent intent = new Intent(this, ExpenseListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void navigateToCharts() {
        Intent intent = new Intent(this, ChartsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void navigateToBudget() {
        Intent intent = new Intent(this, BudgetListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}