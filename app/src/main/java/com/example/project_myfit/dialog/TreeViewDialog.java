package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_myfit.MainActivityViewModel;
import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.TreeViewRootBinding;
import com.example.project_myfit.main.list.ListFragment;
import com.example.project_myfit.main.list.ListViewModel;
import com.example.project_myfit.searchActivity.SearchFragment;
import com.example.project_myfit.searchActivity.SearchViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.CATEGORY_ADD_DIALOG;
import static com.example.project_myfit.MyFitConstant.FOLDER_ADD_DIALOG;
import static com.example.project_myfit.MyFitConstant.MOVE_DIALOG;
import static com.example.project_myfit.MyFitConstant.PARENT_CATEGORY;
import static com.example.project_myfit.MyFitConstant.TREE_VIEW_STATE;

public class TreeViewDialog extends DialogFragment implements AddCategoryDialog.AddCategoryConfirmListener,
        AddFolderDialog.TreeAddFolderConfirmListener, TreeNode.TreeNodeClickListener,
        TreeHolderCategory.TreeViewCategoryFolderAddListener, TreeHolderFolder.TreeViewFolderFolderAddListener {

    private TreeViewModel mModel;
    private TreeNode mNodeRoot;
    private AndroidTreeView mTreeView;
    private ListViewModel mListViewModel;

    @NotNull
    public static TreeViewDialog getInstance(String parentCategory) {
        TreeViewDialog treeViewDialog = new TreeViewDialog();
        Bundle bundle = new Bundle();
        bundle.putString(PARENT_CATEGORY, parentCategory);
        treeViewDialog.setArguments(bundle);
        return treeViewDialog;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mModel = new ViewModelProvider(this).get(TreeViewModel.class);
        mModel.setParentCategory(getArguments() != null ? getArguments().getString(PARENT_CATEGORY) : null);
        mListViewModel = getTargetFragment() instanceof ListFragment ?
                new ViewModelProvider(getTargetFragment()).get(ListViewModel.class) : null;

        setSelectedItems();

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setView(getDialogView())
                .setTitle(R.string.tree_view_dialog_title)
                .create();

        Window window = dialog.getWindow();
        DialogUtils.setLayout(requireContext(), window);
        DialogUtils.setTextSize(requireContext(), dialog);
        return dialog;
    }

    private void setSelectedItems() {
        AndroidViewModel mActivityModel = getTargetFragment() instanceof SearchFragment ?
                new ViewModelProvider(requireActivity()).get(SearchViewModel.class) :
                new ViewModelProvider(requireActivity()).get(MainActivityViewModel.class);

        if (mActivityModel instanceof MainActivityViewModel)
            mModel.setSelectedItemList(((MainActivityViewModel) mActivityModel).getSelectedFolderList(),
                    ((MainActivityViewModel) mActivityModel).getSelectedSizeList());
        else
            mModel.setSelectedItemList(((SearchViewModel) mActivityModel).getSelectedFolderList(),
                    ((SearchViewModel) mActivityModel).getSelectedSizeList());
    }

    @NotNull
    private View getDialogView() {
        TreeViewRootBinding binding = TreeViewRootBinding.inflate(getLayoutInflater());

        binding.treeViewRoot.addView(getTreeView(), 2);

        String category = " " + mModel.getParentCategory();
        binding.treeViewParentText.setText(category);

        binding.addCategoryLayout.setOnClickListener(v -> showDialog(AddCategoryDialog.getInstance(mModel.getParentCategory()), CATEGORY_ADD_DIALOG));
        return binding.getRoot();
    }

    @NotNull
    private View getTreeView() {
        mTreeView = new AndroidTreeView(requireContext(), getNodeRoot());
        mTreeView.setDefaultAnimation(false);
        mTreeView.setUseAutoToggle(false);
        mTreeView.setDefaultNodeClickListener(this);
        return mTreeView.getView();
    }

    @NotNull
    private TreeNode getNodeRoot() {
        mNodeRoot = TreeNode.root();

        for (Category category : mModel.getCategoryList()) {//카테고리 노드 생성
            TreeNode categoryTreeNode = new TreeNode(new TreeHolderCategory.CategoryTreeHolder(category))
                    .setViewHolder(mModel.getCategoryViewHolder(new TreeHolderCategory(requireContext(), this, mListViewModel)));
            for (Folder folder : mModel.getFolderList()) {//카테고리 노드 속 폴더 노드 생성
                if (category.getId() == folder.getFolderId()) {
                    TreeNode folderTreeNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, mModel.getMargin()))
                            .setViewHolder(mModel.getFolderViewHolder(new TreeHolderFolder(requireContext(), this, mListViewModel)));
                    categoryTreeNode.addChild(folderTreeNode);
                }
            }
            mNodeRoot.addChild(categoryTreeNode);
        }
        return mNodeRoot;
    }

    private void showDialog(@NotNull DialogFragment dialog, String tag) {
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), tag);
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState == null && mListViewModel != null) //listFragment expanding node
            expandingNode();
        else if (savedInstanceState != null) {
            mTreeView.restoreState(savedInstanceState.getString(TREE_VIEW_STATE));
        }
    }

    private void expandingNode() {
        expandCategoryNode();

        if (mListViewModel.getThisFolder() != null) {
            List<TreeNode> topFolderNodeList = new ArrayList<>();
            for (TreeNode categoryTreeNode : mNodeRoot.getChildren())
                if (!categoryTreeNode.getChildren().isEmpty())
                    topFolderNodeList.addAll(categoryTreeNode.getChildren());
            expandingFolderNode(topFolderNodeList);
        }
    }

    private void expandCategoryNode() {
        for (TreeNode categoryTreeNode : mNodeRoot.getChildren()) {
            TreeHolderCategory categoryViewHolder = (TreeHolderCategory) categoryTreeNode.getViewHolder();
            if (mListViewModel.getThisCategory().getId() == categoryViewHolder.getCategoryId() && !categoryTreeNode.getChildren().isEmpty()) {
                mTreeView.expandNode(categoryTreeNode);
                break;
            }
        }
    }

    public void expandingFolderNode(@NotNull List<TreeNode> topFolderNodeList) {
        for (TreeNode folderNode : topFolderNodeList) {
            TreeHolderFolder folderViewHolder = (TreeHolderFolder) folderNode.getViewHolder();
            for (Folder folder : mListViewModel.getFolderHistory())
                if (folderViewHolder.getFolderId() == folder.getId() && !folderNode.getChildren().isEmpty()) {
                    mTreeView.expandNode(folderNode);
                    expandingFolderNode(folderNode.getChildren());
                }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TREE_VIEW_STATE, mTreeView.getSaveState());
        mModel.treeViewDestroy();
    }

    @Override
    public void onClick(@NotNull TreeNode node, Object value) {//노드 클릭
        if (node.getViewHolder() instanceof TreeHolderCategory) {//category node click
            TreeHolderCategory categoryViewHolder = (TreeHolderCategory) node.getViewHolder();
            if (categoryViewHolder.isClickable()) {
                DialogFragment dialogFragment = ItemMoveDialog.getInstance(mModel.getSelectedItemSize(), categoryViewHolder.getCategoryId());
                dialogFragment.setTargetFragment(getTargetFragment(), 0);
                dialogFragment.show(getParentFragmentManager(), MOVE_DIALOG);
            }
        } else if (node.getViewHolder() instanceof TreeHolderFolder) {//folder node click
            TreeHolderFolder folderViewHolder = (TreeHolderFolder) node.getViewHolder();
            if (folderViewHolder.isClickable()) {
                DialogFragment dialogFragment = ItemMoveDialog.getInstance(mModel.getSelectedItemSize(), folderViewHolder.getFolderId());
                dialogFragment.setTargetFragment(getTargetFragment(), 0);
                dialogFragment.show(getParentFragmentManager(), MOVE_DIALOG);
            }
        }
    }

    @Override
    public void addCategoryConfirmClick(@NotNull String categoryName, String parentCategory) {
        TreeNode categoryNode = new TreeNode(new TreeHolderCategory.CategoryTreeHolder(mModel.addCategoryConfirmClick(categoryName)))
                .setViewHolder(mModel.getCategoryViewHolder(new TreeHolderCategory(requireContext(), this, mListViewModel)));
        mTreeView.addNode(mNodeRoot, categoryNode);
    }

    @Override
    public void treeViewCategoryAddFolderClick(TreeNode node, TreeHolderCategory.CategoryTreeHolder value) {
        showDialog(AddFolderDialog.getInstance(true), FOLDER_ADD_DIALOG);
        mModel.setClickedNode(node);
    }

    @Override
    public void treeViewFolderAddFolderClick(TreeNode node, TreeHolderFolder.FolderTreeHolder value) {
        showDialog(AddFolderDialog.getInstance(true), FOLDER_ADD_DIALOG);
        mModel.setClickedNode(node);
    }

    @Override
    public void treeAddFolderConfirmClick(String folderName) {
        if (mModel.getClickedNode().getViewHolder() instanceof TreeHolderCategory) {//category node
            mModel.findCategoryClickedNode(mNodeRoot);

            TreeNode addedFolderNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(mModel.categoryFolderInsert(folderName), mModel.getMargin()))
                    .setViewHolder(mModel.getFolderViewHolder(new TreeHolderFolder(requireContext(), this, mListViewModel)));

            mTreeView.addNode(mModel.getClickedNode(), addedFolderNode);
            mTreeView.expandNode(mModel.getClickedNode());

            mModel.categoryAddFolderConfirmClick();
        } else {
            mModel.findFolderClickedNode(mNodeRoot);

            TreeNode addedFolderNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(mModel.folderFolderInsert(folderName), mModel.getMargin2()))
                    .setViewHolder(mModel.getFolderViewHolder(new TreeHolderFolder(requireContext(), this, mListViewModel)));

            mTreeView.addNode(mModel.getClickedNode(), addedFolderNode);
            mTreeView.expandNode(mModel.getClickedNode());

            mModel.folderAddFolderConfirmClick();
        }
    }
}