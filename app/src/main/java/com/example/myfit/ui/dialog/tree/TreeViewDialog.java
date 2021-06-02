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
import com.example.myfit.ui.dialog.tree.holder.BaseTreeHolder;
import com.example.myfit.ui.dialog.tree.holder.TreeCategoryHolder;
import com.example.myfit.ui.dialog.tree.holder.TreeFolderHolder;
import com.example.myfit.ui.dialog.tree.holder.value.CategoryValue;
import com.example.myfit.ui.dialog.tree.holder.value.FolderValue;
import com.example.myfit.util.CommonUtil;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;

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
    private long mCurrentPositionId;
    private long[] mSelectedFolderIds, mSelectedSizeIds, mFolderPathIds;
    private long mCategoryId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(TreeViewModel.class);

        TreeViewDialogArgs args = TreeViewDialogArgs.fromBundle(getArguments());
        if (mModel.getParentCategory() == null)
            mModel.setParentCategory(args.getParentIndex());
        mBinding.setLifecycleOwner(this);

        mSelectedFolderIds = args.getSelectedFolderIds();
        mSelectedSizeIds = args.getSelectedSizeIds();
        mCurrentPositionId = args.getCurrentPositionId();
        mFolderPathIds = args.getFolderPathIds();
        mCategoryId = args.getCategoryId();
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog alertDialog = getAlertDialog();
        setTreeView();

        observeCategoryTupleMutable();
        observeFolderTupleMutable();

        if (savedInstanceState == null && mCurrentPositionId != -1)
            expandingNodes();
        else if (savedInstanceState != null)
            mTreeView.restoreState(savedInstanceState.getString(TREE_VIEW_STATE));
        return alertDialog;
    }

    @NotNull
    @Override
    protected AlertDialog getAlertDialog() {
        return mDialogBuilder
                .setTitle(getString(R.string.tree_title))
                .setView(mBinding.getRoot())
                .create();
    }

    private void setTreeView() {
        mModel.getCategoryTuplesLive().observe(this, categoryTuples ->
                mModel.getFolderTuplesLive().observe(this, folderTuples ->
                        mModel.getFolderParentIdTuplesLive(mSelectedFolderIds).observe(this, folderParentIdTuples ->
                                mModel.getSizeParentIdTuplesLive(mSelectedSizeIds).observe(this, sizeParentIdTuples -> {
                                    LinkedList<TreeNode> categoryNodes = mTreeNodeProvider.makeNodes(categoryTuples, folderTuples);

                                    mTreeNodeProvider.checkIsSelectedItemParent(folderParentIdTuples, sizeParentIdTuples);
                                    mTreeNodeProvider.checkIsSelectedFolder(folderParentIdTuples);
                                    mTreeNodeProvider.checkIsParentSelectedFolder(folderParentIdTuples);
                                    mTreeNodeProvider.showCurrentPosition(mCurrentPositionId);

                                    AndroidTreeView treeView = getTreeView(categoryNodes);
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

    private void observeCategoryTupleMutable() {
        mModel.getAddedCategoryTupleLive().observe(getViewLifecycleOwner(), categoryTuple -> {
            if (categoryTuple != null) {
                TreeNode addedCategoryNode = new TreeNode(new CategoryValue(categoryTuple)).setViewHolder(mTreeNodeProvider.makeCategoryHolder());
                mTreeView.addNode(mNodeRoot, addedCategoryNode);
                mModel.getAddedCategoryTupleLive().setValue(null);
            }
        });
    }

    private void observeFolderTupleMutable() {
        mModel.getAddedFolderTupleLive().observe(getViewLifecycleOwner(), folderTuple -> {
            if (folderTuple != null) {
                TreeNode clickedNode = mTreeNodeProvider.getClickedNode(folderTuple);
                int margin = getMargin(clickedNode);

                TreeNode addedFolderNode = new TreeNode(new FolderValue(folderTuple, margin))
                        .setViewHolder(mTreeNodeProvider.makeFolderHolder());
                mTreeView.addNode(clickedNode, addedFolderNode);

                BaseTreeHolder<?> viewHolder = (BaseTreeHolder<?>) clickedNode.getViewHolder();
                addContentSize(viewHolder);
                viewHolder.setFolderIconClickable();
                mModel.getAddedFolderTupleLive().setValue(null);
            }
        });
    }

    private int getMargin(TreeNode clickedNode) {
        if (clickedNode.getValue() instanceof FolderValue)
            return ((FolderValue) clickedNode.getValue()).getMargin() + getResources().getDimensionPixelSize(R.dimen._8sdp);
        else return mTreeNodeProvider.getMargin();
    }

    private void addContentSize(BaseTreeHolder<?> viewHolder) {
        TextView tvContentSize = viewHolder.getContentSize();
        String originContentSize = tvContentSize.getText().toString();
        int addedContentSize = Integer.parseInt(originContentSize) + 1;
        tvContentSize.setText(String.valueOf(addedContentSize));
    }

    private void expandingNodes() {
        expandCategoryNode();
        expandingFolderNode();
    }

    private void expandCategoryNode() {
        TreeCategoryHolder[] categoryHolders = mTreeNodeProvider.getCategoryHolders();
        Optional<TreeCategoryHolder> holder = Arrays.stream(categoryHolders)
                .filter(categoryHolder -> categoryHolder.getTupleId() == mCategoryId)
                .findFirst();
        holder.ifPresent(categoryHolder -> mTreeView.expandNode(categoryHolder.getNode()));
    }

    public void expandingFolderNode() {
        TreeFolderHolder[] folderHolders = mTreeNodeProvider.getFolderHolders();
        Arrays.stream(mFolderPathIds)
                .forEach(folderPathId -> {
                    Optional<TreeFolderHolder> holder = Arrays.stream(folderHolders)
                            .filter(folderHolder -> folderHolder.getTupleId() == folderPathId)
                            .findFirst();
                    holder.ifPresent(folderHolder -> mTreeView.expandNode(holder.get().getNode()));
                });
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
                            mSelectedFolderIds, mSelectedSizeIds, viewHolder.getTupleId());
            CommonUtil.navigate(mNavController, R.id.treeViewDialog, action);
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