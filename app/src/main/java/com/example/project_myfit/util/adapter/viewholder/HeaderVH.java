package com.example.project_myfit.util.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.project_myfit.databinding.ItemHeaderBinding;

import org.jetbrains.annotations.NotNull;

public class HeaderVH extends RecyclerView.ViewHolder {
    private final ItemHeaderBinding mBinding;

    public HeaderVH(@NotNull ItemHeaderBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
    }

    public void setParentCategory(String parentCategory) {
        mBinding.setParentCategory(parentCategory);
    }

    public ItemHeaderBinding getBinding() {
        return mBinding;
    }
}
