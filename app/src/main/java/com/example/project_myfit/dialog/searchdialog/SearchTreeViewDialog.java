package com.example.project_myfit.dialog.searchdialog;

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
import com.example.project_myfit.dialog.DialogUtils;
import com.example.project_myfit.dialog.TreeHolderCategory;
import com.example.project_myfit.dialog.TreeHolderFolder;
import com.example.project_myfit.dialog.TreeViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.util.MyFitConstant.ADD_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.CATEGORY;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.TREE_VIEW_STATE;

public class SearchTreeViewDialog extends DialogFragment implements TreeNode.TreeNodeClickListener,
        TreeHolderCategory.TreeViewCategoryFolderAddListener, TreeHolderFolder.TreeViewFolderFolderAddListener {
    private TreeViewModel mModel;
    private TreeNode mNodeRoot;
    private AndroidTreeView mTreeView;
    private DialogUtils mDialogUtils;
    private boolean mCategoryAdded;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(TreeViewModel.class);
        mModel.setParentCategory(SearchTreeViewDialogArgs.fromBundle(getArguments()).getParentCategory());
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mDialogUtils = new DialogUtils(requireContext(), getLayoutInflater(), this, R.id.nav_graph_search);
        mModel.setSelectedItemList(mDialogUtils.getDialogViewModel().getSelectedFolderList(), mDialogUtils.getDialogViewModel().getSelectedSizeList());

        AlertDialog alertDialog = getAlertDialog();

        Window window = alertDialog.getWindow();
        mDialogUtils.setLayout(window);
        mDialogUtils.setTextSize(alertDialog);
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
        binding.layoutDialogTree.addView(getTreeView(), 2);
        binding.tvDialogTreeAddCategoryLayout.setOnClickListener(v -> mDialogUtils.treeViewAddCategory(CATEGORY, mModel.getParentCategory(), true));
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
            TreeNode categoryTreeNode = getCategoryNode(category);
            for (Folder folder : mModel.getFolderList()) {//카테고리 노드 속 폴더 노드 생성
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

        if (savedInstanceState != null)
            mTreeView.restoreState(savedInstanceState.getString(TREE_VIEW_STATE));
    }

    private void dialogLive(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mDialogUtils.getDialogViewModel().getBackStackEntryLive().observe(this, navBackStackEntry ->
                navBackStackEntry.getSavedStateHandle().getLiveData(ADD_CONFIRM).observe(navBackStackEntry, o -> {
                    String itemType = (String) o;
                    if (itemType.equals(CATEGORY))
                        addCategoryConfirmClick();
                    else if (itemType.equals(FOLDER))
                        addFolderConfirmClick(savedInstanceState);
                }));
    }

    private void addCategoryConfirmClick() {
        TreeNode addedCategoryNode = getCategoryNode(mDialogUtils.getDialogViewModel().getAddedCategory());
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

        TreeNode addedFolderNode = getFolderNode(mDialogUtils.getDialogViewModel().getAddedFolder(), mModel.getMargin());
        mTreeView.addNode(clickedNode, addedFolderNode);
        mTreeView.expandNode(clickedNode);

        mModel.categoryAddFolder((TreeHolderCategory) viewHolder);
    }

    private void folderAddFolder(Bundle savedInstanceState, TreeNode clickedNode, TreeNode.BaseNodeViewHolder<?> viewHolder) {
        if (savedInstanceState != null) {
            clickedNode = mModel.findFolderClickedNode(mNodeRoot);
            viewHolder = clickedNode.getViewHolder();
        }

        TreeNode addedFolderNode = getFolderNode(mDialogUtils.getDialogViewModel().getAddedFolder(), mModel.getPlusMargin());
        mTreeView.addNode(clickedNode, addedFolderNode);
        mTreeView.expandNode(clickedNode);

        mModel.folderAddFolderConfirmClick((TreeHolderFolder) viewHolder);
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
                mDialogUtils.treeViewNodeClick(mModel.getSelectedItemSize(), categoryViewHolder.getCategoryId(), true);
        } else if (node.getViewHolder() instanceof TreeHolderFolder) {//folder node click
            TreeHolderFolder folderViewHolder = (TreeHolderFolder) node.getViewHolder();
            if (folderViewHolder.isClickable())
                mDialogUtils.treeViewNodeClick(mModel.getSelectedItemSize(), folderViewHolder.getFolderId(),true);
        }
    }

    @Override
    public void treeViewCategoryAddFolderClick(@NotNull TreeNode node, TreeHolderCategory.CategoryTreeHolder value) {
        long parentId = ((TreeHolderCategory) node.getViewHolder()).getCategoryId();
        mDialogUtils.treeViewAddFolder(FOLDER, mModel.getParentCategory(), parentId, true);
        mModel.setClickedNode(node);
    }

    @Override
    public void treeViewFolderAddFolderClick(@NotNull TreeNode node, TreeHolderFolder.FolderTreeHolder value) {
        long parentId = ((TreeHolderFolder) node.getViewHolder()).getFolderId();
        mDialogUtils.treeViewAddFolder(FOLDER, mModel.getParentCategory(), parentId, true);
        mModel.setClickedNode(node);
    }

    private TreeNode getFolderNode(Folder folder, int margin) {
        return new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, margin))
                .setViewHolder(mModel.getFolderViewHolder(new TreeHolderFolder(requireContext(), this, null)));
    }

    private TreeNode getCategoryNode(Category category) {
        return new TreeNode(new TreeHolderCategory.CategoryTreeHolder(category))
                .setViewHolder(mModel.getCategoryViewHolder(new TreeHolderCategory(requireContext(), this, null, null)));
    }
}