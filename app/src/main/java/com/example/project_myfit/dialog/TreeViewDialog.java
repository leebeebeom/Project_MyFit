package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.LayoutDialogTreeBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ADD_CONFIRM_CLICK;
import static com.example.project_myfit.util.MyFitConstant.CATEGORY;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.TREE_VIEW_STATE;

public class TreeViewDialog extends DialogFragment implements TreeNode.TreeNodeClickListener,
        TreeHolderCategory.TreeViewCategoryFolderAddListener, TreeHolderFolder.TreeViewFolderFolderAddListener {
    private TreeViewModel mModel;
    private TreeNode mNodeRoot;
    private AndroidTreeView mTreeView;
    private DialogUtils mDialogUtils;
    private Category mThisCategory;
    private Folder mThisFolder;
    private boolean mCategoryAdded;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(TreeViewModel.class);
        mModel.setParentCategory(TreeViewDialogArgs.fromBundle(getArguments()).getParentCategory());
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mDialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.nav_graph_main);
        mThisCategory = mDialogUtils.getDialogViewModel().getCategory(TreeViewDialogArgs.fromBundle(getArguments()).getThisCategoryId());
        mThisFolder = mDialogUtils.getDialogViewModel().getFolder(TreeViewDialogArgs.fromBundle(getArguments()).getThisFolderId());
        mModel.setSelectedItemList(mDialogUtils.getDialogViewModel().getSelectedFolderList(), mDialogUtils.getDialogViewModel().getSelectedSizeList());

        AlertDialog alertDialog = new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialogStyle)
                .setView(getDialogView())
                .setTitle(R.string.tree_title)
                .create();

        Window window = alertDialog.getWindow();
        mDialogUtils.setLayout(window);
        mDialogUtils.setTextSize(alertDialog);
        return alertDialog;
    }

    @NotNull
    private View getDialogView() {
        LayoutDialogTreeBinding binding = LayoutDialogTreeBinding.inflate(getLayoutInflater());
        binding.setParentCategory(mModel.getParentCategory());
        binding.layoutDialogTree.addView(getTreeView(), 2);
        binding.tvDialogTreeAddCategoryLayout.setOnClickListener(v -> mDialogUtils.treeViewAddCategoryClick(CATEGORY, mModel.getParentCategory(), false));
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
                    .setViewHolder(mModel.getCategoryViewHolder(new TreeHolderCategory(requireContext(), this, mThisCategory, mThisFolder)));
            for (Folder folder : mModel.getFolderList()) {//카테고리 노드 속 폴더 노드 생성
                if (category.getId() == folder.getParentId()) {
                    TreeNode folderTreeNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, mModel.getMargin()))
                            .setViewHolder(mModel.getFolderViewHolder(new TreeHolderFolder(requireContext(), this, mThisFolder)));
                    categoryTreeNode.addChild(folderTreeNode);
                }
            }
            mNodeRoot.addChild(categoryTreeNode);
        }
        return mNodeRoot;
    }

    @Override
    public void onActivityCreated(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //add category or folder
        mDialogUtils.getDialogViewModel().getBackStackEntryLive().observe(this, navBackStackEntry ->
                navBackStackEntry.getSavedStateHandle().getLiveData(ADD_CONFIRM_CLICK).observe(navBackStackEntry, o -> {
                    String itemType = (String) o;
                    if (itemType.equals(CATEGORY))
                        addCategoryConfirmClick();
                    else if (itemType.equals(FOLDER))
                        addFolderConfirmClick(savedInstanceState);
                }));

        if (savedInstanceState == null) //listFragment expanding node
            expandingNode();
        else mTreeView.restoreState(savedInstanceState.getString(TREE_VIEW_STATE));
    }

    private void expandingNode() {
        expandCategoryNode();

        if (mThisFolder != null) {
            //get top folder node
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
            if (mThisCategory.getId() == categoryViewHolder.getCategoryId() && !categoryTreeNode.getChildren().isEmpty()) {
                mTreeView.expandNode(categoryTreeNode);
                break;
            }
        }
    }

    public void expandingFolderNode(@NotNull List<TreeNode> topFolderNodeList) {
        for (TreeNode folderNode : topFolderNodeList) {
            TreeHolderFolder folderViewHolder = (TreeHolderFolder) folderNode.getViewHolder();
            for (Folder folder : mDialogUtils.getDialogViewModel().getFolderHistory())
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
            if (categoryViewHolder.isClickable())
                mDialogUtils.treeViewNodeClick(mModel.getSelectedItemSize(), categoryViewHolder.getCategoryId(), false);
        } else if (node.getViewHolder() instanceof TreeHolderFolder) {//folder node click
            TreeHolderFolder folderViewHolder = (TreeHolderFolder) node.getViewHolder();
            if (folderViewHolder.isClickable())
                mDialogUtils.treeViewNodeClick(mModel.getSelectedItemSize(), folderViewHolder.getFolderId(), false);
        }
    }

    @Override
    public void treeViewCategoryAddFolderClick(@NotNull TreeNode node, TreeHolderCategory.CategoryTreeHolder value) {
        long parentId = ((TreeHolderCategory) node.getViewHolder()).getCategoryId();
        mDialogUtils.treeViewAddFolderClick(FOLDER, mModel.getParentCategory(), parentId, false);
        mModel.setClickedNode(node);
    }

    @Override
    public void treeViewFolderAddFolderClick(@NotNull TreeNode node, TreeHolderFolder.FolderTreeHolder value) {
        long parentId = ((TreeHolderFolder) node.getViewHolder()).getFolderId();
        mDialogUtils.treeViewAddFolderClick(FOLDER, mModel.getParentCategory(), parentId, false);
        mModel.setClickedNode(node);
    }

    private void addCategoryConfirmClick() {
        TreeNode addedCategoryNode = new TreeNode(new TreeHolderCategory.CategoryTreeHolder(mDialogUtils.getDialogViewModel().getAddedCategory()))
                .setViewHolder(mModel.getCategoryViewHolder(new TreeHolderCategory(requireContext(), this, mThisCategory, mThisFolder)));
        mTreeView.addNode(mNodeRoot, addedCategoryNode);
    }

    private void addFolderConfirmClick(Bundle savedInstanceState) {
        if (mModel.getClickedNode().getViewHolder() instanceof TreeHolderCategory) {//category node
            if (savedInstanceState != null) mModel.findCategoryClickedNode(mNodeRoot);

            TreeNode addedFolderNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(mDialogUtils.getDialogViewModel().getAddedFolder(), mModel.getMargin()))
                    .setViewHolder(mModel.getFolderViewHolder(new TreeHolderFolder(requireContext(), this, mThisFolder)));

            mTreeView.addNode(mModel.getClickedNode(), addedFolderNode);
            mTreeView.expandNode(mModel.getClickedNode());

            mModel.categoryAddFolderConfirmClick();
        } else {
            if (savedInstanceState != null) mModel.findFolderClickedNode(mNodeRoot);

            TreeNode addedFolderNode = new TreeNode(new TreeHolderFolder.FolderTreeHolder(mDialogUtils.getDialogViewModel().getAddedFolder(), mModel.getPlusMargin()))
                    .setViewHolder(mModel.getFolderViewHolder(new TreeHolderFolder(requireContext(), this, mThisFolder)));

            mTreeView.addNode(mModel.getClickedNode(), addedFolderNode);
            mTreeView.expandNode(mModel.getClickedNode());

            mModel.folderAddFolderConfirmClick();
        }
    }
}