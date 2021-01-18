package com.example.project_myfit.ui.main.listfragment.adapter.sizeadapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.project_myfit.ui.main.listfragment.database.Size;

import org.jetbrains.annotations.NotNull;

public class SizeDiffUtil extends DiffUtil.ItemCallback<Size> {
    @Override
    public boolean areItemsTheSame(@NonNull @NotNull Size oldItem, @NonNull @NotNull Size newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull Size oldItem, @NonNull @NotNull Size newItem) {
        return String.valueOf(oldItem.getBrand()).equals(String.valueOf(newItem.getBrand())) &&
                String.valueOf(oldItem.getName()).equals(String.valueOf(newItem.getName())) &&
                String.valueOf(oldItem.getImageUri()).equals(String.valueOf(newItem.getImageUri()));
    }
}
