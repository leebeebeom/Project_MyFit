package com.example.project_myfit.ui.main.listfragment.treeview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemTreeCategoryBinding;
import com.example.project_myfit.ui.main.database.Category;
import com.unnamed.b.atv.model.TreeNode;

public class TreeHolderCategory extends TreeNode.BaseNodeViewHolder<TreeHolderCategory.IconTreeHolder> {

    private ItemTreeCategoryBinding mBinding;

    public TreeHolderCategory(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, IconTreeHolder value) {
        mBinding = ItemTreeCategoryBinding.inflate(LayoutInflater.from(context));
        mBinding.text.setText(value.category.getCategory());
        if (node.getChildren().size() != 0) {
            setClickListener();
        } else mBinding.arrowIcon.setVisibility(View.INVISIBLE);
        mBinding.addIcon.setOnClickListener(v -> {
            value.listener.OnCategoryAddClick(node, value);
        });

        return mBinding.getRoot();
    }

    public void setClickListener() {
        mBinding.arrowIcon.setVisibility(View.VISIBLE);
        mBinding.iconLayout.setOnClickListener(v -> {
            getTreeView().toggleNode(mNode);
            if (mNode.isExpanded()) {
                mBinding.arrowIcon.setImageResource(R.drawable.icon_triangle_down);
                mBinding.folderIcon.setImageResource(R.drawable.icon_folder_open);
            } else {
                mBinding.arrowIcon.setImageResource(R.drawable.icon_triangle_right);
                mBinding.folderIcon.setImageResource(R.drawable.icon_folder);
            }
        });
    }

    public static class IconTreeHolder {
        public Category category;
        public TreeViewAddClick listener;

        public IconTreeHolder(Category category, TreeViewAddClick listener) {
            this.category = category;
            this.listener = listener;
        }
    }
}
