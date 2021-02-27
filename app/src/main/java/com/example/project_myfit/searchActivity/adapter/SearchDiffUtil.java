package com.example.project_myfit.searchActivity.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;

import org.jetbrains.annotations.NotNull;

public class SearchDiffUtil extends DiffUtil.ItemCallback<Object> {
    @Override
    public boolean areItemsTheSame(@NonNull @NotNull Object oldItem, @NonNull @NotNull Object newItem) {
        if (oldItem instanceof Folder && newItem instanceof Folder)
            return ((Folder) oldItem).getId() == ((Folder) newItem).getId();
        else if (oldItem instanceof Folder && newItem instanceof Size)
            return false;
        else if (oldItem instanceof Size && newItem instanceof Folder)
            return false;
        else if (oldItem instanceof Size && newItem instanceof Size)
            return ((Size) oldItem).getId() == ((Size) newItem).getId();
        return false;
    }

    @Override
    public boolean areContentsTheSame(@NonNull @NotNull Object oldItem, @NonNull @NotNull Object newItem) {
        if (oldItem instanceof Folder && newItem instanceof Folder)
            return ((Folder) oldItem).getFolderName().equals(((Folder) newItem).getFolderName());
        else if (oldItem instanceof Folder && newItem instanceof Size)
            return false;
        else if (oldItem instanceof Size && newItem instanceof Folder)
            return false;
        else if (oldItem instanceof Size && newItem instanceof Size)
            return ((Size) oldItem).getBrand().equals(((Size) newItem).getBrand()) &&
                    ((Size) oldItem).getName().equals(((Size) newItem).getName());
        return false;
    }
}
