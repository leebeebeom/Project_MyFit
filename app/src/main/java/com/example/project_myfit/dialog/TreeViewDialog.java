package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_myfit.R;
import com.example.project_myfit.databinding.TreeViewRootBinding;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.ListViewModel;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderCategory;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderFolder;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.TREE_VIEW_STATE;

public class TreeViewDialog extends DialogFragment implements AddCategoryDialog.AddCategoryConfirmClick {
    private TreeNode mNodeRoot;
    private ListViewModel mModel;
    private AndroidTreeView mTreeView;
    private TreeNode.TreeNodeClickListener mListener;
    private List<TreeNode> mCategoryTreeNodeList;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (TreeNode.TreeNodeClickListener) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        if (getTargetFragment() != null)
            mModel = new ViewModelProvider(getTargetFragment()).get(ListViewModel.class);
        mTreeView = getTreeView();

        View view = mTreeView.getView();
        int padding = (int) requireContext().getResources().getDimension(R.dimen._8sdp);
        view.setPadding(0, padding, 0, 0);

        //tree view root binding for addCategory
        TreeViewRootBinding binding = TreeViewRootBinding.inflate(getLayoutInflater());
        binding.treeViewRoot.addView(view, 0);
        binding.addCategoryLayout.setOnClickListener(v -> {
            DialogFragment dialog = new AddCategoryDialog();
            dialog.setTargetFragment(this, 0);
            dialog.show(getParentFragmentManager(), null);
        });

        //create dialog
        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setView(binding.getRoot())
                .setTitle(R.string.tree_view_dialog_title)
                .create();

        Window window = dialog.getWindow();
        DialogUtils.setLayout(requireContext(), window);
        DialogUtils.setTextSize(requireContext(), dialog);
        return dialog;
    }

    @NotNull
    private AndroidTreeView getTreeView() {
        int margin = (int) getResources().getDimension(R.dimen._12sdp);
        mCategoryTreeNodeList = new ArrayList<>();
        for (Category category : mModel.getCategoryListByParent()) {
            TreeNode categoryTreeNode = new TreeNode(new TreeHolderCategory.CategoryTreeHolder(category, (TreeViewAddClick) mListener, mModel.getSelectedItemSize()))
                    .setViewHolder(new TreeHolderCategory(requireContext()));
            for (Folder folder : mModel.getAllFolder()) {
                if (category.getId() == folder.getFolderId()) {
                    TreeNode folderTreeNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, margin, (TreeViewAddClick) mListener, mModel)).setViewHolder(new TreeHolderFolder(requireContext()));
                    categoryTreeNode.addChild(folderTreeNode);
                }
            }
            mCategoryTreeNodeList.add(categoryTreeNode);
        }

        mNodeRoot = TreeNode.root();
        mNodeRoot.addChildren(mCategoryTreeNodeList);
        AndroidTreeView treeView = new AndroidTreeView(requireContext(), mNodeRoot);
        treeView.setDefaultAnimation(false);
        treeView.setUseAutoToggle(false);
        treeView.setDefaultNodeClickListener(mListener);
        mModel.setTreeNodeRoot(mNodeRoot);

        return treeView;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<TreeNode> topFolderList = new ArrayList<>();
        for (TreeNode categoryNode : mCategoryTreeNodeList)
            if (categoryNode.getChildren().size() != 0)
                topFolderList.addAll(categoryNode.getChildren());

        expandingNode(topFolderList);

        if (savedInstanceState != null)
            mTreeView.restoreState(savedInstanceState.getString(TREE_VIEW_STATE));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mModel.setTreeNodeRoot(null);
    }

    private void expandingNode(List<TreeNode> topFolderList) {
        for (TreeNode categoryTreeNode : mCategoryTreeNodeList) {
            TreeHolderCategory.CategoryTreeHolder holder = (TreeHolderCategory.CategoryTreeHolder) categoryTreeNode.getValue();
            if (mModel.getThisCategory().getId() == holder.category.getId() && categoryTreeNode.getChildren().size() != 0)
                mTreeView.expandNode(categoryTreeNode);
        }
        if (mModel.getThisFolder() != null) {//if current position is not category
            for (TreeNode folderNode : topFolderList) {
                //visible current position text
                if (mModel.getFolderHistory().get(mModel.getFolderHistory().size() - 1).getId() == ((TreeHolderFolder.FolderTreeHolder) folderNode.getValue()).getFolder().getId())
                    ((TreeHolderFolder) folderNode.getViewHolder()).getBinding().currentPosition.setVisibility(View.VISIBLE);
                for (Folder folder : mModel.getFolderHistory())
                    if (((TreeHolderFolder.FolderTreeHolder) folderNode.getValue()).getFolder().getId() == folder.getId() && folderNode.getChildren().size() != 0)
                        mTreeView.expandNode(folderNode);
                if (folderNode.getChildren().size() != 0) expandingNode(folderNode.getChildren());
            }
        } else {//current position is category
            for (TreeNode categoryNode : mCategoryTreeNodeList)
                if (mModel.getThisCategory().getId() == ((TreeHolderCategory.CategoryTreeHolder) categoryNode.getValue()).category.getId())
                    ((TreeHolderCategory) categoryNode.getViewHolder()).getBinding().currentPosition.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void addCategoryConfirmClick(String categoryName) {
        int orderNumber = mModel.getRepository().getCategoryLargestOrder() + 1;
        Category category = new Category(categoryName.trim(), mModel.getThisCategory().getParentCategory(), orderNumber);
        Category addedCategory = mModel.getRepository().treeViewAddCategory(category);

        TreeNode categoryNode = new TreeNode(new TreeHolderCategory.CategoryTreeHolder(addedCategory, (TreeViewAddClick) mListener, mModel.getSelectedItemSize()))
                .setViewHolder(new TreeHolderCategory(requireContext()));
        mTreeView.addNode(mNodeRoot, categoryNode);
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TREE_VIEW_STATE, mTreeView.getSaveState());
    }

    public interface TreeViewAddClick {
        void treeViewCategoryAddClick(TreeNode node, TreeHolderCategory.CategoryTreeHolder value);

        void treeViewFolderAddClick(TreeNode node, TreeHolderFolder.FolderTreeHolder value);
    }
}
