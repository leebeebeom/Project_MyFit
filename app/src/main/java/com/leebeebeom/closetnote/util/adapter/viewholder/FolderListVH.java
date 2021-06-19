package com.leebeebeom.closetnote.util.adapter.viewholder;

import android.view.View;

import com.leebeebeom.closetnote.data.tuple.tuple.FolderTuple;
import com.leebeebeom.closetnote.databinding.ItemVhFolderListBinding;
import com.leebeebeom.closetnote.util.SizeLiveSet;
import com.leebeebeom.closetnote.util.adapter.BaseAdapter;
import com.google.android.material.checkbox.MaterialCheckBox;

public class FolderListVH extends BaseVH<FolderTuple, BaseVHListener> {
    private final ItemVhFolderListBinding mBinding;

    public FolderListVH(ItemVhFolderListBinding binding, BaseVHListener listener, SizeLiveSet<FolderTuple> selectedItems) {
        super(binding, listener, selectedItems);
        binding.cardView.folderIcon.setVisibility(View.VISIBLE);
        this.mBinding = binding;
    }

    public void bind() {
        mBinding.setFolderListVH(this);
    }

    @Override
    public MaterialCheckBox getCheckBox() {
        return mBinding.cb.cb;
    }

    @Override
    protected void setDragging() {

    }

    @Override
    public void setAdapter(BaseAdapter<?, ?, ?> baseAdapter) {
        mBinding.setBaseAdapter(baseAdapter);
    }
}
