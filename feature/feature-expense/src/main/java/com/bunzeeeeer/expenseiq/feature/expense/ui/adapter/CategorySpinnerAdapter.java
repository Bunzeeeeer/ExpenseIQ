package com.bunzeeeeer.expenseiq.feature.expense.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bunzeeeeer.expenseiq.core.domain.model.Category;
import com.bunzeeeeer.expenseiq.feature.expense.R;

import java.util.List;

/**
 *
 * @Author: Lance Joshua Corcega
 * @Date: 03-15-2026
 *
 */
public class CategorySpinnerAdapter extends ArrayAdapter<Category> {

    private final LayoutInflater inflater;

    public CategorySpinnerAdapter(@NonNull Context context, @NonNull List<Category> categories) {
        super(context, 0, categories);
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createView(position, convertView, parent);
    }

    private View createView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_category_spinner, parent, false);
        }
        Category category = getItem(position);
        TextView tvName = convertView.findViewById(R.id.tvCategoryName);
        if (category != null) {
            tvName.setText(category.getName());
        }
        return convertView;
    }
}