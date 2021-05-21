package com.example.myfit.util.adapter.viewholder;

import com.example.myfit.data.model.folder.FolderTuple;
import com.example.myfit.databinding.ItemFolderListBinding;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

public class FolderListVH extends BaseVH<FolderTuple, ItemFolderListBinding> {
    private final ItemFolderListBinding binding;

    public FolderListVH(@NotNull ItemFolderListBinding binding, BaseVHListener listener) {
        super(binding, listener);
        this.binding = binding;
    }

    public void bind(FolderTuple tuple) {
        binding.setTuple(tuple);
    }

    @Override
    protected MaterialCheckBox getCheckBox() {
        return binding.cb;
    }
}
