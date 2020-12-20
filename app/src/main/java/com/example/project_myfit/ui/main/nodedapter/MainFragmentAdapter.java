package com.example.project_myfit.ui.main.nodedapter;

import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.project_myfit.R;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.example.project_myfit.ui.main.database.ParentCategory;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class MainFragmentAdapter extends BaseNodeAdapter {

    public MainFragmentAdapter() {
        super();
        //Click Listener
        addChildClickViewIds(R.id.add_icon, R.id.delete_icon, R.id.edit_icon);
        addNodeProvider(new ProviderParent());
        addNodeProvider(new ProviderChild());
    }

    @Override
    protected int getItemType(@NotNull List<? extends BaseNode> list, int i) {
        BaseNode node = list.get(i);
        if (node instanceof ParentCategory) {
            return 1;
        } else if (node instanceof ChildCategory) {
            return 2;
        }
        return -1;
    }

}
