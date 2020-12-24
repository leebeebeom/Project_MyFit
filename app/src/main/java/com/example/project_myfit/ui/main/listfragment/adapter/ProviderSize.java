package com.example.project_myfit.ui.main.listfragment.adapter;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.project_myfit.R;
import com.example.project_myfit.ui.main.listfragment.database.Size;

import org.jetbrains.annotations.NotNull;

public class ProviderSize extends BaseNodeProvider {

    @Override
    public int getItemViewType() {
        return 2;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_list_fragment;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, BaseNode baseNode) {
        Size size = (Size) baseNode;
        baseViewHolder.setText(R.id.list_brand, size.getBrand());
        baseViewHolder.setText(R.id.list_name, size.getName());
    }
}
