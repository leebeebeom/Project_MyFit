package com.example.project_myfit.ui.main.nodedapter;

import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.example.project_myfit.R;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.example.project_myfit.ui.main.database.ParentCategory;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainFragmentAdapter extends BaseNodeAdapter {

    public MainFragmentAdapter() {
        super();
        addChildClickViewIds(R.id.add_icon);
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

    public static final int EXPAND_COLLAPSE_PAYLOAD = 110;

}
