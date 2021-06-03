package com.example.myfit.util.adapter.viewholder;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.appcompat.widget.AppCompatImageView;

import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.databinding.ItemFolderGridBinding;
import com.example.myfit.util.adapter.BaseAdapter;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;

import org.jetbrains.annotations.NotNull;

import java.util.Set;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class FolderGridVH extends BaseVH<FolderTuple, BaseVHListener> {
    @Getter
    private final ItemFolderGridBinding mBinding;

    public FolderGridVH(@NotNull ItemFolderGridBinding binding, BaseVHListener listener, Set<FolderTuple> selectedItems) {
        super(binding, listener, selectedItems);
        this.mBinding = binding;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void bind() {
        mBinding.setFolderGridVH(this);
    }

    @Override
    public MaterialCheckBox getCheckBox() {
        return mBinding.cb;
    }

    @Override
    protected void setDraggingView() {
        setItemViewTranslationZ(itemView, 10);
        mBinding.layoutContentsSize.setVisibility(View.INVISIBLE);
        mBinding.tvFolderName.setAlpha(0.5f);
        mBinding.cb.setAlpha(0.5f);
    }

    @Override
    protected void setDropView() {
        setItemViewTranslationZ(itemView, 0);
        mBinding.layoutContentsSize.setVisibility(View.VISIBLE);
        mBinding.tvFolderName.setAlpha(1);
        mBinding.cb.setAlpha(0.8f);
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
