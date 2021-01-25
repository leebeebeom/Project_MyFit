package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
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
    private List<TreeNode> mCategoryTreeNodeList;

    public interface TreeViewAddClick {
        void treeViewCategoryAddClick(TreeNode node, TreeHolderCategory.CategoryTreeHolder value);

        void treeViewFolderAddClick(TreeNode node, TreeHolderFolder.FolderTreeHolder value);
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (TreeNode.TreeNodeClickListener) getTargetFragment();
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

        View view = mTreeView.getView();
        int padding = (int) requireContext().getResources().getDimension(R.dimen._8sdp);
        view.setPadding(0, padding, 0, 0);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setView(view)
                .setTitle("이동할 위치를 선택하세요.")
                .show();

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(R.drawable.tree_view_dialog_background);
        Drawable drawable = ContextCompat.getDrawable(requireContext(), R.drawable.tree_view_dialog_background);

        int margin = (int) requireContext().getResources().getDimension(R.dimen._20sdp);
        InsetDrawable inset = new InsetDrawable(drawable, margin);
        window.setBackgroundDrawable(inset);

        float titleSize = getResources().getDimension(R.dimen._5sdp);
        int titleId = getResources().getIdentifier("alertTitle", "id", requireContext().getPackageName());
        TextView title = dialog.findViewById(titleId);
        if (title != null) title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, titleSize);

        return dialog;
    }

    private AndroidTreeView getTreeView() {
        List<Folder> folderList = mModel.getAllFolder();
        mCategoryTreeNodeList = new ArrayList<>();
        for (Category category : mModel.getCategoryList()) {
            TreeNode categoryTreeNode = new TreeNode(new TreeHolderCategory.CategoryTreeHolder(category, (TreeViewAddClick) mListener))
                    .setViewHolder(new TreeHolderCategory(requireContext()));
            for (Folder folder : folderList) {
                if (category.getId() == folder.getFolderId()) {
                    TreeNode folderTreeNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, folderList,
                            40, (TreeViewAddClick) mListener, mModel.getSelectedFolder())).setViewHolder(new TreeHolderFolder(requireContext()));
                    categoryTreeNode.addChild(folderTreeNode);
                }
            }
            if (mModel.getCategory().getId() == category.getId() && categoryTreeNode.getChildren().size() != 0)
                categoryTreeNode.setExpanded(true);
            mCategoryTreeNodeList.add(categoryTreeNode);
        }

        TreeNode root = TreeNode.root();
        root.addChildren(mCategoryTreeNodeList);
        AndroidTreeView treeView = new AndroidTreeView(requireContext(), root);
        treeView.setDefaultAnimation(false);
        treeView.setUseAutoToggle(false);
        treeView.setDefaultNodeClickListener(mListener);
        mModel.setRootTreeNode(root);

        return treeView;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<TreeNode> topFolderList = new ArrayList<>();
        for (TreeNode categoryNode : mCategoryTreeNodeList) {
            if (categoryNode.getChildren().size() != 0)
                topFolderList.addAll(categoryNode.getChildren());
        }
        expandingNode(topFolderList);
    }

    private void expandingNode(List<TreeNode> topFolderList) {
        if (mModel.getFolderHistory() != null) {
            for (TreeNode folderNode : topFolderList) {
                if (mModel.getFolderHistory().get(mModel.getFolderHistory().size() - 1).getId() == ((TreeHolderFolder.FolderTreeHolder) folderNode.getValue()).folder.getId())
                    ((TreeHolderFolder) folderNode.getViewHolder()).getBinding().currentPosition.setVisibility(View.VISIBLE);
                for (Folder folder : mModel.getFolderHistory())
                    if (((TreeHolderFolder.FolderTreeHolder) folderNode.getValue()).folder.getId() == folder.getId() && folderNode.getChildren().size() != 0)
                        mTreeView.expandNode(folderNode);
                if (folderNode.getChildren().size() != 0) expandingNode(folderNode.getChildren());
            }
        } else {
            for (TreeNode categoryNode : mCategoryTreeNodeList)
                if (mModel.getCategory().getId() == ((TreeHolderCategory.CategoryTreeHolder) categoryNode.getValue()).category.getId())
                    ((TreeHolderCategory) categoryNode.getViewHolder()).getBinding().currentPosition.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("state", mTreeView.getSaveState());
    }

}
