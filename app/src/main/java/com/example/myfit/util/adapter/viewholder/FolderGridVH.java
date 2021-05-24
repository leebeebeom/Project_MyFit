package com.example.myfit.util.adapter.viewholder;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.myfit.data.model.folder.FolderTuple;
import com.example.myfit.databinding.ItemFolderGridBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class FolderGridVH extends BaseVH<FolderTuple, BaseVHListener> {
    private final ItemFolderGridBinding binding;

    public FolderGridVH(@NotNull ItemFolderGridBinding binding, BaseVHListener listener, Set<Long> selectedIds) {
        super(binding, listener, selectedIds);
        this.binding = binding;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bind(FolderTuple tuple) {
        binding.setTuple(tuple);
        if (tuple.getId() == -1) {
            binding.getRoot().setVisibility(View.INVISIBLE);
            itemView.setOnClickListener(null);
            itemView.setOnLongClickListener(null);
            binding.iconDragHandle.setOnTouchListener(null);
        }
    }

    @Override
    public MaterialCheckBox getCheckBox() {
        return binding.cb;
    }

    @Override
    public AppCompatImageView getDragHandleIcon() {
        return binding.iconDragHandle;
    }

    @Override
    protected void setDraggingView() {
        setItemViewTranslationZ(itemView, 10);
        binding.layoutContentsSize.setVisibility(View.INVISIBLE);
        binding.tvFolderName.setAlpha(0.5f);
        binding.cb.setAlpha(0.5f);
    }

    @Override
    protected void setDropView() {
        setItemViewTranslationZ(itemView, 0);
        binding.layoutContentsSize.setVisibility(View.VISIBLE);
        binding.tvFolderName.setAlpha(1);
        binding.cb.setAlpha(0.8f);
    }

    @Override
    public MaterialCardView getCardView() {
        return binding.cardView;
    }
}
