package com.example.project_myfit.util.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.ItemFolderListBinding;

import org.jetbrains.annotations.NotNull;

public class FolderListVH extends RecyclerView.ViewHolder {
    private final ItemFolderListBinding mBinding;
    private Folder mFolder;

    public FolderListVH(@NotNull ItemFolderListBinding binding, FolderVHListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;

        itemView.setOnClickListener(v -> listener.onFolderItemViewClick(mFolder, mBinding.cb));
        itemView.setOnLongClickListener(v -> {
            listener.onFolderItemViewLongClick(getLayoutPosition());
            return false;
        });
    }

    public void setFolder(Folder folder) {
        this.mFolder = folder;
        mBinding.setFolder(folder);
    }

    public ItemFolderListBinding getBinding() {
        return mBinding;
    }
}
