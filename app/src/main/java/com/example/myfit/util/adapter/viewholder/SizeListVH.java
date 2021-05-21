package com.example.myfit.util.adapter.viewholder;

import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.databinding.ItemSizeListBinding;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

public class SizeListVH extends BaseVH<SizeTuple, ItemSizeListBinding> {
    private final ItemSizeListBinding binding;

    public SizeListVH(@NotNull ItemSizeListBinding binding, SizeVHListener listener) {
        super(binding, listener);
        this.binding = binding;

        binding.cbFavorite.setOnClickListener(v -> listener.sizeFavoriteClick((SizeTuple) getTuple()));
    }

    @Override
    public void bind(SizeTuple tuple) {
        binding.setTuple(tuple);
    }

    @Override
    protected MaterialCheckBox getCheckBox() {
        return null;
    }
}

