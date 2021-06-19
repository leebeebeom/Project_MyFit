package com.leebeebeom.closetnote.util.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.leebeebeom.closetnote.databinding.ItemVhHeaderBinding;
import com.leebeebeom.closetnote.util.CommonUtil;

import org.jetbrains.annotations.NotNull;

public class HeaderVH extends RecyclerView.ViewHolder {
    private final ItemVhHeaderBinding mBinding;

    public HeaderVH(@NotNull ItemVhHeaderBinding binding) {
        super(binding.getRoot());
        this.mBinding = binding;
    }

    public void bind(int parentIndex) {
        String parentCategory = CommonUtil.getParentCategory(parentIndex);
        mBinding.setParentCategory(parentCategory);
    }
}
