package com.example.project_myfit.ui.main.listfragment.adapter;

import android.view.View;

import com.chad.library.adapter.base.entity.node.BaseNode;
import com.chad.library.adapter.base.provider.BaseNodeProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.example.project_myfit.R;
import com.example.project_myfit.ui.main.listfragment.database.ListFolder;

import org.jetbrains.annotations.NotNull;

public class ProviderFolder extends BaseNodeProvider {
    ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    @Override
    public int getItemViewType() {
        return 1;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_list_fragment_folder;
    }

    @Override
    public void convert(@NotNull BaseViewHolder baseViewHolder, BaseNode baseNode) {
        ListFolder listFolder = (ListFolder) baseNode;
        baseViewHolder.setText(R.id.list_folder_name, listFolder.getFolderName());
        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(baseViewHolder.getView(R.id.swipeLayout), String.valueOf(((ListFolder) baseNode).getId()));
    }

    @Override
    public void onClick(@NotNull BaseViewHolder helper, @NotNull View view, BaseNode data, int position) {
        getAdapter().expandOrCollapse(position, true, true);
    }
}
