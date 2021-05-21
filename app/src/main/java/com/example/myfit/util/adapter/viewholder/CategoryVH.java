package com.example.myfit.util.adapter.viewholder;

import com.example.myfit.data.model.category.CategoryTuple;
import com.example.myfit.databinding.ItemCategoryBinding;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

public class CategoryVH extends BaseVH<CategoryTuple, ItemCategoryBinding> {
    private final ItemCategoryBinding binding;

    public CategoryVH(@NotNull ItemCategoryBinding binding, BaseVHListener listener) {
        super(binding, listener);
        this.binding = binding;
    }

    @Override
    public void bind(CategoryTuple tuple) {
        binding.setTuple(tuple);
    }

    @Override
    protected MaterialCheckBox getCheckBox() {
        return binding.cb;
    }
}