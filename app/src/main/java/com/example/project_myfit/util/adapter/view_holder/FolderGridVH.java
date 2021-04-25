package com.example.project_myfit.util.adapter.view_holder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.ItemFolderGridBinding;

import org.jetbrains.annotations.NotNull;

public class FolderGridVH extends RecyclerView.ViewHolder {
    private final ItemFolderGridBinding mBinding;
    private Folder mFolder;

    public FolderGridVH(@NotNull ItemFolderGridBinding binding, FolderVHListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;

        itemView.setOnClickListener(v -> {
            if (mFolder.getId() != -1)
                listener.onFolderItemViewClick(mFolder, mBinding.cbItemFolderGrid);
        });

        itemView.setOnLongClickListener(v -> {
            if (mFolder.getId() != -1)
                listener.onFolderItemViewLongClick(getLayoutPosition());
            return false;
        });
    }

    public void setFolder(Folder folder) {
        this.mFolder = folder;
        mBinding.setFolder(folder);
    }

    public ItemFolderGridBinding getBinding() {
        return mBinding;
    }
}
