package com.leebeebeom.closetnote.ui.dialog.tree;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import com.leebeebeom.closetnote.NavGraphTreeViewArgs;
import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.databinding.LayoutDialogTreeBinding;
import com.leebeebeom.closetnote.ui.dialog.BaseDialog;
import com.leebeebeom.closetnote.ui.dialog.tree.holder.BaseTreeHolder;
import com.leebeebeom.closetnote.ui.dialog.tree.holder.TreeCategoryHolder;
import com.leebeebeom.closetnote.ui.dialog.tree.holder.TreeFolderHolder;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class TreeViewDialog extends BaseDialog implements TreeNode.TreeNodeClickListener {
    public static final String TREE_VIEW_STATE = "state";

    @Inject
    TreeNodeProvider mTreeNodeProvider;
    @Inject
    NavController mNavController;

    private LayoutDialogTreeBinding mBinding;
    private TreeViewModel mModel;
    private TreeNode mNodeRoot;
    private AndroidTreeView mTreeView;
    private long mCurrentPositionId;
    private long[] mSelectedFolderIds, mSelectedSizeIds, mFolderPathIds;
    private long mCategoryId;
    private int mParentIndex;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mModel = new ViewModelProvider(this).get(TreeViewModel.class);
        mBinding = LayoutDialogTreeBinding.inflate(LayoutInflater.from(requireContext()));

        NavGraphTreeViewArgs args = NavGraphTreeViewArgs.fromBundle(getArguments());
        if (mModel.getParentCategory() == null)
            mModel.setParentCategory(args.getParentIndex());

        mBinding.setDialog(this);
        mBinding.setModel(mModel);
        mBinding.setLifecycleOwner(this);

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
        AlertDialog alertDialog = getAlertDialog();
        setTreeView(savedInstanceState);
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

    private void setTreeView(Bundle savedInstanceState) {
        mModel.getCategoryTuplesLive(mParentIndex).observe(this, categoryTuples ->
                mModel.getFolderTuplesLive(mParentIndex).observe(this, folderTuples ->
                        mModel.getFolderParentIdTuplesLive(mSelectedFolderIds).observe(this, folderParentIdTuples ->
                                mModel.getSizeParentIdTuplesLive(mSelectedSizeIds).observe(this, sizeParentIdTuples -> {
                                    List<TreeNode> categoryNodes = mTreeNodeProvider.makeNodes(categoryTuples, folderTuples);
                                    AndroidTreeView treeView = getTreeView(categoryNodes);

                                    mBinding.treeViewContainer.removeAllViewsInLayout();
                                    mBinding.treeViewContainer.addView(treeView.getView());

                                    if (savedInstanceState == null && mCurrentPositionId != -1)
                                        expandingNodes();
                                    else if (savedInstanceState != null)
                                        mTreeView.restoreState(savedInstanceState.getString(TREE_VIEW_STATE));

                                    mTreeNodeProvider.checkIsSelectedItemParent(folderParentIdTuples);
                                    mTreeNodeProvider.checkIsSelectedItemParent(sizeParentIdTuples);
                                    mTreeNodeProvider.checkIsSelectedFolder(folderParentIdTuples);
                                    mTreeNodeProvider.checkIsParentSelectedFolder();

                                    mTreeNodeProvider.showCurrentPosition(mCurrentPositionId);
                                }))));
    }

    @NotNull
    private AndroidTreeView getTreeView(List<TreeNode> categoryNodes) {
        mNodeRoot = TreeNode.root().addChildren(categoryNodes);
        mTreeView = new AndroidTreeView(requireContext(), mNodeRoot);
        mTreeView.setDefaultAnimation(false);
        mTreeView.setUseAutoToggle(false);
        mTreeView.setDefaultNodeClickListener(this);
        return mTreeView;
    }

    private void expandingNodes() {
        expandCategoryNode();
        expandingFolderNode();
    }

    private void expandCategoryNode() {
        Optional<TreeCategoryHolder> currentPositionHolder =
                mTreeNodeProvider.getCategoryHolders().stream()
                        .filter(categoryHolder -> categoryHolder.getTupleId() == mCategoryId)
                        .findFirst();
        currentPositionHolder.ifPresent(categoryHolder -> mTreeView.expandNode(categoryHolder.getNode()));
    }

    public void expandingFolderNode() {
        Arrays.stream(mFolderPathIds)
                .forEach(folderPathId -> {
                    Optional<TreeFolderHolder> currentPositionHolder =
                            mTreeNodeProvider.getFolderHolders().stream()
                                    .filter(folderHolder -> folderHolder.getTupleId() == folderPathId)
                                    .findFirst();
                    currentPositionHolder.ifPresent(folderHolder -> mTreeView.expandNode(currentPositionHolder.get().getNode()));
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

    public void navigateAddCategory() {
        CommonUtil.navigate(mNavController, R.id.treeViewDialog, TreeViewDialogDirections.toAddCategory(mParentIndex));
    }
}