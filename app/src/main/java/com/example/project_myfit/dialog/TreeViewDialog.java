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

import static com.example.project_myfit.util.MyFitConstant.ADD_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.CATEGORY;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.TREE_VIEW_STATE;

public class TreeViewDialog extends DialogFragment implements TreeNode.TreeNodeClickListener,
        TreeHolderCategory.TreeViewCategoryFolderAddListener, TreeHolderFolder.TreeViewFolderFolderAddListener {
    private TreeViewModel mModel;
    private TreeNode mNodeRoot;
    private AndroidTreeView mTreeView;
    private DialogUtil mDialogUtil;
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
        mDialogUtil = new DialogUtil(requireContext(), this, R.id.nav_graph_main);
        mThisCategory = mDialogUtil.getDialogViewModel().getCategory(TreeViewDialogArgs.fromBundle(getArguments()).getThisCategoryId());
        mThisFolder = mDialogUtil.getDialogViewModel().getFolder(TreeViewDialogArgs.fromBundle(getArguments()).getThisFolderId());
        mModel.setSelectedItemList(mDialogUtil.getDialogViewModel().getSelectedFolderList(), mDialogUtil.getDialogViewModel().getSelectedSizeList());

        AlertDialog alertDialog = getAlertDialog();

        Window window = alertDialog.getWindow();
        mDialogUtil.setLayout(window);
        mDialogUtil.setTextSize(alertDialog);
        return alertDialog;
    }

    @NotNull
    private AlertDialog getAlertDialog() {
        return new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialogStyle)
                .setView(getDialogView())
                .setTitle(R.string.tree_title)
                .create();
    }

    @NotNull
    private View getDialogView() {
        LayoutDialogTreeBinding binding = LayoutDialogTreeBinding.inflate(getLayoutInflater());
        binding.setParentCategory(mModel.getParentCategory());
        binding.layout.addView(getTreeView(), 2);
        binding.layoutAddCategory.setOnClickListener(v -> mDialogUtil.treeViewAddCategory(CATEGORY, mModel.getParentCategory(), false));
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

        for (Category category : mModel.getCategoryList()) {
            TreeNode categoryTreeNode = getCategoryNode(category);
            for (Folder folder : mModel.getFolderList()) {
                if (category.getId() == folder.getParentId()) {
                    TreeNode folderTreeNode = getFolderNode(folder, mModel.getMargin());
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
        dialogLive(savedInstanceState);

        if (savedInstanceState == null)
            expandingNode();
        else mTreeView.restoreState(savedInstanceState.getString(TREE_VIEW_STATE));
    }

    private void dialogLive(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mDialogUtil.getDialogViewModel().getBackStackEntryLive().observe(this, navBackStackEntry ->
                navBackStackEntry.getSavedStateHandle().getLiveData(ADD_CONFIRM).observe(navBackStackEntry, o -> {
                    String itemType = (String) o;
                    if (itemType.equals(CATEGORY))
                        addCategoryConfirmClick();
                    else if (itemType.equals(FOLDER))
                        addFolderConfirmClick(savedInstanceState);
                }));
    }

    private void addCategoryConfirmClick() {
        TreeNode addedCategoryNode = getCategoryNode(mDialogUtil.getDialogViewModel().getAddedCategory());
        mTreeView.addNode(mNodeRoot, addedCategoryNode);
    }

    private void addFolderConfirmClick(Bundle savedInstanceState) {
        TreeNode clickedNode = mModel.getClickedNode();
        TreeNode.BaseNodeViewHolder<?> viewHolder = clickedNode.getViewHolder();

        if (viewHolder instanceof TreeHolderCategory)
            categoryAddFolder(savedInstanceState, clickedNode, viewHolder);
        else if (viewHolder instanceof TreeHolderFolder)
            folderAddFolder(savedInstanceState, clickedNode, viewHolder);
    }

    private void categoryAddFolder(Bundle savedInstanceState, TreeNode clickedNode, TreeNode.BaseNodeViewHolder<?> viewHolder) {
        if (savedInstanceState != null) {
            clickedNode = mModel.findCategoryClickedNode(mNodeRoot, (TreeHolderCategory) viewHolder);
            viewHolder = clickedNode.getViewHolder();
        }

        TreeNode addedFolderNode = getFolderNode(mDialogUtil.getDialogViewModel().getAddedFolder(), mModel.getMargin());
        mTreeView.addNode(clickedNode, addedFolderNode);
        mTreeView.expandNode(clickedNode);

        mModel.categoryAddFolder((TreeHolderCategory) viewHolder);
    }

    private void folderAddFolder(Bundle savedInstanceState, TreeNode clickedNode, TreeNode.BaseNodeViewHolder<?> viewHolder) {
        if (savedInstanceState != null) {
            clickedNode = mModel.findFolderClickedNode(mNodeRoot, (TreeHolderFolder) viewHolder);
            viewHolder = clickedNode.getViewHolder();
        }

        TreeNode addedFolderNode = getFolderNode(mDialogUtil.getDialogViewModel().getAddedFolder(), mModel.getPlusMargin());
        mTreeView.addNode(clickedNode, addedFolderNode);
        mTreeView.expandNode(clickedNode);

        mModel.folderAddFolder((TreeHolderFolder) viewHolder);
    }

    private void expandingNode() {
        expandCategoryNode();

        if (mThisFolder != null) {
            List<TreeNode> topFolderNodeList = getTopFolderNodeList();
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

    @NotNull
    private List<TreeNode> getTopFolderNodeList() {
        List<TreeNode> topFolderNodeList = new ArrayList<>();
        for (TreeNode categoryTreeNode : mNodeRoot.getChildren())
            if (!categoryTreeNode.getChildren().isEmpty())
                topFolderNodeList.addAll(categoryTreeNode.getChildren());
        return topFolderNodeList;
    }

    public void expandingFolderNode(@NotNull List<TreeNode> topFolderNodeList) {
        for (TreeNode folderNode : topFolderNodeList) {
            TreeHolderFolder folderViewHolder = (TreeHolderFolder) folderNode.getViewHolder();
            for (Folder folder : mDialogUtil.getDialogViewModel().getFolderHistory())
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
    public void onClick(@NotNull TreeNode node, Object value) {
        if (node.getViewHolder() instanceof TreeHolderCategory) {
            TreeHolderCategory categoryViewHolder = (TreeHolderCategory) node.getViewHolder();
            if (categoryViewHolder.isClickable())
                mDialogUtil.treeViewNodeClick(mModel.getSelectedItemSize(), categoryViewHolder.getCategoryId(), false);
        } else if (node.getViewHolder() instanceof TreeHolderFolder) {
            TreeHolderFolder folderViewHolder = (TreeHolderFolder) node.getViewHolder();
            if (folderViewHolder.isClickable())
                mDialogUtil.treeViewNodeClick(mModel.getSelectedItemSize(), folderViewHolder.getFolderId(), false);
        }
    }

    @Override
    public void treeViewCategoryAddFolderClick(@NotNull TreeNode node, TreeHolderCategory.CategoryTreeHolder value) {
        long parentId = ((TreeHolderCategory) node.getViewHolder()).getCategoryId();
        mDialogUtil.treeViewAddFolder(FOLDER, mModel.getParentCategory(), parentId, false);
        mModel.setClickedNode(node);
    }

    @Override
    public void treeViewFolderAddFolderClick(@NotNull TreeNode node, TreeHolderFolder.FolderTreeHolder value) {
        long parentId = ((TreeHolderFolder) node.getViewHolder()).getFolderId();
        mDialogUtil.treeViewAddFolder(FOLDER, mModel.getParentCategory(), parentId, false);
        mModel.setClickedNode(node);
    }

    private TreeNode getFolderNode(Folder folder, int margin) {
        return new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, margin))
                .setViewHolder(mModel.getFolderViewHolder(new TreeHolderFolder(requireContext(), this, mThisFolder)));
    }

    private TreeNode getCategoryNode(Category category) {
        return new TreeNode(new TreeHolderCategory.CategoryTreeHolder(category))
                .setViewHolder(mModel.getCategoryViewHolder(new TreeHolderCategory(requireContext(), this, mThisCategory, mThisFolder)));
    }
}