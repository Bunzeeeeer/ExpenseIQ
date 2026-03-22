package com.bunzeeeeer.expenseiq.feature.budget.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bunzeeeeer.expenseiq.core.domain.model.Budget;
import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.feature.budget.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @Author: Lance Joshua Corcega, Claude AI
 * @Date: 03-22-2026
 *
 */
public class BudgetListAdapter extends RecyclerView.Adapter<BudgetListAdapter.ViewHolder> {

    public interface OnBudgetClickListener {
        void onBudgetClick(Budget budget);
    }

    private List<Budget> budgets = new ArrayList<>();
    private final Map<Long, Category> categoryMap = new HashMap<>();
    private OnBudgetClickListener listener;

    public void setBudgets(List<Budget> newBudgets) {
        int oldSize = this.budgets.size();
        this.budgets = newBudgets;
        int newSize = this.budgets.size();
        if (oldSize == newSize) {
            notifyItemRangeChanged(0, newSize);
        } else if (newSize > oldSize) {
            notifyItemRangeChanged(0, oldSize);
            notifyItemRangeInserted(oldSize, newSize - oldSize);
        } else {
            notifyItemRangeChanged(0, newSize);
            notifyItemRangeRemoved(newSize, oldSize - newSize);
        }
    }

    public void setCategories(List<Category> categories) {
        categoryMap.clear();
        for (Category cat : categories) {
            categoryMap.put(cat.getId(), cat);
        }
        notifyItemRangeChanged(0, budgets.size());
    }

    public void setOnBudgetClickListener(OnBudgetClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Budget budget = budgets.get(position);

        Category category = budget.getCategoryId() != null
                ? categoryMap.get(budget.getCategoryId())
                : null;

        String categoryName = category != null
                ? category.getName()
                : holder.itemView.getContext().getString(R.string.budget_unknown_category);

        String categoryIcon = category != null
                ? category.getIcon()
                : holder.itemView.getContext().getString(R.string.budget_default_icon);

        holder.tvCategoryIcon.setText(categoryIcon);
        holder.tvCategoryName.setText(categoryName);
        holder.tvLimitAmount.setText(holder.itemView.getContext()
                .getString(R.string.budget_amount_format, budget.getLimitAmount()));
        holder.tvMonthYear.setText(holder.itemView.getContext()
                .getString(R.string.budget_month_year_format,
                        budget.getMonth(), budget.getYear()));

        // Progress bar — 0 for now, will be wired with actual spending later
        holder.progressBudget.setProgressCompat(0, false);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onBudgetClick(budget);
            }
        });
    }

    @Override
    public int getItemCount() {
        return budgets.size();
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvCategoryIcon;
        final TextView tvCategoryName;
        final TextView tvLimitAmount;
        final TextView tvMonthYear;
        final LinearProgressIndicator progressBudget;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryIcon = itemView.findViewById(R.id.tvCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvLimitAmount = itemView.findViewById(R.id.tvLimitAmount);
            tvMonthYear = itemView.findViewById(R.id.tvMonthYear);
            progressBudget = itemView.findViewById(R.id.progressBudget);
        }
    }
}