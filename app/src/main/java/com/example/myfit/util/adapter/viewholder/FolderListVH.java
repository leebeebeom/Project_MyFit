package com.example.myfit.util.adapter.viewholder;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.databinding.ItemFolderListBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.Set;

public class FolderListVH extends BaseVH<FolderTuple, BaseVHListener> {
    private final ItemFolderListBinding mBinding;

    public FolderListVH(ItemFolderListBinding binding, BaseVHListener listener, Set<FolderTuple> selectedItems) {
        super(binding, listener, selectedItems);
        this.mBinding = binding;
    }

    protected void bind(FolderTuple tuple) {
        mBinding.setTuple(tuple);
    }

    @Override
    public MaterialCheckBox getCheckBox() {
        return mBinding.cb;
    }

    @Override
    public AppCompatImageView getDragHandleIcon() {
        return null;
    }

    @Override
    protected void setDraggingView() {
    }

    @Override
    protected void setDropView() {
    }

    @Override
    public MaterialCardView getCardView() {
        return mBinding.cardView;
    }
}
