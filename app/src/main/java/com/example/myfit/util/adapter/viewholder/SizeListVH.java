package com.example.myfit.util.adapter.viewholder;

import android.view.View;

import com.example.myfit.data.tuple.tuple.SizeTuple;
import com.example.myfit.databinding.ItemSizeListBinding;
import com.example.myfit.util.adapter.BaseAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class SizeListVH extends BaseVH.BaseSizeVH {
    private final ItemSizeListBinding mBinding;

    public SizeListVH(@NotNull ItemSizeListBinding binding, BaseVHListener.SizeVHListener listener, Set<SizeTuple> selectedItems) {
        super(binding, listener, selectedItems);
        this.mBinding = binding;
    }

    @Override
    public void bind() {
        mBinding.setSizeListVH(this);
    }

    @Override
    public MaterialCheckBox getCheckBox() {
        return mBinding.cb;
    }

    @Override
    protected void setDraggingView() {
        setItemViewTranslationZ(itemView, 10);
        mBinding.cb.setVisibility(View.INVISIBLE);
        mBinding.nameLayout.layout.setAlpha(0.7f);
        mBinding.iv.setAlpha(0.5f);
    }

    @Override
    protected void setDropView() {
        setItemViewTranslationZ(itemView, 0);
        mBinding.cb.setVisibility(View.VISIBLE);
        mBinding.nameLayout.layout.setAlpha(1);
        mBinding.iv.setAlpha(1f);
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

