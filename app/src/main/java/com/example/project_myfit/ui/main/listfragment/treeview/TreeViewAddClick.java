package com.example.project_myfit.ui.main.listfragment.treeview;

import com.unnamed.b.atv.model.TreeNode;

public interface TreeViewAddClick {
    void OnCategoryAddClick(TreeNode node, TreeHolderCategory.IconTreeHolder value);

    void OnFolderAddClick(TreeNode node, TreeHolderFolder.IconTreeHolder value);
}
