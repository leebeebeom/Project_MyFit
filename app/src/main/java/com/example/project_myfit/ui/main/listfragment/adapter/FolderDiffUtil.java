package com.example.project_myfit.ui.main.listfragment.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.project_myfit.ui.main.listfragment.database.Folder;

import org.jetbrains.annotations.NotNull;

public class FolderDiffUtil extends DiffUtil.ItemCallback<Folder> {
    @Override
    public boolean areItemsTheSame(@NonNull @NotNull Folder oldItem, @NonNull @NotNull Folder newItem) {
        return oldItem.getId() == newItem.getId();
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull Folder oldItem, @NonNull @NotNull Folder newItem) {
        return oldItem.getFolderName().equals(newItem.getFolderName()) &&
                oldItem.getItemAmount().equals(newItem.getItemAmount());
    }
}
