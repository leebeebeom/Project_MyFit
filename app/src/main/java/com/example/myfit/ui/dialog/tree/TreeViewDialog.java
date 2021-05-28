package com.example.myfit.ui.dialog.tree;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.example.myfit.R;
import com.example.myfit.databinding.LayoutDialogTreeBinding;
import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.ui.dialog.DialogBuilder;
import com.example.myfit.ui.dialog.tree.holder.BaseTreeHolder;
import com.example.myfit.ui.dialog.tree.holder.TreeCategoryHolder;
import com.example.myfit.ui.dialog.tree.holder.value.CategoryValue;
import com.example.myfit.ui.dialog.tree.holder.value.FolderValue;
import com.example.myfit.util.CommonUtil;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TreeViewDialog extends BaseDialog implements TreeNode.TreeNodeClickListener {
    public static final String TREE_VIEW_STATE = "state";

    @Inject
    TreeNodeProvider mTreeNodeProvider;
    @Inject
    LayoutDialogTreeBinding mBinding;

    private TreeViewModel mModel;
    private TreeNode mNodeRoot;
    private AndroidTreeView mTreeView;
    private int mParentIndex;
    private long mCurrentPositionId;
    private long[] mSelectedFolderIds, mSelectedSizeIds, mFolderPathIds;
    private long mCategoryId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(TreeViewModel.class);
        TreeViewDialogArgs args = TreeViewDialogArgs.fromBundle(getArguments());
        mParentIndex = args.getParentIndex();
        mSelectedFolderIds = args.getSelectedFolderIds();
        mSelectedSizeIds = args.getSelectedSizeIds();
        mCurrentPositionId = args.getCurrentPositionId();
        mFolderPathIds = args.getFolderPathIds();
        mCategoryId = args.getCategoryId();
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog alertDialog = getAlertDialog(getDialogBuilder());
        setTreeView();

        observeCategoryInsertIdLive();
        observeFolderInsertIdLive();
        observeCategoryTupleMutable();
        observeFolderTupleMutable();

        if (savedInstanceState == null && mCurrentPositionId != -1)
            expandingNode();
        else if (savedInstanceState != null)
            mTreeView.restoreState(savedInstanceState.getString(TREE_VIEW_STATE));
        return alertDialog;
    }

    @NotNull
    @Override
    protected AlertDialog getAlertDialog(DialogBuilder dialogBuilder) {
        return dialogBuilder
                .setTitle(getString(R.string.tree_title))
                .setView(getBinding().getRoot())
                .create();
    }

    private LayoutDialogTreeBinding getBinding() {
        String parentCategory = CommonUtil.getParentCategory(mParentIndex);
        mBinding.setParentCategory(parentCategory);
        mBinding.layoutAddCategory.setOnClickListener(v ->
                CommonUtil.navigate(getNavController(), R.id.treeViewDialog,
                        TreeViewDialogDirections.toAddCategoryDialog(mParentIndex)));
        return mBinding;
    }

    private void setTreeView() {
        mModel.getCategoryTuplesLive(mParentIndex).observe(this, categoryTuples ->
                mModel.getFolderTuplesLive(mParentIndex).observe(this, folderTuples ->
                        mModel.getFolderParentIdTuplesLive(mSelectedFolderIds).observe(this, folderParentIdTuples ->
                                mModel.getSizeParentIdTuplesLive(mSelectedSizeIds).observe(this, sizeParentIdTuples -> {
                                    LinkedList<TreeNode> categoryNodes = mTreeNodeProvider.makeNodes(categoryTuples, folderTuples);

                                    mTreeNodeProvider.checkIsSelectedItemParent(folderParentIdTuples, sizeParentIdTuples);
                                    mTreeNodeProvider.checkIsSelectedFolder(folderParentIdTuples);
                                    mTreeNodeProvider.checkIsParentSelectedFolder(folderParentIdTuples);
                                    mTreeNodeProvider.showCurrentPosition(mCurrentPositionId);

                                    AndroidTreeView treeView = getTreeView(categoryNodes);
                                    mBinding.treeViewContainer.removeAllViews();
                                    mBinding.treeViewContainer.addView(treeView.getView());
                                }))));
    }

    @NotNull
    private AndroidTreeView getTreeView(LinkedList<TreeNode> categoryNodes) {
        mNodeRoot = TreeNode.root().addChildren(categoryNodes);
        mTreeView = new AndroidTreeView(requireContext(), mNodeRoot);
        mTreeView.setDefaultAnimation(false);
        mTreeView.setUseAutoToggle(false);
        mTreeView.setDefaultNodeClickListener(this);
        return mTreeView;
    }

    private void observeCategoryInsertIdLive() {
        mModel.getCategoryInsertIdLive().observe(getViewLifecycleOwner(), insertId -> {
            if (insertId != null) {
                mModel.setCategoryInsertId(insertId);
                mModel.getCategoryInsertIdLive().setValue(null);
            }
        });
    }

    private void observeFolderInsertIdLive() {
        mModel.getFolderInsertIdLive().observe(getViewLifecycleOwner(), insertId -> {
            if (insertId != null) {
                mModel.setFolderInsertId(insertId);
                mModel.getCategoryInsertIdLive().setValue(null);
            }
        });
    }

    private void observeCategoryTupleMutable() {
        mModel.getCategoryTupleMutable().observe(getViewLifecycleOwner(), categoryTuple -> {
            if (categoryTuple != null) {
                TreeNode addedCategoryNode = new TreeNode(new CategoryValue(categoryTuple)).setViewHolder(mTreeNodeProvider.makeCategoryHolder());
                mTreeView.addNode(mNodeRoot, addedCategoryNode);
                mModel.getCategoryTupleMutable().setValue(null);
            }
        });
    }

    private void observeFolderTupleMutable() {
        mModel.getFolderTupleMutable().observe(getViewLifecycleOwner(), folderTuple -> {
            if (folderTuple != null) {
                TreeNode clickedNode = mTreeNodeProvider.getClickedNode(folderTuple);
                int margin = getMargin(clickedNode);

                TreeNode addedFolderNode = new TreeNode(new FolderValue(folderTuple, margin))
                        .setViewHolder(mTreeNodeProvider.makeFolderHolder());
                mTreeView.addNode(clickedNode, addedFolderNode);

                addContentsSize(clickedNode);
                BaseTreeHolder<?> viewHolder = (BaseTreeHolder<?>) clickedNode.getViewHolder();
                viewHolder.setIconClickable();
                mModel.getFolderTupleMutable().setValue(null);
            }
        });
    }

    private int getMargin(TreeNode clickedNode) {
        if (clickedNode.getValue() instanceof FolderValue)
            return ((FolderValue) clickedNode.getValue()).getMargin() + getResources().getDimensionPixelSize(R.dimen._8sdp);
        else return mTreeNodeProvider.getMargin();
    }

    private void addContentsSize(TreeNode clickedNode) {
        TextView tvContentsSize = ((BaseTreeHolder<?>) clickedNode.getViewHolder()).getContentsSize();
        String originContentsSize = tvContentsSize.getText().toString();
        int addedContentsSize = Integer.parseInt(originContentsSize + 1);
        tvContentsSize.setText(String.valueOf(addedContentsSize));
    }

    private void expandingNode() {
        expandCategoryNode();
        expandingFolderNode();
    }

    private void expandCategoryNode() {
        TreeCategoryHolder[] categoryHolders = mTreeNodeProvider.getCategoryHolders();
        Arrays.stream(categoryHolders)
                .filter(categoryHolder -> categoryHolder.getId() == mCategoryId)
                .forEach(categoryHolder -> mTreeView.expandNode(categoryHolder.getNode()));
    }

    public void expandingFolderNode() {
        Arrays.stream(mTreeNodeProvider.getFolderHolders())
                .filter(folderHolder ->
                        Arrays.stream(mFolderPathIds)
                                .anyMatch(folderId -> folderHolder.getId() == folderId))
                .forEach(folderHolder -> mTreeView.expandNode(folderHolder.getNode()));
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TREE_VIEW_STATE, mTreeView.getSaveState());
    }

    @Override
    public void onClick(@NotNull TreeNode node, Object value) {
        BaseTreeHolder<?> viewHolder = (BaseTreeHolder<?>) node.getViewHolder();
        if (viewHolder.isClickable()) {
            TreeViewDialogDirections.ToMoveFolderAndSizeDialog action =
                    TreeViewDialogDirections.toMoveFolderAndSizeDialog(
                            mSelectedFolderIds, mSelectedSizeIds, viewHolder.getId());
            CommonUtil.navigate(getNavController(), R.id.treeViewDialog, action);
        }
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return null;
    }

    @Override
    protected int getResId() {
        return R.id.treeViewDialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }
}