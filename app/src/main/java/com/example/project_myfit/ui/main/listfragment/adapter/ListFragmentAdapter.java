package com.example.project_myfit.ui.main.listfragment.adapter;

import com.chad.library.adapter.base.BaseNodeAdapter;
import com.chad.library.adapter.base.entity.node.BaseNode;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;
import com.example.project_myfit.ui.main.listfragment.database.Size;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListFragmentAdapter extends BaseNodeAdapter {

    public ListFragmentAdapter() {
        super();
        addNodeProvider(new ProviderFolder());
        addNodeProvider(new ProviderSize());
    }

    @Override
    protected int getItemType(@NotNull List<? extends BaseNode> list, int i) {
        BaseNode node = list.get(i);
        if (node instanceof ListFolder) {
            return 1;
        } else if (node instanceof Size) {
            return 2;
        }
        return -1;
    }
}
