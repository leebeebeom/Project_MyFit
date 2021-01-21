package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderCategory;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderFolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TreeViewDialog extends DialogFragment {
    private AndroidTreeView mTreeView;
    private MainActivityViewModel mModel;
    private TreeNode.TreeNodeClickListener mListener;

    public interface TreeViewAddClick {
        void treeViewCategoryAddClick(TreeNode node, TreeHolderCategory.CategoryTreeHolder value);

        void treeViewFolderAddClick(TreeNode node, TreeHolderFolder.FolderTreeHolder value);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (TreeNode.TreeNodeClickListener) getTargetFragment();
    }

    public TreeViewDialog() {
    }

    public static TreeViewDialog getInstance() {
        return new TreeViewDialog();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mModel = new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);
        mTreeView = getTreeView();
        if (savedInstanceState != null) {
            String state = savedInstanceState.getString("state");
            if (!TextUtils.isEmpty(state)) {
                mTreeView.restoreState(savedInstanceState.getString("state"));
            }
        }

        Dialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setView(mTreeView.getView())
                .create();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(R.drawable.tree_view_dialog_background);
        return dialog;
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("state", mTreeView.getSaveState());
    }

    private AndroidTreeView getTreeView() {
        List<Folder> folderList = mModel.getAllFolder();
        List<TreeNode> categoryTreeNodeList = new ArrayList<>();
        for (Category category : mModel.getCategoryList()) {
            TreeNode categoryTreeNode = new TreeNode(new TreeHolderCategory.CategoryTreeHolder(category, (TreeViewAddClick) mListener))
                    .setViewHolder(new TreeHolderCategory(requireContext()));
            if (mModel.getCategory().getId() == category.getId())
                categoryTreeNode.setExpanded(true);
            for (Folder folder : folderList) {
                if (category.getId() == folder.getFolderId()) {
                    TreeNode folderTreeNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, folderList,
                            40, (TreeViewAddClick) mListener, mModel.getSelectedFolder())).setViewHolder(new TreeHolderFolder(requireContext()));
                    categoryTreeNode.addChild(folderTreeNode);
                }
            }
            categoryTreeNodeList.add(categoryTreeNode);
        }
        TreeNode root = TreeNode.root();
        root.addChildren(categoryTreeNodeList);
        AndroidTreeView treeView = new AndroidTreeView(requireContext(), root);
        treeView.setDefaultAnimation(false);
        treeView.setUseAutoToggle(false);
        treeView.setDefaultNodeClickListener(mListener);
        mModel.setRootTreeNode(root);
        return treeView;
    }


}
