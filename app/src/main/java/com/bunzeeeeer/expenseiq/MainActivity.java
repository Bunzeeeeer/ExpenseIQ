package com.bunzeeeeer.expenseiq;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.security.SecureRandom;
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
 * @Date: 03-22-2026
 *
 */
@SuppressWarnings("java:S1450")
public class MainActivity extends BaseActivity {

    private MainViewModel viewModel;

    private TextView tvGreeting;
    private TextView tvDate;
    private TextView tvHealthScore;
    private TextView tvHealthLabel;
    private View healthScoreRing;
    private TextView tvStreakCount;
    private TextView tvStreakLabel;
    private LinearLayout llAlertsContainer;
    private BarChart barChartWeekly;
    private TextView tvMoneyTip;

    private List<Expense> currentExpenses;
    private List<Expense> currentWeeklyExpenses;
    private List<Budget> currentBudgets;
    private List<Category> currentCategories;

    private static final SecureRandom RANDOM = new SecureRandom();
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
        checkAuth();
        initViewComponents();
        initGreeting();
        initDateLabel();
        initMoneyTip();
        initBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (viewModel != null) {
            viewModel.refresh();
        }
    }

    @Override
    protected void initViewModel() {
        initViewModelConstructor();
    }

    @Override
    protected void initObservers() {
        initExpensesObserver();
        initWeeklyExpensesObserver();
        initBudgetsObserver();
        initCategoriesObserver();
        initErrorObserver();
    }

    // ─── Auth ─────────────────────────────────────────────────────────────────

    private void checkAuth() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, getLoginActivityClass());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, getLoginActivityClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private Class<?> getLoginActivityClass() {
        try {
            return Class.forName("com.bunzeeeeer.expenseiq.auth.ui.LoginActivity");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("LoginActivity not found", e);
        }
    }

    // ─── Design ──────────────────────────────────────────────────────────────

    private void initViewComponents() {
        tvGreeting = findViewById(R.id.tvGreeting);
        tvDate = findViewById(R.id.tvDate);
        tvHealthScore = findViewById(R.id.tvHealthScore);
        tvHealthLabel = findViewById(R.id.tvHealthLabel);
        healthScoreRing = findViewById(R.id.healthScoreRing);
        tvStreakCount = findViewById(R.id.tvStreakCount);
        tvStreakLabel = findViewById(R.id.tvStreakLabel);
        llAlertsContainer = findViewById(R.id.llAlertsContainer);
        barChartWeekly = findViewById(R.id.barChartWeekly);
        tvMoneyTip = findViewById(R.id.tvMoneyTip);
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());
    }

    private void initGreeting() {
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
        SimpleDateFormat dateFmt = new SimpleDateFormat(
                getString(R.string.home_date_format), Locale.getDefault());
        tvDate.setText(dateFmt.format(new Date()));
    }

    private void initMoneyTip() {
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
        com.google.firebase.auth.FirebaseUser currentUser =
                FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            logout();
            return;
        }
        String uid = currentUser.getUid();
        AppDatabase db = AppDatabase.getInstance(this);
        viewModel = new ViewModelProvider(this,
                new MainViewModelFactory(
                        uid,
                        new ExpenseRepositoryImpl(db.expenseDao()),
                        new BudgetRepositoryImpl(db.budgetDao()),
                        new CategoryRepositoryImpl(db.categoryDao())
                )).get(MainViewModel.class);
    }

    // ─── Observers ───────────────────────────────────────────────────────────

    private void initExpensesObserver() {
        viewModel.getExpensesThisMonth().observe(this, expenses -> {
            currentExpenses = expenses;
            updateHealthScore();
            updateStreak();
            updateAlerts();
        });
    }

    private void initWeeklyExpensesObserver() {
        viewModel.getExpensesLastSevenDays().observe(this, expenses -> {
            currentWeeklyExpenses = expenses;
            updateWeeklyChart();
        });
    }

    private void initBudgetsObserver() {
        viewModel.getBudgets().observe(this, budgets -> {
            currentBudgets = budgets;
            updateHealthScore();
            updateAlerts();
        });
    }

    private void initCategoriesObserver() {
        viewModel.getCategories().observe(this, categories -> {
            currentCategories = categories;
            updateAlerts();
        });
    }

    private void initErrorObserver() {
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Health Score ─────────────────────────────────────────────────────────

    private void updateHealthScore() {
        if (currentBudgets == null || currentBudgets.isEmpty()) {
            tvHealthScore.setText(getString(R.string.home_health_score_na));
            tvHealthLabel.setText(getString(R.string.home_health_no_budget));
            healthScoreRing.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFF9B95A3));
            return;
        }

        if (currentExpenses == null) return;

        Map<Long, Double> catSpending = buildCatSpendingMap();
        int score = calculateHealthScore(catSpending);
        tvHealthScore.setText(String.valueOf(score));
        applyHealthScoreStyle(score);
    }

    private int calculateHealthScore(Map<Long, Double> catSpending) {
        int score = 100;
        for (Budget budget : currentBudgets) {
            score -= resolveBudgetDeduction(budget, catSpending);
        }
        return Math.max(0, score);
    }

    private int resolveBudgetDeduction(Budget budget, Map<Long, Double> catSpending) {
        if (budget.getCategoryId() == null) return 0;
        Double spentValue = catSpending.get(budget.getCategoryId());
        double spent = spentValue != null ? spentValue : 0.0;
        double limit = budget.getLimitAmount();
        if (limit <= 0) return 0;
        double pct = (spent / limit) * 100;
        return resolveScoreDeduction(pct);
    }

    private int resolveScoreDeduction(double pct) {
        if (pct >= 90) return 20;
        if (pct >= 75) return 10;
        if (pct >= 50) return 5;
        return 0;
    }

    private void applyHealthScoreStyle(int score) {
        if (score >= 80) {
            tvHealthLabel.setText(getString(R.string.home_health_great));
            healthScoreRing.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFF386A20));
        } else if (score >= 50) {
            tvHealthLabel.setText(getString(R.string.home_health_fair));
            healthScoreRing.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFF7D5700));
        } else {
            tvHealthLabel.setText(getString(R.string.home_health_poor));
            healthScoreRing.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(0xFFB3261E));
        }
    }

    // ─── Streak ──────────────────────────────────────────────────────────────

    private void updateStreak() {
        if (currentExpenses == null || currentExpenses.isEmpty()) {
            tvStreakCount.setText("0");
            tvStreakLabel.setText(getString(R.string.home_streak_none));
            return;
        }

        int streak = calculateStreak();
        tvStreakCount.setText(String.valueOf(streak));
        if (streak >= 7) {
            tvStreakLabel.setText(getString(R.string.home_streak_fire));
        } else if (streak >= 3) {
            tvStreakLabel.setText(getString(R.string.home_streak_good));
        } else if (streak == 1) {
            tvStreakLabel.setText(getString(R.string.home_streak_start));
        } else {
            tvStreakLabel.setText(getString(R.string.home_streak_none));
        }
    }

    private int calculateStreak() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        int streak = 0;
        for (int i = 0; i < 30; i++) {
            long dayStart = cal.getTimeInMillis();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            long dayEnd = cal.getTimeInMillis();
            cal.add(Calendar.DAY_OF_YEAR, -1);

            boolean hasExpense = false;
            for (Expense e : currentExpenses) {
                if (e.getDate() >= dayStart && e.getDate() < dayEnd) {
                    hasExpense = true;
                    break;
                }
            }
            if (hasExpense) {
                streak++;
            } else {
                break;
            }
            cal.add(Calendar.DAY_OF_YEAR, -1);
        }
        return streak;
    }

    // ─── Alerts ──────────────────────────────────────────────────────────────

    private void updateAlerts() {
        llAlertsContainer.removeAllViews();

        if (currentBudgets == null || currentBudgets.isEmpty()) {
            addAlert(getString(R.string.home_alert_no_budget), 0xFF7B5EA7);
            return;
        }

        if (currentExpenses == null || currentCategories == null) return;

        Map<Long, Double> catSpending = buildCatSpendingMap();
        Map<Long, Category> categoryMap = buildCategoryMap();
        boolean anyAlert = false;

        for (Budget budget : currentBudgets) {
            boolean added = processAlertForBudget(budget, catSpending, categoryMap);
            if (added) anyAlert = true;
        }

        if (!anyAlert) {
            addAlert(getString(R.string.home_alert_all_good), 0xFF386A20);
        }
    }

    private boolean processAlertForBudget(Budget budget,
                                          Map<Long, Double> catSpending,
                                          Map<Long, Category> categoryMap) {
        if (budget.getCategoryId() == null) return false;
        Double spentValue = catSpending.get(budget.getCategoryId());
        double spent = spentValue != null ? spentValue : 0.0;
        double limit = budget.getLimitAmount();
        if (limit <= 0) return false;
        double pct = (spent / limit) * 100;

        Category cat = categoryMap.get(budget.getCategoryId());
        String name = cat != null ? cat.getName() : getString(R.string.home_top_category_unknown);
        String icon = cat != null ? cat.getIcon() : "🎯";

        if (pct >= 100) {
            addAlert(getString(R.string.home_alert_over_budget, icon, name), 0xFFB3261E);
            return true;
        }
        if (pct >= 80) {
            addAlert(getString(R.string.home_alert_near_budget, icon, name, (int) pct), 0xFF7D5700);
            return true;
        }
        return false;
    }

    private void addAlert(String message, int color) {
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setTextColor(color);
        tv.setTextSize(13);
        tv.setPadding(0, 4, 0, 4);
        llAlertsContainer.addView(tv);
    }

    // ─── Weekly Chart ─────────────────────────────────────────────────────────

    private void updateWeeklyChart() {
        if (currentWeeklyExpenses == null) return;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_YEAR, -6);

        String[] labels = new String[7];
        float[] totals = new float[7];
        SimpleDateFormat dayFmt = new SimpleDateFormat("EEE", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            labels[i] = dayFmt.format(cal.getTime());
            long dayStart = cal.getTimeInMillis();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            long dayEnd = cal.getTimeInMillis();

            for (Expense e : currentWeeklyExpenses) {
                if (e.getDate() >= dayStart && e.getDate() < dayEnd) {
                    totals[i] += (float) e.getAmount();
                }
            }
        }

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            entries.add(new BarEntry(i, totals[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColor(Color.parseColor("#9B7FC7"));
        dataSet.setDrawValues(false);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        barChartWeekly.setData(barData);
        barChartWeekly.setFitBars(true);
        barChartWeekly.getDescription().setEnabled(false);
        barChartWeekly.getLegend().setEnabled(false);
        barChartWeekly.setDrawGridBackground(false);
        barChartWeekly.setDrawBorders(false);
        barChartWeekly.getAxisLeft().setEnabled(false);
        barChartWeekly.getAxisRight().setEnabled(false);
        barChartWeekly.setTouchEnabled(false);

        XAxis xAxis = barChartWeekly.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.parseColor("#6B6575"));
        xAxis.setTextSize(11f);

        barChartWeekly.invalidate();
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private Map<Long, Double> buildCatSpendingMap() {
        Map<Long, Double> catSpending = new HashMap<>();
        if (currentExpenses != null) {
            for (Expense e : currentExpenses) {
                if (e.getCategoryId() != null) {
                    catSpending.merge(e.getCategoryId(), e.getAmount(), Double::sum);
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