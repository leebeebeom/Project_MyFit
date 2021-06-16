package com.example.myfit.util.adapter.viewholder;

import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.databinding.ItemVhFolderGridBinding;
import com.example.myfit.util.SizeLiveSet;
import com.example.myfit.util.adapter.BaseAdapter;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class FolderGridVH extends BaseVH<FolderTuple, BaseVHListener> {
    private final ItemVhFolderGridBinding mBinding;

    public FolderGridVH(@NotNull ItemVhFolderGridBinding binding, BaseVHListener listener, SizeLiveSet<FolderTuple> selectedItems) {
        super(binding, listener, selectedItems);
        this.mBinding = binding;
    }

    @Override
    public void bind() {
            mBinding.setFolderGridVH(this);
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
