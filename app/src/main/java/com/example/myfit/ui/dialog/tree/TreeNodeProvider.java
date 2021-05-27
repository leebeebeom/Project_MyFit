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

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class TreeNodeProvider {
    private final Context context;
    private final NavController navController;
    private final int margin;
    private LinkedList<TreeNode> categoryNodes;
    private List<FolderTuple> folderTuples;
    private TreeCategoryHolder[] categoryHolders;
    private TreeFolderHolder[] folderHolders;

    @Inject
    public TreeNodeProvider(Context context, NavController navController) {
        this.context = context;
        this.navController = navController;
        this.margin = context.getResources().getDimensionPixelSize(R.dimen._12sdp);
    }

    public LinkedList<TreeNode> makeNodes(List<CategoryTuple> categoryTuples, List<FolderTuple> folderTuples) {
        this.folderTuples = folderTuples;
        this.categoryNodes = new LinkedList<>();

        categoryTuples.forEach(
                categoryTuple -> categoryNodes.add(new TreeNode(new CategoryValue(categoryTuple)).setViewHolder(makeCategoryHolder())));

        addFolderNodes();

        categoryHolders = extractCategoryHolders();
        folderHolders = extractFolderHolders();
        return categoryNodes;
    }

    public TreeCategoryHolder makeCategoryHolder() {
        return new TreeCategoryHolder(context, navController);
    }

    private void addFolderNodes() {
        categoryNodes.forEach(categoryNode ->
                folderTuples.stream()
                        .filter(folderTuple -> {
                            TreeCategoryHolder categoryHolder = (TreeCategoryHolder) categoryNode.getViewHolder();
                            return categoryHolder.getId() == folderTuple.getParentId();
                        })
                        .forEach(folderTuple ->
                                categoryNode.addChild(new TreeNode(new FolderValue(folderTuple, margin)).setViewHolder(makeFolderHolder()))));
    }

    public TreeFolderHolder makeFolderHolder() {
        return new TreeFolderHolder(context, folderTuples, navController);
    }

    @NotNull
    private TreeCategoryHolder[] extractCategoryHolders() {
        return (TreeCategoryHolder[]) categoryNodes.stream()
                .map(TreeNode::getViewHolder)
                .toArray();
    }

    private TreeFolderHolder[] extractFolderHolders() {
        List<TreeNode> topFolderNodes = extractTopFolderNodes();
        LinkedList<TreeNode> allFolderNodes = new LinkedList<>();
        addAllFolderNodes(topFolderNodes, allFolderNodes);
        folderHolders = (TreeFolderHolder[]) allFolderNodes.stream().map(TreeNode::getValue).toArray();
        return folderHolders;
    }

    private List<TreeNode> extractTopFolderNodes() {
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

    public void checkIsSelectedItemParent(ParentIdTuple[] folderParentIdTuples, ParentIdTuple[] sizeParentIdTuples) {
        checkIsSelectedItemParent(folderParentIdTuples, categoryHolders);
        checkIsSelectedItemParent(sizeParentIdTuples, categoryHolders);
        checkIsSelectedItemParent(folderParentIdTuples, folderHolders);
        checkIsSelectedItemParent(sizeParentIdTuples, folderHolders);
    }

    private <T extends BaseTreeHolder<?>> void checkIsSelectedItemParent(ParentIdTuple[] parentIdTuples, T[] holders) {
        Arrays.stream(parentIdTuples)
                .forEach(parentIdTuple ->
                        Arrays.stream(holders)
                                .filter(holder -> holder.getId() == parentIdTuple.getParentId())
                                .forEach(holder -> holder.setAlpha()));
    }

    public void checkIsSelectedFolder(ParentIdTuple[] folderParentIdTuples) {
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
        Arrays.stream(folderParentTuples)
                .forEach(folderParentIdTuple ->
                        Arrays.stream(folderHolders).filter(folderHolder -> {
                            BaseTreeHolder<?> parentHolder = folderHolder.getParent();
                            return (parentHolder instanceof TreeCategoryHolder && ((TreeCategoryHolder) parentHolder).getId() == folderParentIdTuple.getId()) ||
                                    (parentHolder instanceof TreeFolderHolder && ((TreeFolderHolder) parentHolder).getId() == folderParentIdTuple.getId());
                        }).forEach(BaseTreeHolder::setAlpha));
    }

    public void showCurrentPosition(long currentPositionId) {
        Optional<TreeCategoryHolder> currentPositionHolder = Arrays.stream(categoryHolders)
                .filter(categoryHolder -> categoryHolder.getId() == currentPositionId)
                .findFirst();
        if (currentPositionHolder.isPresent()) {
            currentPositionHolder.get().showCurrentPosition();
        } else {
            Optional<TreeFolderHolder> currentPositionHolder2 = Arrays.stream(folderHolders)
                    .filter(folderHolder -> folderHolder.getId() == currentPositionId)
                    .findFirst();
            currentPositionHolder2.ifPresent(BaseTreeHolder::showCurrentPosition);
        }
    }

    public TreeNode getClickedNode(FolderTuple folderTuple) {
        Optional<TreeCategoryHolder> parentHolder = Arrays.stream(categoryHolders)
                .filter(categoryHolder -> categoryHolder.getId() == folderTuple.getParentId())
                .findFirst();

        if (parentHolder.isPresent()) {
            return parentHolder.get().getNode();
        } else {
            Optional<TreeFolderHolder> parentHolder2 = Arrays.stream(folderHolders)
                    .filter(folderHolder -> folderHolder.getId() == folderTuple.getParentId())
                    .findFirst();
            return parentHolder2.map(TreeFolderHolder::getNode).orElse(null);
        }
    }

    public int getMargin() {
        return margin;
    }

    public TreeCategoryHolder[] getCategoryHolders() {
        return categoryHolders;
    }

    public TreeFolderHolder[] getFolderHolders() {
        return folderHolders;
    }
}
