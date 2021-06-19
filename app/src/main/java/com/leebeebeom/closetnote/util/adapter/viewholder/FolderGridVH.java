package com.leebeebeom.closetnote.util.adapter.viewholder;

import com.leebeebeom.closetnote.data.tuple.tuple.FolderTuple;
import com.leebeebeom.closetnote.databinding.ItemVhFolderGridBinding;
import com.leebeebeom.closetnote.util.SizeLiveSet;
import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
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
