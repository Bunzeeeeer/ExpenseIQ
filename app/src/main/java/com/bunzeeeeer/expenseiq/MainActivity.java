package com.bunzeeeeer.expenseiq;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bunzeeeeer.expenseiq.feature.budget.ui.BudgetListActivity;
import com.bunzeeeeer.expenseiq.feature.dashboard.ui.DashboardActivity;
import com.bunzeeeeer.expenseiq.feature.expense.ui.ExpenseListActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Launch Dashboard by default
        navigateToDashboard();
        bottomNav.setSelectedItemId(R.id.nav_dashboard);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                navigateToDashboard();
                return true;
            } else if (id == R.id.nav_expenses) {
                navigateToExpenses();
                return true;
            } else if (id == R.id.nav_budget) {
                navigateToBudget();
                return true;
            }
            // Other tabs wired when features are built
            return false;
        });
    }

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

    private void navigateToBudget() {
        Intent intent = new Intent(this, BudgetListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}