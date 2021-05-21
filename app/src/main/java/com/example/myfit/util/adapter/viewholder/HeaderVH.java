package com.example.myfit.util.adapter.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myfit.databinding.ItemHeaderBinding;
import com.example.myfit.util.constant.ParentCategory;

import org.jetbrains.annotations.NotNull;

public class HeaderVH extends RecyclerView.ViewHolder {
    private final ItemHeaderBinding binding;

    public HeaderVH(@NotNull ItemHeaderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(int parentIndex) {
        binding.setParentCategory(getParentCategory(parentIndex));
    }

    private String getParentCategory(int parentIndex) {
        switch (parentIndex) {
            case 0:
                return ParentCategory.TOP.name();
            case 1:
                return ParentCategory.BOTTOM.name();
            case 2:
                return ParentCategory.OUTER.name();
            default:
                return ParentCategory.ETC.name();
        }
    }
}
