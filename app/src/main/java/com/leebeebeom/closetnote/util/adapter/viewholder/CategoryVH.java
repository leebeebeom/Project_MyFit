package com.leebeebeom.closetnote.util.adapter.viewholder;

import android.view.View;

import com.leebeebeom.closetnote.data.tuple.tuple.CategoryTuple;
import com.leebeebeom.closetnote.databinding.ItemVhCategoryBinding;
import com.leebeebeom.closetnote.util.SizeLiveSet;
import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

public class CategoryVH extends BaseVH<CategoryTuple, BaseVHListener> {
    private final ItemVhCategoryBinding mBinding;

    public CategoryVH(@NotNull ItemVhCategoryBinding binding, BaseVHListener listener, SizeLiveSet<CategoryTuple> selectedItems) {
        super(binding, listener, selectedItems);
        binding.cardView.dotIcon.setVisibility(View.VISIBLE);
        this.mBinding = binding;
    }

    @Override
    public void bind() {
        mBinding.setCategoryVH(this);
    }

    @Override
    public MaterialCheckBox getCheckBox() {
        return mBinding.cb.cb;
    }

    @Override
    protected void setDragging() {
        mBinding.setDragging(sDragging);
    }

    @Override
    public void setAdapter(BaseAdapter<?, ?, ?> baseAdapter) {
        mBinding.setBaseAdapter(baseAdapter);
    }
}