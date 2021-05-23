package com.example.myfit.util.adapter.viewholder;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.myfit.data.model.folder.FolderTuple;
import com.example.myfit.databinding.ItemFolderListBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FolderListVH extends BaseVH<FolderTuple, BaseVHListener> {
    private final ItemFolderListBinding binding;

    public FolderListVH(@NotNull ItemFolderListBinding binding, BaseVHListener listener, Set<Long> selectedIds) {
        super(binding, listener, selectedIds);
        this.binding = binding;
    }

    protected void bind(FolderTuple tuple) {
        binding.setTuple(tuple);
    }

    @Override
    public MaterialCheckBox getCheckBox() {
        return binding.cb;
    }

    @Override
    public AppCompatImageView getDragHandleIcon() {
        return null;
    }

    @Override
    protected void setDraggingView() {
    }

    @Override
    protected void setDropView() {
    }

    @Override
    public MaterialCardView getCardView() {
        return binding.cardView;
    }
}
