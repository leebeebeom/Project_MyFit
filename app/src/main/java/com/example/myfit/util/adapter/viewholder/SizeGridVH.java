package com.example.myfit.util.adapter.viewholder;

import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.databinding.ItemSizeGridBinding;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

public class SizeGridVH extends BaseVH<SizeTuple, ItemSizeGridBinding> {
    private final ItemSizeGridBinding binding;

    public SizeGridVH(@NotNull ItemSizeGridBinding binding, SizeVHListener listener) {
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
        return binding.cb;
    }
}

