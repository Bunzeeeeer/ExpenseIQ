package com.bunzeeeeer.expenseiq.feature.expense.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bunzeeeeer.expenseiq.core.domain.model.Expense;
import com.bunzeeeeer.expenseiq.feature.expense.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-15-2026
 *
 */
public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ViewHolder> {

    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    private List<Expense> expenses = new ArrayList<>();
    private OnExpenseClickListener listener;

    public void setExpenses(List<Expense> newExpenses) {
        int oldSize = this.expenses.size();
        this.expenses = newExpenses;
        int newSize = this.expenses.size();
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

    public void setOnExpenseClickListener(OnExpenseClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        holder.tvTitle.setText(expense.getTitle());
        holder.tvAmount.setText(holder.itemView.getContext()
                .getString(R.string.expense_amount_negative_format, expense.getAmount()));
        SimpleDateFormat sdf = new SimpleDateFormat(
                holder.itemView.getContext().getString(R.string.expense_date_format),
                Locale.getDefault());
        holder.tvDate.setText(sdf.format(new Date(expense.getDate())));
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExpenseClick(expense);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static final class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvTitle;
        private final TextView tvAmount;
        private final TextView tvDate;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvExpenseTitle);
            tvAmount = itemView.findViewById(R.id.tvExpenseAmount);
            tvDate = itemView.findViewById(R.id.tvExpenseDate);
        }
    }
}