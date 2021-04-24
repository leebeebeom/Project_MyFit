package com.example.project_myfit.util.adapter.view_holder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemListRecyclerGridBinding;

import org.jetbrains.annotations.NotNull;

public class SizeGridVH extends RecyclerView.ViewHolder {
    private final ItemListRecyclerGridBinding mBinding;
    private Size mSize;

    public SizeGridVH(@NotNull ItemListRecyclerGridBinding binding, SizeVHListener listener) {
        super(binding.getRoot());
        this.mBinding = binding;

        itemView.setOnClickListener(v -> listener.onSizeItemViewClick(mSize, mBinding.itemListGridCheckBox));

        itemView.setOnLongClickListener(v -> {
            listener.onSizeItemViewLongClick(getLayoutPosition());
            return false;
        });

        mBinding.itemListGridFavoriteCheckBox.setOnClickListener(v -> listener.onSizeFavoriteClick(mSize));
    }

    public void setSize(Size size) {
        this.mSize = size;
        mBinding.setSize(size);
    }

    public ItemListRecyclerGridBinding getBinding() {
        return mBinding;
    }
}

