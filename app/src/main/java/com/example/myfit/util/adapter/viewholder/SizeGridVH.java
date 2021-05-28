package com.example.myfit.util.adapter.viewholder;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.myfit.data.model.size.SizeTuple;
import com.example.myfit.databinding.ItemSizeGridBinding;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class SizeGridVH extends BaseVH.BaseSizeVH {
    private final ItemSizeGridBinding mBinding;

    public SizeGridVH(@NotNull ItemSizeGridBinding binding, SizeVHListener listener, Set<SizeTuple> selectedItems) {
        super(binding, listener, selectedItems);
        this.mBinding = binding;

        binding.cbFavorite.setOnClickListener(v -> listener.sizeFavoriteClick(getTuple()));
    }

    @Override
    protected void bind(SizeTuple tuple) {
        mBinding.setTuple(tuple);
    }

    @Override
    public MaterialCheckBox getCheckBox() {
        return mBinding.cb;
    }

    @Override
    public AppCompatImageView getDragHandleIcon() {
        return mBinding.iconDragHandle;
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
    public MaterialCardView getCardView() {
        return mBinding.cardView;
    }

    public ItemSizeGridBinding getBinding() {
        return mBinding;
    }

    @Override
    public MaterialCheckBox getFavorite() {
        return mBinding.cbFavorite;
    }
}

