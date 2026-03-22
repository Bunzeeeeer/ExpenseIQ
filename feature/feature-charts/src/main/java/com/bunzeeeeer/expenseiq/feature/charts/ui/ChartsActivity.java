package com.bunzeeeeer.expenseiq.feature.charts.ui;

import android.graphics.Color;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.bunzeeeeer.expenseiq.core.data.AppDatabase;
import com.bunzeeeeer.expenseiq.core.data.repository.CategoryRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.data.repository.ExpenseRepositoryImpl;
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.core.domain.model.Expense;
import com.bunzeeeeer.expenseiq.core.ui.base.BaseActivity;
import com.bunzeeeeer.expenseiq.feature.charts.R;
import com.bunzeeeeer.expenseiq.feature.charts.data.ChartsRepository;
import com.bunzeeeeer.expenseiq.feature.charts.viewmodel.ChartsViewModel;
import com.bunzeeeeer.expenseiq.feature.charts.viewmodel.ChartsViewModelFactory;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-22-2026
 *
 */
@SuppressWarnings("java:S1450")
public class ChartsActivity extends BaseActivity {

    private ChartsViewModel viewModel;

    private PieChart pieChart;
    private LineChart lineChart;
    private BarChart barChart;

    // Pastel chart colors matching the app theme
    private static final int[] CHART_COLORS = {
            0xFFFFB3C6, 0xFFA8E6CF, 0xFFFFCBA4,
            0xFFB5D0FF, 0xFFD4BBFF, 0xFFD3D1C7
    };

    // ─── BaseActivity contract ────────────────────────────────────────────────

    @Override
    protected int getLayoutId() {
        return R.layout.activity_charts;
    }

    @Override
    protected void initDesign() {
        initToolbar();
        initCharts();
    }

    @Override
    protected void initViewModel() {
        initViewModelConstructor();
    }

    @Override
    protected void initObservers() {
        initChartsDataObserver();
        initErrorObserver();
    }

    // ─── Design ──────────────────────────────────────────────────────────────

    private void initToolbar() {
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initCharts() {
        pieChart = findViewById(R.id.pieChart);
        lineChart = findViewById(R.id.lineChart);
        barChart = findViewById(R.id.barChart);
        setupPieChart();
        setupLineChart();
        setupBarChart();
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(52f);
        pieChart.setTransparentCircleRadius(57f);
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText(getString(R.string.charts_pie_center_text));
        pieChart.setCenterTextSize(13f);
        pieChart.setCenterTextColor(0xFF6B6575);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setTextColor(0xFF6B6575);
        pieChart.getLegend().setTextSize(11f);
        pieChart.setEntryLabelColor(Color.TRANSPARENT);
        pieChart.animateY(800);
    }

    private void setupLineChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setTextColor(0xFF9B95A3);
        lineChart.getAxisLeft().setGridColor(0xFFF0EBF8);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setTextColor(0xFF9B95A3);
        lineChart.getXAxis().setGridColor(0xFFF0EBF8);
        lineChart.getXAxis().setGranularity(1f);
        lineChart.animateX(800);
    }

    private void setupBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setTextColor(0xFF9B95A3);
        barChart.getAxisLeft().setGridColor(0xFFF0EBF8);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setTextColor(0xFF9B95A3);
        barChart.getXAxis().setGridColor(0xFFF0EBF8);
        barChart.getXAxis().setGranularity(1f);
        barChart.animateY(800);
    }

    // ─── ViewModel ───────────────────────────────────────────────────────────

    private void initViewModelConstructor() {
        AppDatabase db = AppDatabase.getInstance(this);
        ChartsRepository repository = new ChartsRepository(
                new ExpenseRepositoryImpl(db.expenseDao()),
                new CategoryRepositoryImpl(db.categoryDao())
        );
        viewModel = new ViewModelProvider(this,
                new ChartsViewModelFactory(repository))
                .get(ChartsViewModel.class);
    }

    // ─── Observers ───────────────────────────────────────────────────────────

    private void initChartsDataObserver() {
        viewModel.getChartsData().observe(this, data -> {
            updateSummaryStats(data.getExpenses());
            updatePieChart(data.getExpenses(), data.getCategories());
            updateLineChart(data.getExpenses());
            updateBarChart(data.getExpenses());
        });
    }

    private void initErrorObserver() {
        viewModel.getError().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ─── Chart Updaters ───────────────────────────────────────────────────────

    private void updateSummaryStats(List<Expense> expenses) {
        double total = 0;
        for (Expense e : expenses) {
            total += e.getAmount();
        }
        TextView tvTotalAllTime = findViewById(R.id.tvTotalAllTime);
        TextView tvTotalTransactions = findViewById(R.id.tvTotalTransactions);
        tvTotalAllTime.setText(getString(R.string.charts_amount_format, total));
        tvTotalTransactions.setText(String.valueOf(expenses.size()));
    }

    private void updatePieChart(List<Expense> expenses, List<Category> categories) {
        // Build category lookup
        Map<Long, String> categoryNames = new HashMap<>();
        for (Category cat : categories) {
            categoryNames.put(cat.getId(), cat.getName());
        }

        // Sum by category
        Map<Long, Float> catTotals = new HashMap<>();
        for (Expense e : expenses) {
            if (e.getCategoryId() != null) {
                catTotals.merge(e.getCategoryId(), (float) e.getAmount(), Float::sum);
            }
        }

        if (catTotals.isEmpty()) {
            pieChart.setNoDataText(getString(R.string.charts_no_data));
            pieChart.setNoDataTextColor(0xFF9B95A3);
            pieChart.invalidate();
            return;
        }

        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();
        int idx = 0;
        for (Map.Entry<Long, Float> entry : catTotals.entrySet()) {
            String name = categoryNames.containsKey(entry.getKey())
                    ? categoryNames.get(entry.getKey())
                    : getString(R.string.charts_unknown_category);
            entries.add(new PieEntry(entry.getValue(), name));
            colors.add(CHART_COLORS[idx % CHART_COLORS.length]);
            idx++;
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setSliceSpace(2f);
        dataSet.setSelectionShift(4f);
        dataSet.setValueTextColor(Color.TRANSPARENT);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void updateLineChart(List<Expense> expenses) {
        // Group by day — key = "yyyy-MM-dd"
        SimpleDateFormat dayFmt = new SimpleDateFormat(
                getString(R.string.charts_day_format), Locale.getDefault());
        Map<String, Float> dailyTotals = new TreeMap<>();

        for (Expense e : expenses) {
            String day = dayFmt.format(new Date(e.getDate()));
            dailyTotals.merge(day, (float) e.getAmount(), Float::sum);
        }

        if (dailyTotals.isEmpty()) {
            lineChart.setNoDataText(getString(R.string.charts_no_data));
            lineChart.setNoDataTextColor(0xFF9B95A3);
            lineChart.invalidate();
            return;
        }

        List<Entry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int i = 0;

        SimpleDateFormat displayFmt = new SimpleDateFormat(
                getString(R.string.charts_day_display_format), Locale.getDefault());

        for (Map.Entry<String, Float> entry : dailyTotals.entrySet()) {
            entries.add(new Entry(i, entry.getValue()));
            try {
                Date date = dayFmt.parse(entry.getKey());
                labels.add(date != null ? displayFmt.format(date) : entry.getKey());
            } catch (Exception ex) {
                labels.add(entry.getKey());
            }
            i++;
        }

        LineDataSet dataSet = new LineDataSet(entries,
                getString(R.string.charts_line_label));
        dataSet.setColor(0xFF7B5EA7);
        dataSet.setCircleColor(0xFF7B5EA7);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(0xFFD4BBFF);
        dataSet.setFillAlpha(80);

        lineChart.getXAxis().setValueFormatter(
                new IndexAxisValueFormatter(labels));
        lineChart.getXAxis().setLabelCount(
                Math.min(labels.size(), 6), true);
        lineChart.setData(new LineData(dataSet));
        lineChart.invalidate();
    }

    private void updateBarChart(List<Expense> expenses) {
        // Group by month — key = "yyyy-MM"
        SimpleDateFormat monthFmt = new SimpleDateFormat(
                getString(R.string.charts_month_format), Locale.getDefault());
        Map<String, Float> monthlyTotals = new TreeMap<>();

        for (Expense e : expenses) {
            String month = monthFmt.format(new Date(e.getDate()));
            monthlyTotals.merge(month, (float) e.getAmount(), Float::sum);
        }

        if (monthlyTotals.isEmpty()) {
            barChart.setNoDataText(getString(R.string.charts_no_data));
            barChart.setNoDataTextColor(0xFF9B95A3);
            barChart.invalidate();
            return;
        }

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int i = 0;

        SimpleDateFormat displayFmt = new SimpleDateFormat(
                getString(R.string.charts_month_display_format), Locale.getDefault());

        for (Map.Entry<String, Float> entry : monthlyTotals.entrySet()) {
            entries.add(new BarEntry(i, entry.getValue()));
            try {
                Date date = monthFmt.parse(entry.getKey());
                labels.add(date != null ? displayFmt.format(date) : entry.getKey());
            } catch (Exception ex) {
                labels.add(entry.getKey());
            }
            i++;
        }

        BarDataSet dataSet = new BarDataSet(entries,
                getString(R.string.charts_bar_label));
        dataSet.setColors(CHART_COLORS);
        dataSet.setValueTextColor(0xFF6B6575);
        dataSet.setValueTextSize(10f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f);

        barChart.getXAxis().setValueFormatter(
                new IndexAxisValueFormatter(labels));
        barChart.getXAxis().setLabelCount(
                Math.min(labels.size(), 6), true);
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.invalidate();
    }
}