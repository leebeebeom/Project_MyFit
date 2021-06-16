package com.example.myfit.util.adapter.viewholder;

import com.example.myfit.data.tuple.tuple.SizeTuple;
import com.example.myfit.databinding.ItemVhSizeGridBinding;
import com.example.myfit.util.SizeLiveSet;
import com.example.myfit.util.adapter.BaseAdapter;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class SizeGridVH extends BaseVH.BaseSizeVH {
    private final ItemVhSizeGridBinding mBinding;

    public SizeGridVH(@NotNull ItemVhSizeGridBinding binding, BaseVHListener.SizeVHListener listener, SizeLiveSet<SizeTuple> selectedItems) {
        super(binding, listener, selectedItems);
        this.mBinding = binding;
    }

    @Override
    public void bind() {
        mBinding.setSizeGridVH(this);
    }

    @Override
    public MaterialCheckBox getCheckBox() {
        return mBinding.cb;
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

