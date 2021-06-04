package com.example.myfit.util.adapter.viewholder;

import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.databinding.ItemVhFolderListBinding;
import com.example.myfit.util.adapter.BaseAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Set;

public class FolderListVH extends BaseVH<FolderTuple, BaseVHListener> {
    private final ItemVhFolderListBinding mBinding;

    public FolderListVH(ItemVhFolderListBinding binding, BaseVHListener listener, Set<FolderTuple> selectedItems) {
        super(binding, listener, selectedItems);
        this.mBinding = binding;
    }

    protected void bind() {
        mBinding.setFolderListVH(this);
    }

    @Override
    public MaterialCheckBox getCheckBox() {
        return mBinding.cb;
    }

    @Override
    protected void setDraggingView() {
    }

    @Override
    protected void setDropView() {
    }

    @Override
    public MaterialCardView getCardView() {
        return mBinding.cardView.cardView;
    }

    @Override
    public void setAdapter(BaseAdapter<?, ?, ?> baseAdapter) {
        mBinding.setBaseAdapter(baseAdapter);
    }
}
