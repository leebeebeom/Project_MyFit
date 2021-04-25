package com.example.project_myfit.util.adapter.view_holder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.ItemFolderListBinding;

import org.jetbrains.annotations.NotNull;

public class FolderListVH extends RecyclerView.ViewHolder {
    private final ItemFolderListBinding mBinding;
    private Folder mFolder;

    public FolderListVH(@NotNull ItemFolderListBinding folderBinding, FolderVHListener listener) {
        super(folderBinding.getRoot());
        this.mBinding = folderBinding;

        itemView.setOnClickListener(v -> listener.onFolderItemViewClick(mFolder, mBinding.cbItemFolderList));
        itemView.setOnLongClickListener(v -> {
            listener.onFolderItemViewLongClick(getLayoutPosition());
            return false;
        });
    }

    public void setFolder(Folder folder) {
        mBinding.setFolder(folder);
        this.mFolder = folder;
    }

    public ItemFolderListBinding getBinding() {
        return mBinding;
    }
}
