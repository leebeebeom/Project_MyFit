package com.example.myfit.util.adapter.viewholder.size;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.databinding.ItemSizeListBinding;
import com.example.myfit.util.adapter.viewholder.SizeVHListener;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

public class SizeListVH extends BaseSizeVH {
    private final ItemSizeListBinding binding;

    public SizeListVH(@NotNull ItemSizeListBinding binding, SizeVHListener listener) {
        super(binding, listener);
        this.binding = binding;

        binding.cbFavorite.setOnClickListener(v -> getListener().sizeFavoriteClick((SizeTuple) getTuple()));
    }

    @Override
    public void bind(SizeTuple tuple) {
        binding.setTuple(tuple);
    }

    @Override
    protected MaterialCheckBox getCheckBox() {
        return null;
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

