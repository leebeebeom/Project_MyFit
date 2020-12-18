package com.example.project_myfit.ui.main.nodedapter;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.project_myfit.R;
import com.example.project_myfit.ui.main.database.ChildCategory;

import org.jetbrains.annotations.NotNull;

public class ProviderChild extends BaseNodeProvider {
    @Override
    public int getItemViewType() {
        return 2;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_main_fragment_child;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, BaseNode baseNode) {
        ChildCategory childCategory = (ChildCategory) baseNode;
        baseViewHolder.setText(R.id.child_text, childCategory.getChildCategory());
    }
}
