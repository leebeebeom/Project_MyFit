package com.example.project_myfit.ui.main.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.project_myfit.ui.main.database.Category;

import org.jetbrains.annotations.NotNull;

public class CategoryDiffUtil extends DiffUtil.ItemCallback<Category> {

    @Override
    public boolean areItemsTheSame(@NonNull @NotNull Category oldItem, @NonNull @NotNull Category newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull Category oldItem, @NonNull @NotNull Category newItem) {
        return oldItem.getCategory().equals(newItem.getCategory()) &&
                oldItem.getItemAmount().equals(newItem.getItemAmount());
    }
}
