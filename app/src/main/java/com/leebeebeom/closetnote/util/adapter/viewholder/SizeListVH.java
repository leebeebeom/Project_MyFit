package com.leebeebeom.closetnote.util.adapter.viewholder;

import com.leebeebeom.closetnote.data.tuple.tuple.SizeTuple;
import com.leebeebeom.closetnote.databinding.ItemVhSizeListBinding;
import com.leebeebeom.closetnote.util.SizeLiveSet;
import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

public class SizeListVH extends BaseVH.BaseSizeVH {
    private final ItemVhSizeListBinding mBinding;

    public SizeListVH(@NotNull ItemVhSizeListBinding binding, BaseVHListener.SizeVHListener listener, SizeLiveSet<SizeTuple> selectedItems) {
        super(binding, listener, selectedItems);
        this.mBinding = binding;
    }

    @Override
    public void bind() {
        mBinding.setSizeListVH(this);
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

