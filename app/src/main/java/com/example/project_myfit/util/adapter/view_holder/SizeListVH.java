package com.example.project_myfit.util.adapter.view_holder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemSizeListBinding;

import org.jetbrains.annotations.NotNull;

public class SizeListVH extends RecyclerView.ViewHolder {
    private final ItemSizeListBinding mBinding;
    private Size mSize;

    public SizeListVH(@NotNull ItemSizeListBinding binding, SizeVHListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;

        itemView.setOnClickListener(v -> listener.onSizeItemViewClick(mSize, mBinding.cbItemSizeList));

        itemView.setOnLongClickListener(v -> {
            listener.onSizeItemViewLongClick(getLayoutPosition());
            return false;
        });

        mBinding.cbItemSizeListFavorite.setOnClickListener(v -> listener.onSizeFavoriteClick(mSize));
    }

    public void setSize(Size size) {
        mSize = size;
        mBinding.setSize(size);
    }

    public ItemSizeListBinding getBinding() {
        return mBinding;
    }
}

