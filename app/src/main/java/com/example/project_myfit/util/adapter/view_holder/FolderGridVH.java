package com.example.project_myfit.util.adapter.view_holder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.ItemListRecyclerFolderBinding;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

public class FolderGridVH extends RecyclerView.ViewHolder {
    private final ItemListRecyclerFolderBinding mBinding;
    private Folder mFolder;

    public FolderGridVH(@NotNull ItemListRecyclerFolderBinding binding, FolderGridVHListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;

        itemView.setOnClickListener(v -> {
            if (mFolder.getId() != -1)
                listener.onFolderItemViewClick(mFolder, mBinding.itemListFolderCheckBox);
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

    public ItemListRecyclerFolderBinding getBinding() {
        return mBinding;
    }

    public interface FolderGridVHListener {
        void onFolderItemViewClick(Folder folder, MaterialCheckBox checkBox);

        void onFolderItemViewLongClick(int position);

        void onFolderDragHandleTouch(RecyclerView.ViewHolder holder);
    }
}
