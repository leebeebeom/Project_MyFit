package com.example.myfit.util.adapter.viewholder;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.myfit.data.model.folder.FolderTuple;
import com.example.myfit.databinding.ItemFolderListBinding;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

public class FolderListVH extends BaseVH<FolderTuple> {
    private final ItemFolderListBinding binding;

    public FolderListVH(@NotNull ItemFolderListBinding binding, BaseVHListener listener) {
        super(binding, listener);
        this.binding = binding;
    }

    protected void bind(FolderTuple tuple) {
        binding.setTuple(tuple);
    }

    @Override
    protected MaterialCheckBox getCheckBox() {
        return binding.cb;
    }

    @Override
    protected AppCompatImageView getDragHandleIcon() {
        return null;
    }

    @Override
    protected void setDraggingView() {
    }

    @Override
    protected void setDropView() {
    }
}
