package com.example.project_myfit.ui.main.listfragment.treeview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemTreeCategoryBinding;
import com.example.project_myfit.dialog.TreeViewDialog;
import com.example.project_myfit.ui.main.database.Category;
import com.unnamed.b.atv.model.TreeNode;

public class TreeHolderCategory extends TreeNode.BaseNodeViewHolder<TreeHolderCategory.CategoryTreeHolder> {

    private ItemTreeCategoryBinding mBinding;

    public TreeHolderCategory(Context context) {
        super(context);
    }

    @Override
    public View createNodeView(TreeNode node, CategoryTreeHolder value) {
        mBinding = ItemTreeCategoryBinding.inflate(LayoutInflater.from(context));
        mBinding.text.setText(value.category.getCategory());

        if (node.isExpanded()) {
            mBinding.arrowIcon.setImageResource(R.drawable.icon_triangle_down);
            mBinding.folderIcon.setImageResource(R.drawable.icon_folder_open);
        }

        if (node.getChildren().size() != 0) {
            mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(node));
        } else mBinding.arrowIcon.setVisibility(View.INVISIBLE);
        mBinding.addIcon.setOnClickListener(v -> value.listener.treeViewCategoryAddClick(node, value));

        return mBinding.getRoot();
    }

    public void setIconClickable() {
        mBinding.arrowIcon.setVisibility(View.VISIBLE);
        mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(mNode));
    }

    @Override
    public void toggle(boolean active) {
        mBinding.arrowIcon.setImageResource(active ? R.drawable.icon_triangle_down : R.drawable.icon_triangle_right);
        mBinding.folderIcon.setImageResource(active ? R.drawable.icon_folder_open : R.drawable.icon_folder);
    }

    public static class CategoryTreeHolder {
        public Category category;
        public TreeViewDialog.TreeViewAddClick listener;

        public CategoryTreeHolder(Category category, TreeViewDialog.TreeViewAddClick listener) {
            this.category = category;
            this.listener = listener;
        }
    }
}
