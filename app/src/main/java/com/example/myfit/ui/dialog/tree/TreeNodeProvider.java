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
import com.example.myfit.ui.dialog.tree.holder.value.CategoryValue;
import com.example.myfit.ui.dialog.tree.holder.value.FolderValue;
import com.unnamed.b.atv.model.TreeNode;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TreeNodeProvider {
    private final Context context;
    private LinkedList<TreeNode> categoryNodes;
    private final int margin;
    private List<FolderTuple> folderTuples;
    private TreeCategoryHolder[] categoryHolders;
    private TreeFolderHolder[] folderHolders;

    public TreeNodeProvider(Context context) {
        this.context = context;
        this.margin = context.getResources().getDimensionPixelSize(R.dimen._12sdp);
    }

    public LinkedList<TreeNode> makeNodes(List<CategoryTuple> categoryTuples, List<FolderTuple> folderTuples, NavController navController) {
        this.folderTuples = folderTuples;
        categoryNodes = new LinkedList<>();

        categoryTuples.forEach(
                categoryTuple -> categoryNodes.add(new TreeNode(new CategoryValue(categoryTuple)).setViewHolder(makeCategoryHolder(navController))));

        addFolderNodes(navController);
        return categoryNodes;
    }

    public TreeCategoryHolder makeCategoryHolder(NavController navController) {
        return new TreeCategoryHolder(context, navController);
    }

    private void addFolderNodes(NavController navController) {
        categoryNodes.forEach(categoryNode ->
                folderTuples.stream()
                        .filter(folderTuple -> {
                            TreeCategoryHolder categoryHolder = (TreeCategoryHolder) categoryNode.getViewHolder();
                            return categoryHolder.getId() == folderTuple.getParentId();
                        })
                        .forEach(folderTuple ->
                                categoryNode.addChild(new TreeNode(new FolderValue(folderTuple, margin)).setViewHolder(makeFolderHolder(navController)))));
    }

    public TreeFolderHolder makeFolderHolder(NavController navController) {
        return new TreeFolderHolder(context, folderTuples, navController);
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
        if (categoryHolders == null) {
            categoryHolders = (TreeCategoryHolder[]) categoryNodes.stream()
                    .map(TreeNode::getViewHolder)
                    .toArray();
        }
        return categoryHolders;
    }

    public TreeFolderHolder[] getFolderHolders() {
        if (folderHolders == null) {
            List<TreeNode> topFolderNodes = getTopFolderNodes();
            LinkedList<TreeNode> allFolderNodes = new LinkedList<>();
            addAllFolderNodes(topFolderNodes, allFolderNodes);
            folderHolders = (TreeFolderHolder[]) allFolderNodes.stream().map(TreeNode::getValue).toArray();
        }
        return folderHolders;
    }

    private List<TreeNode> getTopFolderNodes() {
        return categoryNodes.stream()
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

    private <T extends BaseTreeHolder<?>> void checkIsParentItemParent(ParentIdTuple[] parentIdTuples, T[] holders) {
        Arrays.stream(parentIdTuples)
                .forEach(parentIdTuple ->
                        Arrays.stream(holders)
                                .filter(holder -> holder.getId() == parentIdTuple.getParentId())
                                .forEach(holder -> holder.setAlpha()));
    }

    public void checkIsSelectedFolder(ParentIdTuple[] folderParentIdTuples) {
        TreeFolderHolder[] folderHolders = getFolderHolders();

        Arrays.stream(folderParentIdTuples)
                .forEach(folderParentIdTuple -> {
                    TreeFolderHolder[] selectedFolderHolders = (TreeFolderHolder[])
                            Arrays.stream(folderHolders)
                                    .filter(folderHolder -> folderHolder.getId() == folderParentIdTuple.getId())
                                    .toArray();

                    BaseTreeHolder<?>[] selectedFolderHolderParents = (BaseTreeHolder<?>[])
                            Arrays.stream(selectedFolderHolders)
                                    .map(BaseTreeHolder::getParent)
                                    .toArray();

                    Arrays.stream(selectedFolderHolders).forEach(BaseTreeHolder::setAlpha);
                    Arrays.stream(selectedFolderHolderParents).forEach(BaseTreeHolder::setAlpha);
                });
    }

    public void checkIsParentSelectedFolder(ParentIdTuple[] folderParentTuples) {
        TreeFolderHolder[] folderHolders = getFolderHolders();

        Arrays.stream(folderParentTuples)
                .forEach(folderParentIdTuple ->
                        Arrays.stream(folderHolders).filter(folderHolder -> {
                            BaseTreeHolder<?> parentHolder = folderHolder.getParent();
                            return (parentHolder instanceof TreeCategoryHolder && ((TreeCategoryHolder) parentHolder).getId() == folderParentIdTuple.getId()) ||
                                    (parentHolder instanceof TreeFolderHolder && ((TreeFolderHolder) parentHolder).getId() == folderParentIdTuple.getId());
                        }).forEach(BaseTreeHolder::setAlpha));
    }

    public void showCurrentPosition(long currentPositionId) {
        TreeCategoryHolder[] categoryHolders = getCategoryHolders();
        Optional<TreeCategoryHolder> currentPositionHolder = Arrays.stream(categoryHolders)
                .filter(categoryHolder -> categoryHolder.getId() == currentPositionId).findFirst();
        if (currentPositionHolder.isPresent()) {
            currentPositionHolder.get().showCurrentPosition();
        } else {
            TreeFolderHolder[] folderHolders = getFolderHolders();
            Optional<TreeFolderHolder> currentPositionHolder2 = Arrays.stream(folderHolders)
                    .filter(folderHolder -> folderHolder.getId() == currentPositionId).findFirst();
            currentPositionHolder2.ifPresent(BaseTreeHolder::showCurrentPosition);
        }
    }

    public TreeNode getClickedNode(FolderTuple folderTuple) {
        TreeCategoryHolder[] categoryHolders = getCategoryHolders();

        Optional<TreeCategoryHolder> parentHolder = Arrays.stream(categoryHolders)
                .filter(categoryHolder -> categoryHolder.getId() == folderTuple.getParentId())
                .findFirst();

        if (parentHolder.isPresent()) {
            return parentHolder.get().getNode();
        } else {
            TreeFolderHolder[] folderHolders = getFolderHolders();
            Optional<TreeFolderHolder> parentHolder2 = Arrays.stream(folderHolders)
                    .filter(folderHolder -> folderHolder.getId() == folderTuple.getParentId()).findFirst();
            return parentHolder2.map(TreeFolderHolder::getNode).orElse(null);
        }
    }

    public int getMargin() {
        return margin;
    }
}
