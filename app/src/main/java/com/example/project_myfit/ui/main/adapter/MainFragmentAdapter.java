package com.example.project_myfit.ui.main.adapter;

import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.example.project_myfit.R;
import com.example.project_myfit.ui.main.DragCallBack;
import com.example.project_myfit.ui.main.database.ChildCategory;
import com.example.project_myfit.ui.main.database.ParentCategory;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainFragmentAdapter extends BaseNodeAdapter {
    public MainFragmentAdapter(DragCallBack.DragListener listener) {
        super();
        //Click Listener
        addChildClickViewIds(R.id.add_icon, R.id.delete_icon, R.id.edit_icon, R.id.item_child_root);
        addNodeProvider(new ProviderParent());
        addNodeProvider(new ProviderChild(listener));
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
