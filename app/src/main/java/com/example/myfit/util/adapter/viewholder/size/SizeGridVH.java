package com.example.myfit.util.adapter.viewholder.size;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.databinding.ItemSizeGridBinding;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

public class SizeGridVH extends BaseSizeVH {
    private final ItemSizeGridBinding binding;

    public SizeGridVH(@NotNull ItemSizeGridBinding binding) {
        super(binding);
        this.binding = binding;

        binding.cbFavorite.setOnClickListener(v -> getListener().sizeFavoriteClick((SizeTuple) getTuple()));
    }

    @Override
    protected void bind(SizeTuple tuple) {
        binding.setTuple(tuple);
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
        binding.iv.setAlpha(0.5f);
        binding.layoutContents.setAlpha(0.7f);
        binding.cb.setAlpha(0.5f);
    }

    @Override
    protected void setDropView() {
        setItemViewTranslationZ(itemView, 0);
        binding.iv.setAlpha(1f);
        binding.layoutContents.setAlpha(1);
        binding.cb.setAlpha(0.8f);
    }
}

