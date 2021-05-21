package com.example.myfit.ui.dialog.tree;

import android.content.Context;

import androidx.navigation.NavController;

import com.example.myfit.R;
import com.example.myfit.data.model.category.CategoryTuple;
import com.example.myfit.data.model.folder.FolderTuple;
import com.example.myfit.data.model.tuple.ParentIdTuple;
import com.example.myfit.ui.dialog.tree.holder.BaseTreeHolder;
import com.example.myfit.ui.dialog.tree.holder.TreeCategoryHolder;
import com.example.myfit.ui.dialog.tree.holder.TreeFolderHolder;
import com.unnamed.b.atv.model.TreeNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class TreeNodeProvider {
    private final Context context;
    private LinkedList<TreeNode> nodes;
    private TreeCategoryHolder[] categoryHolders;
    private TreeFolderHolder[] folderHolders;
    private int margin;
    private List<FolderTuple> folderTuples;

    public TreeNodeProvider(Context context) {
        this.context = context;
    }

    public LinkedList<TreeNode> makeNodes(List<CategoryTuple> categoryTuples, List<FolderTuple> folderTuples, NavController navController) {
        this.folderTuples = folderTuples;
        nodes = new LinkedList<>();
        categoryTuples.forEach(tuple -> nodes.add(new TreeNode(new TreeCategoryHolder.TreeCategoryValue(tuple))
                .setViewHolder(getCategoryHolder(navController))));

        addFolderNodes(navController);
        return nodes;
    }

    private void addFolderNodes(NavController navController) {
        nodes.forEach(categoryNode -> {
            TreeCategoryHolder categoryHolder = (TreeCategoryHolder) categoryNode.getViewHolder();
            folderTuples.stream()
                    .filter(folderTuple -> categoryHolder.getId() == folderTuple.getId())
                    .forEach(folderTuple ->
                            categoryNode.addChild(new TreeNode(new TreeFolderHolder.TreeFolderValue(folderTuple, getMargin()))
                                    .setViewHolder(getFolderHolder(navController))));
        });
    }

    public int getMargin() {
        if (margin == 0) margin = context.getResources().getDimensionPixelSize(R.dimen._12sdp);
        return margin;
    }

    public void checkIsParentItemParent(ParentIdTuple[] folderParentIdTuples, ParentIdTuple[] sizeParentIdTuples) {
        TreeCategoryHolder[] categoryHolders = getCategoryHolders();
        TreeFolderHolder[] folderHolders = getFolderHolders();

        checkIsParentItemParent(folderParentIdTuples, categoryHolders);
        checkIsParentItemParent(sizeParentIdTuples, categoryHolders);
        checkIsParentItemParent(folderParentIdTuples, folderHolders);
        checkIsParentItemParent(sizeParentIdTuples, folderHolders);
    }

    public TreeCategoryHolder[] getCategoryHolders() {
        if (categoryHolders == null)
            categoryHolders = (TreeCategoryHolder[]) nodes.stream()
                    .map(TreeNode::getViewHolder)
                    .toArray();
        return categoryHolders;
    }

    public TreeFolderHolder[] getFolderHolders() {
        if (folderHolders == null) {
            List<TreeNode> topFolderNodes = getTopFolderNodes();
            LinkedList<TreeNode> allFolderNodes = new LinkedList<>();
            addAllFolderNodes(topFolderNodes, allFolderNodes);
            folderHolders = (TreeFolderHolder[]) allFolderNodes.stream().map(TreeNode::getViewHolder).toArray();
        }
        return folderHolders;
    }

    private List<TreeNode> getTopFolderNodes() {
        TreeNode[] categoryNodes = (TreeNode[]) nodes.stream().map(TreeNode::getChildren).flatMap(Collection::parallelStream).toArray();

        return Arrays.stream(categoryNodes)
                .filter(categoryNode -> !categoryNode.getChildren().isEmpty())
                .map(TreeNode::getChildren)
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toList());
    }

    private void addAllFolderNodes(List<TreeNode> topFolderNodes, LinkedList<TreeNode> allFolderNodes) {
        topFolderNodes.stream()
                .filter(folderNode -> !folderNode.getChildren().isEmpty())
                .forEach(folderNode -> {
                    allFolderNodes.addAll(folderNode.getChildren());
                    addAllFolderNodes(folderNode.getChildren(), allFolderNodes);
                });
    }

    private <T extends BaseTreeHolder<?, ?>> void checkIsParentItemParent(ParentIdTuple[] parentIdTuples, T[] holders) {
        Arrays.stream(parentIdTuples).forEach(parentIdTuple -> {
            BaseTreeHolder<?, ?>[] selectedItemParents = (BaseTreeHolder<?, ?>[]) Arrays.stream(holders)
                    .filter(holder -> holder.getId() == parentIdTuple.getParentId()).toArray();
            Arrays.stream(selectedItemParents).forEach(BaseTreeHolder::setAlpha);
        });
    }

    public void checkIsSelectedFolder(ParentIdTuple[] folderParentIdTuples) {
        TreeFolderHolder[] folderHolders = getFolderHolders();
        TreeFolderHolder[] clickableFolderHolders = (TreeFolderHolder[]) Arrays.stream(folderHolders)
                .filter(folderHolder -> !folderHolder.isClickable()).toArray();

        Arrays.stream(folderParentIdTuples).forEach(folderParentIdTuple -> {
            TreeFolderHolder[] selectedFolders = (TreeFolderHolder[]) Arrays.stream(folderHolders)
                    .filter(folderHolder -> folderHolder.getId() == folderParentIdTuple.getId()).toArray();
            BaseTreeHolder<?, ?>[] selectedFolderParents = (BaseTreeHolder<?, ?>[]) Arrays.stream(selectedFolders)
                    .map(folderHolder -> folderHolder.getNode().getParent().getViewHolder()).toArray();

            Arrays.stream(selectedFolders).forEach(BaseTreeHolder::setAlpha);
            Arrays.stream(selectedFolderParents).forEach(BaseTreeHolder::setAlpha);
        });
    }

    public void checkIsParentSelectedFolder(ParentIdTuple[] folderParentTuples) {
        TreeFolderHolder[] folderHolders = getFolderHolders();

        Arrays.stream(folderParentTuples).forEach(folderParentIdTuple ->
                Arrays.stream(folderHolders).filter(folderHolder -> {
                    Object parentHolder = folderHolder.getNode().getParent().getViewHolder();
                    return (parentHolder instanceof TreeCategoryHolder && ((TreeCategoryHolder) parentHolder).getId() == folderParentIdTuple.getId()) ||
                            (parentHolder instanceof TreeFolderHolder && ((TreeFolderHolder) parentHolder).getId() == folderParentIdTuple.getId());
                }).forEach(BaseTreeHolder::setAlpha));

    }


    public TreeCategoryHolder getCategoryHolder(NavController navController) {
        return new TreeCategoryHolder(context, navController);
    }

    public TreeFolderHolder getFolderHolder(NavController navController) {
        return new TreeFolderHolder(context, folderTuples, navController);
    }

    public TreeNode getClickedNode(FolderTuple folderTuple) {
        TreeCategoryHolder[] categoryHolders = getCategoryHolders();
        TreeCategoryHolder[] parentHolder = (TreeCategoryHolder[]) Arrays.stream(categoryHolders)
                .filter(categoryHolder -> categoryHolder.getId() == folderTuple.getParentId()).toArray();
        if (parentHolder.length == 1) {
            return parentHolder[0].getNode();
        } else {
            TreeFolderHolder[] folderHolders = getFolderHolders();
            TreeFolderHolder[] holder = (TreeFolderHolder[]) Arrays.stream(folderHolders)
                    .filter(folderHolder -> folderHolder.getId() == folderTuple.getParentId()).toArray();
            return holder[0].getNode();
        }
    }

    public void showCurrentPosition(long currentPositionId) {
        TreeCategoryHolder[] categoryHolders = getCategoryHolders();
        TreeCategoryHolder[] currentPosition = (TreeCategoryHolder[]) Arrays.stream(categoryHolders)
                .filter(categoryHolder -> categoryHolder.getId() == currentPositionId).toArray();
        if (currentPosition.length == 1) {
            currentPosition[0].showCurrentPosition();
        } else {
            TreeFolderHolder[] folderHolders = getFolderHolders();
            TreeFolderHolder[] holder = (TreeFolderHolder[]) Arrays.stream(folderHolders)
                    .filter(folderHolder -> folderHolder.getId() == currentPositionId).toArray();
            holder[0].showCurrentPosition();
        }
    }
}
