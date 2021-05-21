package com.example.myfit.util.adapter.viewholder;

import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.myfit.data.model.category.CategoryTuple;
import com.example.myfit.databinding.ItemCategoryBinding;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

public class CategoryVH extends BaseVH<CategoryTuple> {
    private final ItemCategoryBinding binding;

    public CategoryVH(@NotNull ItemCategoryBinding binding, BaseVHListener listener) {
        super(binding);
        this.binding = binding;
    }

    @Override
    protected void bind(CategoryTuple tuple) {
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
        binding.cb.setVisibility(View.INVISIBLE);
        binding.tvCategoryName.setAlpha(0.5f);
        binding.layoutContentsSize.setAlpha(0.5f);
    }

    @Override
    protected void setDropView() {
        setItemViewTranslationZ(itemView, 10);
        binding.cb.setVisibility(View.VISIBLE);
        binding.tvCategoryName.setAlpha(0.8f);
        binding.layoutContentsSize.setAlpha(0.8f);
    }
}