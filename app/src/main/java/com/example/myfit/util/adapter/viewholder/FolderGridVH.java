package com.example.myfit.util.adapter.viewholder;

import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.myfit.data.model.folder.FolderTuple;
import com.example.myfit.databinding.ItemFolderGridBinding;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

public class FolderGridVH extends BaseVH<FolderTuple> {
    private final ItemFolderGridBinding binding;

    public FolderGridVH(@NotNull ItemFolderGridBinding binding, BaseVHListener listener) {
        super(binding, listener);
        this.binding = binding;
    }

    @Override
    protected void bind(FolderTuple tuple) {
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

    @Override
    protected AppCompatImageView getDragHandleIcon() {
        return binding.iconDragHandle;
    }

    @Override
    protected void setDraggingView() {
        setItemViewTranslationZ(itemView, 10);
        binding.layoutContentsSize.setVisibility(View.INVISIBLE);
        binding.tvFolderName.setAlpha(0.5f);
        binding.cb.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setDropView() {
        setItemViewTranslationZ(itemView, 0);
        binding.layoutContentsSize.setVisibility(View.VISIBLE);
        binding.tvFolderName.setAlpha(1);
        binding.cb.setVisibility(View.VISIBLE);
    }
}
