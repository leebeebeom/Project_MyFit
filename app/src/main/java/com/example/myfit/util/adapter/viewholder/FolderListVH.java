package com.example.myfit.util.adapter.viewholder;

import android.view.View;

import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.databinding.ItemVhFolderListBinding;
import com.example.myfit.util.SizeLiveSet;
import com.example.myfit.util.adapter.BaseAdapter;
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
