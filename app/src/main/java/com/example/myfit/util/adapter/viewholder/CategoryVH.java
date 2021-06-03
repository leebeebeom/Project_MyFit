package com.example.myfit.util.adapter.viewholder;

import android.view.View;

import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.databinding.ItemCategoryBinding;
import com.example.myfit.util.adapter.BaseAdapter;
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
    protected void bind() {
        mBinding.setCategoryVH(this);
    }

    @Override
    public MaterialCheckBox getCheckBox() {
        return mBinding.cb;
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

    @Override
    public void setAdapter(BaseAdapter<?, ?, ?> baseAdapter) {
        mBinding.setBaseAdapter(baseAdapter);
    }
}