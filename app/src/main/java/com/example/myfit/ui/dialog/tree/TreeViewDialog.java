package com.example.myfit.ui.dialog.tree;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;

import com.example.myfit.R;
import com.example.myfit.databinding.LayoutDialogTreeBinding;
import com.example.myfit.ui.dialog.BaseDialog;
import com.example.myfit.ui.dialog.tree.holder.TreeCategoryHolder;
import com.example.myfit.ui.dialog.tree.holder.TreeFolderHolder;
import com.example.myfit.util.CommonUtil;
import com.example.myfit.util.constant.ParentCategory;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.example.myfit.util.MyFitConstant.TREE_VIEW_STATE;

@AndroidEntryPoint
public class TreeViewDialog extends BaseDialog implements TreeNode.TreeNodeClickListener {
    @Inject
    TreeNodeProvider treeNodeProvider;
    private int parentIndex;
    private TreeViewModel model;
    private TreeNode nodeRoot;
    private AndroidTreeView treeView;
    private LayoutDialogTreeBinding binding;
    private long[] selectedFolderIds, selectedSizeIds;
    private long currentPositionId;
    private long[] folderPathIds;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentIndex = TreeViewDialogArgs.fromBundle(getArguments()).getParentIndex();
        selectedFolderIds = TreeViewDialogArgs.fromBundle(getArguments()).getSelectedFolderIds();
        selectedSizeIds = TreeViewDialogArgs.fromBundle(getArguments()).getSelectedSizeIds();
        currentPositionId = TreeViewDialogArgs.fromBundle(getArguments()).getCurrentPositionId();
        folderPathIds = TreeViewDialogArgs.fromBundle(getArguments()).getFolderPathIds();
        model = new ViewModelProvider(this).get(TreeViewModel.class);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog alertDialog = getAlertDialog();
        setTreeView();

        observeCategoryInsertIdLive();
        observeFolderInsertIdLive();
        observeCategoryTupleMutable();
        observeFolderTupleMutable();

        if (savedInstanceState == null && currentPositionId != 0)
            expandingNode();
        else if (savedInstanceState != null)
            treeView.restoreState(savedInstanceState.getString(TREE_VIEW_STATE));
        return alertDialog;
    }

    @NotNull
    @Override
    protected AlertDialog getAlertDialog() {
        return dialogBuilder
                .setTitle(getString(R.string.tree_title))
                .setView(getBinding().getRoot())
                .create();
    }

    private void setTreeView() {
        model.getCategoryTuplesLive(parentIndex).observe(this, categoryTuples ->
                model.getFolderTuplesLive(parentIndex).observe(this, folderTuples ->
                        model.getFolderParentIdTuplesLive(selectedFolderIds).observe(this, folderParentIdTuples ->
                                model.getSizeParentIdTuplesLive(selectedSizeIds).observe(this, sizeParentIdTuples -> {
                                    LinkedList<TreeNode> nodes = treeNodeProvider.makeNodes(categoryTuples, folderTuples, getNavController());

                                    treeNodeProvider.checkIsParentItemParent(folderParentIdTuples, sizeParentIdTuples);
                                    treeNodeProvider.checkIsSelectedFolder(folderParentIdTuples);
                                    treeNodeProvider.checkIsParentSelectedFolder(folderParentIdTuples);
                                    treeNodeProvider.showCurrentPosition(currentPositionId);

                                    AndroidTreeView treeView = getTreeView(nodes);
                                    binding.treeViewContainer.removeAllViews();
                                    binding.treeViewContainer.addView(treeView.getView());
                                }))));
    }

    @NotNull
    private AndroidTreeView getTreeView(LinkedList<TreeNode> nodes) {
        nodeRoot = TreeNode.root().addChildren(nodes);
        treeView = new AndroidTreeView(requireContext(), nodeRoot);
        treeView.setDefaultAnimation(false);
        treeView.setUseAutoToggle(false);
        treeView.setDefaultNodeClickListener(this);
        return treeView;
    }

    private LayoutDialogTreeBinding getBinding() {
        binding = LayoutDialogTreeBinding.inflate(getLayoutInflater());
        binding.setParentCategory(getParentCategory());
        binding.layoutAddCategory.setOnClickListener(v ->
                CommonUtil.navigate(getNavController(), R.id.treeViewDialog,
                        TreeViewDialogDirections.toAddCategoryDialog(parentIndex)));
        return binding;
    }

    private String getParentCategory() {
        switch (parentIndex) {
            case 0:
                return ParentCategory.TOP.name();
            case 1:
                return ParentCategory.BOTTOM.name();
            case 2:
                return ParentCategory.OUTER.name();
            default:
                return ParentCategory.ETC.name();
        }
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
                        new TreeNode(new TreeCategoryHolder.TreeCategoryValue(categoryTuple))
                                .setViewHolder(treeNodeProvider.getCategoryHolder(getNavController()));
                treeView.addNode(nodeRoot, addedCategoryNode);
                model.getCategoryTupleMutable().setValue(null);
            }
        });
    }

    private void observeFolderTupleMutable() {
        model.getFolderTupleMutable().observe(getViewLifecycleOwner(), folderTuple -> {
            if (folderTuple != null) {
                TreeNode clickedNode = treeNodeProvider.getClickedNode(folderTuple);
                int margin;
                if (clickedNode.getViewHolder() instanceof TreeFolderHolder)
                    margin = ((TreeFolderHolder) clickedNode.getViewHolder()).getMargin() + getResources().getDimensionPixelSize(R.dimen._8sdp);
                else margin = treeNodeProvider.getMargin();

                TreeNode addedFolderNode = new TreeNode(new TreeFolderHolder.TreeFolderValue(folderTuple, margin))
                        .setViewHolder(treeNodeProvider.getFolderHolder(getNavController()));
                treeView.addNode(clickedNode, addedFolderNode);
                model.getFolderTupleMutable().setValue(null);
            }
        });
    }

    private void expandingNode() {
        expandCategoryNode();
        expandingFolderNode();
    }

    private void expandCategoryNode() {
        TreeCategoryHolder[] categoryHolders = treeNodeProvider.getCategoryHolders();
        Arrays.stream(categoryHolders)
                .filter(categoryHolder -> categoryHolder.getId() == currentPositionId)
                .forEach(categoryHolder -> treeView.expandNode(categoryHolder.getNode()));
    }

    public void expandingFolderNode() {
        Arrays.stream(treeNodeProvider.getFolderHolders())
                .filter(folderHolder -> Arrays.stream(folderPathIds).anyMatch(folderId -> folderHolder.getId() == folderId))
                .forEach(folderHolder -> treeView.expandNode(folderHolder.getNode()));
    }

    @Override
    public void onSaveInstanceState(@NonNull @NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TREE_VIEW_STATE, treeView.getSaveState());
    }

    @Override
    public void onClick(@NotNull TreeNode node, Object value) {
        if (node.getViewHolder() instanceof TreeCategoryHolder && ((TreeCategoryHolder) node.getViewHolder()).isClickable()) {
            CommonUtil.navigate(getNavController(), R.id.treeViewDialog,
                    TreeViewDialogDirections.toMoveFolderAndSizeDialog(selectedFolderIds, selectedSizeIds,
                            ((TreeCategoryHolder) node.getViewHolder()).getId()));
        } else if (node.getViewHolder() instanceof TreeFolderHolder && ((TreeFolderHolder) node.getViewHolder()).isClickable())
            CommonUtil.navigate(getNavController(), R.id.treeViewDialog,
                    TreeViewDialogDirections.toMoveFolderAndSizeDialog(
                            selectedFolderIds, selectedSizeIds, ((TreeFolderHolder) node.getViewHolder()).getId()));
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return null;
    }

    @Override
    protected NavBackStackEntry getBackStack() {
        return getNavController().getBackStackEntry(R.id.treeViewDialog);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}