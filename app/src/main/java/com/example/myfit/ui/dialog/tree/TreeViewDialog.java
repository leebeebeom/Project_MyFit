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
    TreeNodeProvider treeNodeProvider;
    private int parentIndex;
    private TreeViewModel model;
    private TreeNode nodeRoot;
    private AndroidTreeView treeView;
    private LayoutDialogTreeBinding binding;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new ViewModelProvider(this).get(TreeViewModel.class);
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

        if (savedInstanceState == null && getCurrentPositionId() != -1)
            expandingNode();
        else if (savedInstanceState != null)
            treeView.restoreState(savedInstanceState.getString(TREE_VIEW_STATE));
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
        binding = LayoutDialogTreeBinding.inflate(getLayoutInflater());
        String parentCategory = CommonUtil.getParentCategory(parentIndex);
        binding.setParentCategory(parentCategory);
        binding.layoutAddCategory.setOnClickListener(v ->
                CommonUtil.navigate(getNavController(), R.id.treeViewDialog,
                        TreeViewDialogDirections.toAddCategoryDialog(parentIndex)));
        return binding;
    }

    private void setTreeView() {
        model.getCategoryTuplesLive(parentIndex).observe(this, categoryTuples ->
                model.getFolderTuplesLive(parentIndex).observe(this, folderTuples ->
                        model.getFolderParentIdTuplesLive(getSelectedFolderIds()).observe(this, folderParentIdTuples ->
                                model.getSizeParentIdTuplesLive(getSelectedSizeIds()).observe(this, sizeParentIdTuples -> {
                                    LinkedList<TreeNode> categoryNodes = treeNodeProvider.makeNodes(categoryTuples, folderTuples);

                                    treeNodeProvider.checkIsParentItemParent(folderParentIdTuples, sizeParentIdTuples);
                                    treeNodeProvider.checkIsSelectedFolder(folderParentIdTuples);
                                    treeNodeProvider.checkIsParentSelectedFolder(folderParentIdTuples);
                                    treeNodeProvider.showCurrentPosition(getCurrentPositionId());

                                    AndroidTreeView treeView = getTreeView(categoryNodes);
                                    binding.treeViewContainer.removeAllViews();
                                    binding.treeViewContainer.addView(treeView.getView());
                                }))));
    }

    @NotNull
    private AndroidTreeView getTreeView(LinkedList<TreeNode> categoryNodes) {
        nodeRoot = TreeNode.root().addChildren(categoryNodes);
        treeView = new AndroidTreeView(requireContext(), nodeRoot);
        treeView.setDefaultAnimation(false);
        treeView.setUseAutoToggle(false);
        treeView.setDefaultNodeClickListener(this);
        return treeView;
    }

    private void observeCategoryInsertIdLive() {
        model.getCategoryInsertIdLive().observe(getViewLifecycleOwner(), insertId -> {
            if (insertId != null) {
                model.setCategoryInsertId(insertId);
                model.getCategoryInsertIdLive().setValue(null);
            }
        });
    }

    private void observeFolderInsertIdLive() {
        model.getFolderInsertIdLive().observe(getViewLifecycleOwner(), insertId -> {
            if (insertId != null) {
                model.setFolderInsertId(insertId);
                model.getCategoryInsertIdLive().setValue(null);
            }
        });
    }

    private void observeCategoryTupleMutable() {
        model.getCategoryTupleMutable().observe(getViewLifecycleOwner(), categoryTuple -> {
            if (categoryTuple != null) {
                TreeNode addedCategoryNode =
                        new TreeNode(new CategoryValue(categoryTuple))
                                .setViewHolder(treeNodeProvider.makeCategoryHolder());
                treeView.addNode(nodeRoot, addedCategoryNode);
                model.getCategoryTupleMutable().setValue(null);
            }
        });
    }

    private void observeFolderTupleMutable() {
        model.getFolderTupleMutable().observe(getViewLifecycleOwner(), folderTuple -> {
            if (folderTuple != null) {
                TreeNode clickedNode = treeNodeProvider.getClickedNode(folderTuple);
                int margin = getMargin(clickedNode);

                TreeNode addedFolderNode = new TreeNode(new FolderValue(folderTuple, margin))
                        .setViewHolder(treeNodeProvider.makeFolderHolder());
                treeView.addNode(clickedNode, addedFolderNode);

                addContentsSize(clickedNode);

                model.getFolderTupleMutable().setValue(null);
            }
        });
    }

    private int getMargin(TreeNode clickedNode) {
        if (clickedNode.getValue() instanceof FolderValue)
            return ((FolderValue) clickedNode.getValue()).getMargin() + getResources().getDimensionPixelSize(R.dimen._8sdp);
        else return treeNodeProvider.getMargin();
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
        TreeCategoryHolder[] categoryHolders = treeNodeProvider.getCategoryHolders();
        Arrays.stream(categoryHolders)
                .filter(categoryHolder -> categoryHolder.getId() == getCurrentPositionId())
                .forEach(categoryHolder -> treeView.expandNode(categoryHolder.getNode()));
    }

    public void expandingFolderNode() {
        Arrays.stream(treeNodeProvider.getFolderHolders())
                .filter(folderHolder ->
                        Arrays.stream(getFolderPathIds())
                                .anyMatch(folderId -> folderHolder.getId() == folderId))
                .forEach(folderHolder -> treeView.expandNode(folderHolder.getNode()));
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TREE_VIEW_STATE, treeView.getSaveState());
    }

    @Override
    public void onClick(@NotNull TreeNode node, Object value) {
        if (((BaseTreeHolder<?>) node.getViewHolder()).isClickable()) {
            TreeViewDialogDirections.ToMoveFolderAndSizeDialog action =
                    TreeViewDialogDirections.toMoveFolderAndSizeDialog(
                            getSelectedFolderIds(), getSelectedSizeIds(), ((BaseTreeHolder<?>) node.getViewHolder()).getId());
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
        binding = null;
    }

    private int getParentIndex() {
        return TreeViewDialogArgs.fromBundle(getArguments()).getParentIndex();
    }

    private long[] getSelectedFolderIds() {
        return TreeViewDialogArgs.fromBundle(getArguments()).getSelectedFolderIds();
    }

    private long[] getSelectedSizeIds() {
        return TreeViewDialogArgs.fromBundle(getArguments()).getSelectedSizeIds();
    }

    private long getCurrentPositionId() {
        return TreeViewDialogArgs.fromBundle(getArguments()).getCurrentPositionId();
    }

    private long[] getFolderPathIds() {
        return TreeViewDialogArgs.fromBundle(getArguments()).getFolderPathIds();
    }
}