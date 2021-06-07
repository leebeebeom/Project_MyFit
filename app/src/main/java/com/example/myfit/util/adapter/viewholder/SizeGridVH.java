package com.example.myfit.util.adapter.viewholder;

import com.example.myfit.data.tuple.tuple.SizeTuple;
import com.example.myfit.databinding.ItemVhSizeGridBinding;
import com.example.myfit.util.adapter.BaseAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class SizeGridVH extends BaseVH.BaseSizeVH {
    private final ItemVhSizeGridBinding mBinding;

    public SizeGridVH(@NotNull ItemVhSizeGridBinding binding, BaseVHListener.SizeVHListener listener, Set<SizeTuple> selectedItems) {
        super(binding, listener, selectedItems);
        this.mBinding = binding;
    }

    @Override
    protected void bind() {
        mBinding.setSizeGridVH(this);
    }

    @Override
    public MaterialCheckBox getCheckBox() {
        return mBinding.cb;
    }

    @Override
    protected void setDraggingView() {
        setItemViewTranslationZ(itemView, 10);
        mBinding.iv.setAlpha(0.5f);
        mBinding.layoutContents.setAlpha(0.7f);
        mBinding.cb.setAlpha(0.5f);
    }

    @Override
    protected void setDropView() {
        setItemViewTranslationZ(itemView, 0);
        mBinding.iv.setAlpha(1f);
        mBinding.layoutContents.setAlpha(1);
        mBinding.cb.setAlpha(0.8f);
    }

    @Override
    public void setAdapter(BaseAdapter<?, ?, ?> baseAdapter) {
        mBinding.setBaseAdapter(baseAdapter);
    }
}

