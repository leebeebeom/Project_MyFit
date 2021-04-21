package com.example.project_myfit.searchActivity.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;

import org.jetbrains.annotations.NotNull;

public class SearchDiffUtil extends DiffUtil.ItemCallback<Object> {
    @Override
    public boolean areItemsTheSame(@NonNull @NotNull Object oldItem, @NonNull @NotNull Object newItem) {
        if (oldItem instanceof Folder && newItem instanceof Folder)
            return ((Folder) oldItem).getId() == ((Folder) newItem).getId();
        else if (oldItem instanceof Size && newItem instanceof Size)
            return ((Size) oldItem).getId() == ((Size) newItem).getId();
        return false;
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull Object oldItem, @NonNull @NotNull Object newItem) {
        if (oldItem instanceof Folder && newItem instanceof Folder)
            return ((Folder) oldItem).getFolderName().equals(((Folder) newItem).getFolderName()) &&
                    ((Folder) oldItem).getDummy() == ((Folder) newItem).getDummy() &&
                    ((Folder) oldItem).getParentId() == ((Folder) newItem).getParentId();
        else if (oldItem instanceof Size && newItem instanceof Size)
            return ((Size) oldItem).getBrand().equals(((Size) newItem).getBrand()) &&
                    ((Size) oldItem).getName().equals(((Size) newItem).getName()) &&
                    String.valueOf(((Size) oldItem).getImageUri()).equals(String.valueOf(((Size) newItem).getImageUri())) &&
                    ((Size) oldItem).getParentId() == ((Size) newItem).getParentId();
        return false;
    }
}
