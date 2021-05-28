package com.example.myfit.util.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.databinding.ItemHeaderBinding;
import com.example.myfit.util.CommonUtil;

import org.jetbrains.annotations.NotNull;

public class HeaderVH extends RecyclerView.ViewHolder {
    private final ItemHeaderBinding mBinding;

    public HeaderVH(@NotNull ItemHeaderBinding binding) {
        super(binding.getRoot());
        this.mBinding = binding;
    }

    public void bind(int parentIndex) {
        String parentCategory = CommonUtil.getParentCategory(parentIndex);
        mBinding.setParentCategory(parentCategory);
    }
}
