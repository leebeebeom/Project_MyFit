package com.example.myfit.util.adapter.viewholder;

import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.myfit.data.model.category.CategoryTuple;
import com.example.myfit.databinding.ItemCategoryBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class CategoryVH extends BaseVH<CategoryTuple, BaseVHListener> {
    private final ItemCategoryBinding mBinding;

    public CategoryVH(@NotNull ItemCategoryBinding binding, BaseVHListener listener, Set<CategoryTuple> selectedItems) {
        super(binding, listener, selectedItems);
        this.mBinding = binding;
    }

    @Override
    protected void bind(CategoryTuple tuple) {
        mBinding.setTuple(tuple);
    }

    @Override
    public MaterialCheckBox getCheckBox() {
        return mBinding.cb;
    }

    @Override
    public AppCompatImageView getDragHandleIcon() {
        return mBinding.iconDragHandle;
    }

    @Override
    protected void setDraggingView() {
        setItemViewTranslationZ(itemView, 10);
        mBinding.cb.setVisibility(View.INVISIBLE);
        mBinding.tvCategoryName.setAlpha(0.5f);
        mBinding.layoutContentsSize.setAlpha(0.5f);
    }

    @Override
    protected void setDropView() {
        setItemViewTranslationZ(itemView, 10);
        mBinding.cb.setVisibility(View.VISIBLE);
        mBinding.tvCategoryName.setAlpha(0.8f);
        mBinding.layoutContentsSize.setAlpha(0.8f);
    }

    @Override
    public MaterialCardView getCardView() {
        return mBinding.cardView;
    }
}