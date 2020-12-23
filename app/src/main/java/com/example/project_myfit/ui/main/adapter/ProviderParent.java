package com.example.project_myfit.ui.main.adapter;

import android.view.View;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.project_myfit.R;
import com.example.project_myfit.ui.main.database.ParentCategory;

import org.jetbrains.annotations.NotNull;

public class ProviderParent extends BaseNodeProvider {
    @Override
    public int getItemViewType() {
        return 1;
    }

    @Override
    public int getLayoutId() {
        //Root
        return R.layout.item_main_fragment_parent;
    }

    @Override
    public void convert(@NotNull BaseViewHolder helper, BaseNode data) {
        //Bind
        ParentCategory parentCategory = (ParentCategory) data;
        helper.setText(R.id.parent_text, parentCategory.getParentCategory());
    }

    @Override
    public void onClick(@NotNull BaseViewHolder helper, @NotNull View view, BaseNode data, int position) {
        //Click
        getAdapter().expandOrCollapse(position, true, true);
    }
}
