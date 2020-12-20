package com.example.project_myfit.ui.main.nodedapter;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.project_myfit.R;
import com.example.project_myfit.ui.main.database.ChildCategory;

import org.jetbrains.annotations.NotNull;

public class ProviderChild extends BaseNodeProvider {
    ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    @Override
    public int getItemViewType() {
        return 2;
    }

    @Override
    public int getLayoutId() {
        //Root
        return R.layout.item_main_fragment_child;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, BaseNode baseNode) {
        //Bind
        ChildCategory childCategory = (ChildCategory) baseNode;
        baseViewHolder.setText(R.id.child_text, childCategory.getChildCategory());
        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(baseViewHolder.getView(R.id.swipeLayout), ((ChildCategory) baseNode).getChildCategory());
        viewBinderHelper.closeLayout(String.valueOf(childCategory.getChildCategory()));
    }
}
