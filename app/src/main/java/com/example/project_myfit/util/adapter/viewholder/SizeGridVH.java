package com.example.project_myfit.util.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemSizeGridBinding;

import org.jetbrains.annotations.NotNull;

public class SizeGridVH extends RecyclerView.ViewHolder {
    private final ItemSizeGridBinding mBinding;
    private Size mSize;

    public SizeGridVH(@NotNull ItemSizeGridBinding binding, SizeVHListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;

        itemView.setOnClickListener(v -> listener.onSizeItemViewClick(mSize, mBinding.cbItemSizeGrid));

        itemView.setOnLongClickListener(v -> {
            listener.onSizeItemViewLongClick(getLayoutPosition());
            return false;
        });

        mBinding.cbItemSizeGridFavorite.setOnClickListener(v -> listener.onSizeFavoriteClick(mSize));
    }

    public void setSize(Size size) {
        this.mSize = size;
        mBinding.setSize(size);
    }

    public ItemSizeGridBinding getBinding() {
        return mBinding;
    }
}

