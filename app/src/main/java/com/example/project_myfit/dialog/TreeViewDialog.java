package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.databinding.LayoutDialogTreeBinding;
import com.example.project_myfit.util.CommonUtil;
import com.example.project_myfit.util.DummyUtil;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.ADD_CONFIRM;
import static com.example.project_myfit.util.MyFitConstant.CATEGORY;
import static com.example.project_myfit.util.MyFitConstant.FOLDER;
import static com.example.project_myfit.util.MyFitConstant.TREE_VIEW_STATE;

public class TreeViewDialog extends DialogFragment implements TreeNode.TreeNodeClickListener, TreeHolderListener {
    private int mNavGraphId;
    private TreeViewModel mModel;
    private TreeNode mNodeRoot;
    private AndroidTreeView mTreeView;
    private DialogUtil mDialogUtil;
    private Category mThisCategory;
    private Folder mThisFolder;
    private boolean mCategoryAdded;
    private DummyUtil mDummyUtil;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(TreeViewModel.class);
        mModel.setParentCategory(TreeViewDialogArgs.fromBundle(getArguments()).getParentCategory());
        mNavGraphId = TreeViewDialogArgs.fromBundle(getArguments()).getNavGraphId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mDialogUtil = new DialogUtil(requireContext(), this, mNavGraphId);
        mThisCategory = mDialogUtil.getDialogViewModel().getCategory(TreeViewDialogArgs.fromBundle(getArguments()).getThisCategoryId());
        mThisFolder = mDialogUtil.getDialogViewModel().getFolder(TreeViewDialogArgs.fromBundle(getArguments()).getThisFolderId());
        mModel.setSelectedItemList(mDialogUtil.getDialogViewModel().getSelectedFolderList(), mDialogUtil.getDialogViewModel().getSelectedSizeList());

        AlertDialog alertDialog = getAlertDialog();

        mDialogUtil.setBackground(alertDialog.getWindow());
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
        String category = " " + mModel.getParentCategory();
        binding.tvCategory.setText(category);
        binding.layout.addView(getTreeView(), 2);
        binding.layoutAddCategory.setOnClickListener(v ->
                CommonUtil.navigate(mDialogUtil.getNavController(), R.id.treeViewDialog,
                        TreeViewDialogDirections.actionTreeViewDialogToAddDialog(CATEGORY, mModel.getParentCategory(), 0, mNavGraphId)));
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

        observeDialogLive(savedInstanceState);

        if (savedInstanceState == null && mNavGraphId == R.id.nav_graph_main)
            expandingNode();
        else if (savedInstanceState != null)
            mTreeView.restoreState(savedInstanceState.getString(TREE_VIEW_STATE));
    }

    private void observeDialogLive(@org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mDialogUtil.getDialogViewModel().getBackStackEntryLive().observe(this, navBackStackEntry ->
                navBackStackEntry.getSavedStateHandle().getLiveData(ADD_CONFIRM).observe(navBackStackEntry, o -> {
                    int itemType = (int) o;
                    if (itemType == CATEGORY)
                        addCategoryNode();
                    else addFolderNode(savedInstanceState);
                }));
    }

    private void addCategoryNode() {
        TreeNode addedCategoryNode = getCategoryNode(mDialogUtil.getDialogViewModel().getAddedCategory());
        mTreeView.addNode(mNodeRoot, addedCategoryNode);
    }

    private void addFolderNode(Bundle savedInstanceState) {
        TreeNode clickedNode = mModel.getClickedNode();
        TreeNode.BaseNodeViewHolder<?> viewHolder = clickedNode.getViewHolder();

        if (viewHolder instanceof TreeHolderCategory)
            categoryAddFolderNode(savedInstanceState, clickedNode, (TreeHolderCategory) viewHolder);
        else if (viewHolder instanceof TreeHolderFolder)
            folderAddFolder(savedInstanceState, clickedNode, (TreeHolderFolder) viewHolder);
    }

    private void categoryAddFolderNode(Bundle savedInstanceState, TreeNode clickedNode, TreeHolderCategory categoryViewHolder) {
        if (savedInstanceState != null) {
            clickedNode = mModel.findCategoryClickedNode(mNodeRoot, (TreeHolderCategory) categoryViewHolder);
            categoryViewHolder = (TreeHolderCategory) clickedNode.getViewHolder();
        }

        TreeNode addedFolderNode = getFolderNode(mDialogUtil.getDialogViewModel().getAddedFolder(), mModel.getMargin());

        addNode(clickedNode, addedFolderNode);
        setContentsSize(categoryViewHolder.getBinding().tvContentsSize);
        categoryViewHolder.setIconClickable();

        if (mDummyUtil == null) mDummyUtil = new DummyUtil(requireContext());
        mDummyUtil.setCategoryDummy(((TreeHolderCategory) categoryViewHolder).getCategory());
    }

    private void folderAddFolder(Bundle savedInstanceState, TreeNode clickedNode, TreeHolderFolder folderViewHolder) {
        if (savedInstanceState != null) {
            clickedNode = mModel.findFolderClickedNode(mNodeRoot, (TreeHolderFolder) folderViewHolder);
            folderViewHolder = (TreeHolderFolder) clickedNode.getViewHolder();
        }

        TreeNode addedFolderNode = getFolderNode(mDialogUtil.getDialogViewModel().getAddedFolder(), mModel.getPlusMargin());
        addNode(clickedNode, addedFolderNode);
        folderViewHolder.setIconClickable();
        setContentsSize(folderViewHolder.getBinding().tvContentsSize);

        if (mDummyUtil == null) mDummyUtil = new DummyUtil(requireContext());
        mDummyUtil.setFolderDummy(((TreeHolderFolder) folderViewHolder).getFolder());
    }

    private void addNode(TreeNode clickedNode, TreeNode addedNode) {
        mTreeView.addNode(clickedNode, addedNode);
        mTreeView.expandNode(clickedNode);
    }

    private void setContentsSize(@NotNull MaterialTextView tvContentsSize) {
        int size = Integer.parseInt(tvContentsSize.getText().toString());
        tvContentsSize.setText(String.valueOf(size + 1));
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
            if (mThisCategory != null && mThisCategory.getId() == categoryViewHolder.getCategoryId() && !categoryTreeNode.getChildren().isEmpty()) {
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
        mModel.setResourcesNull();
    }

    @Override
    public void onClick(@NotNull TreeNode node, Object value) {
        if (node.getViewHolder() instanceof TreeHolderCategory && ((TreeHolderCategory) node.getViewHolder()).isClickable()) {
            CommonUtil.navigate(mDialogUtil.getNavController(), R.id.treeViewDialog,
                    TreeViewDialogDirections.actionTreeViewDialogToItemMoveDialog(
                            mModel.getSelectedItemSize(), ((TreeHolderCategory) node.getViewHolder()).getCategoryId(), mNavGraphId));
        } else if (node.getViewHolder() instanceof TreeHolderFolder && ((TreeHolderFolder) node.getViewHolder()).isClickable())
            CommonUtil.navigate(mDialogUtil.getNavController(), R.id.treeViewDialog,
                    TreeViewDialogDirections.actionTreeViewDialogToItemMoveDialog(
                            mModel.getSelectedItemSize(), ((TreeHolderFolder) node.getViewHolder()).getFolderId(), mNavGraphId));
    }

    @Override
    public void addFolderIconClick(@NotNull TreeNode node, long parentId) {
        CommonUtil.navigate(mDialogUtil.getNavController(), R.id.treeViewDialog,
                TreeViewDialogDirections.actionTreeViewDialogToAddDialog(FOLDER, mModel.getParentCategory(), parentId, mNavGraphId));
        mModel.setClickedNode(node);
    }

    private TreeNode getCategoryNode(Category category) {
        return new TreeNode(new TreeHolderCategory.CategoryTreeValue(category))
                .setViewHolder(mModel.getCategoryViewHolder(new TreeHolderCategory(requireContext(), this, mThisCategory, mThisFolder)));
    }

    private TreeNode getFolderNode(Folder folder, int margin) {
        return new TreeNode(new TreeHolderFolder.FolderTreeHolder(folder, margin))
                .setViewHolder(mModel.getFolderViewHolder(new TreeHolderFolder(requireContext(), this, mThisFolder)));
    }
}