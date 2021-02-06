package com.example.project_myfit.ui.main.listfragment.treeview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.ItemTreeCategoryBinding;
import com.example.project_myfit.dialog.TreeViewDialog;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.unnamed.b.atv.model.TreeNode;

import java.util.List;

public class TreeHolderCategory extends TreeNode.BaseNodeViewHolder<TreeHolderCategory.CategoryTreeHolder> {

    private final ItemTreeCategoryBinding mBinding;

    public TreeHolderCategory(Context context) {
        super(context);
        mBinding = ItemTreeCategoryBinding.inflate(LayoutInflater.from(context));
    }

    @Override
    public View createNodeView(TreeNode node, CategoryTreeHolder value) {
        mBinding.setCategory(value.category);

        //if selected item position is this node
        if (value.selectedSizeList.size() != 0) {
            Size size = value.selectedSizeList.get(0);
            if (size.getFolderId() == value.category.getId()) {
                mBinding.iconLayout.setAlpha(0.5f);
                mBinding.text.setAlpha(0.5f);
            }
        }

        if (node.getChildren().size() != 0)
            mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(node));
        else mBinding.arrowIcon.setVisibility(View.INVISIBLE);

        mBinding.addIcon.setOnClickListener(v -> value.listener.treeViewCategoryAddClick(node, value));

        return mBinding.getRoot();
    }

    public void setIconClickable() {
        mBinding.arrowIcon.setVisibility(View.VISIBLE);
        mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(mNode));
    }

    public ItemTreeCategoryBinding getBinding() {
        return mBinding;
    }

    @Override
    public void toggle(boolean active) {
        mBinding.arrowIcon.setImageResource(active ? R.drawable.icon_triangle_down : R.drawable.icon_triangle_right);
        mBinding.folderIcon.setImageResource(active ? R.drawable.icon_folder_open : R.drawable.icon_folder);
    }

    public static class CategoryTreeHolder {
        public Category category;
        public TreeViewDialog.TreeViewAddClick listener;
        public List<Size> selectedSizeList;

        public CategoryTreeHolder(Category category, TreeViewDialog.TreeViewAddClick listener, List<Size> selectedSizeList) {
            this.category = category;
            this.listener = listener;
            this.selectedSizeList = selectedSizeList;
        }
    }
}
