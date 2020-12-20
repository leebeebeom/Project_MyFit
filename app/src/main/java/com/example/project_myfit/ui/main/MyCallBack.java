package com.example.project_myfit.ui.main;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.chad.library.adapter.base.entity.node.BaseNode;

public class MyCallBack extends DiffUtil.ItemCallback<BaseNode> {
    @Override
    public boolean areItemsTheSame(@NonNull BaseNode oldItem, @NonNull BaseNode newItem) {
        return false;
    }

    @Override
    public boolean areContentsTheSame(@NonNull BaseNode oldItem, @NonNull BaseNode newItem) {
        return false;
    }
}
