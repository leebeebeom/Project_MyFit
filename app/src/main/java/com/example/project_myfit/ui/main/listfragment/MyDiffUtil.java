package com.example.project_myfit.ui.main.listfragment;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.example.project_myfit.ui.main.database.ParentCategory;

public class MyDiffUtil extends DiffUtil.ItemCallback<BaseNode> {
    @Override
    public boolean areItemsTheSame(@NonNull BaseNode oldItem, @NonNull BaseNode newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull BaseNode oldItem, @NonNull BaseNode newItem) {
        return oldItem.getChildNode().equals(newItem.getChildNode());
    }
}
