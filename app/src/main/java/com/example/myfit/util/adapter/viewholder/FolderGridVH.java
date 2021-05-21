package com.example.myfit.util.adapter.viewholder;

import android.view.View;

import com.example.myfit.data.model.folder.FolderTuple;
import com.example.myfit.databinding.ItemFolderGridBinding;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

public class FolderGridVH extends BaseVH<FolderTuple, ItemFolderGridBinding> {
    private final ItemFolderGridBinding binding;

    public FolderGridVH(@NotNull ItemFolderGridBinding binding, BaseVHListener listener) {
        super(binding, listener);
        this.binding = binding;
    }

    @Override
    public void bind(FolderTuple tuple) {
        binding.setTuple(tuple);
        if (tuple.getId() == -1) {
            binding.getRoot().setVisibility(View.INVISIBLE);
            binding.getRoot().setEnabled(false);
        }
    }

    @Override
    protected MaterialCheckBox getCheckBox() {
        return binding.cb;
    }
}
