package com.example.myfit.ui.dialog.tree;

import android.content.Context;

import androidx.navigation.NavController;

import com.example.myfit.R;
import com.example.myfit.data.tuple.ParentIdTuple;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.data.tuple.tuple.FolderTuple;
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
    private final Context mContext;
    private final NavController mNavController;
    private final int mMargin;
    private LinkedList<TreeNode> mCategoryNodes;
    private List<FolderTuple> mFolderTuples;
    private TreeCategoryHolder[] mCategoryHolders;
    private TreeFolderHolder[] mFolderHolders;

    @Inject
    public TreeNodeProvider(Context context, NavController navController) {
        this.mContext = context;
        this.mNavController = navController;
        this.mMargin = context.getResources().getDimensionPixelSize(R.dimen._12sdp);
    }

    public LinkedList<TreeNode> makeNodes(List<CategoryTuple> categoryTuples, List<FolderTuple> folderTuples) {
        this.mFolderTuples = folderTuples;
        this.mCategoryNodes = new LinkedList<>();

        categoryTuples.forEach(
                categoryTuple -> mCategoryNodes.add(new TreeNode(new CategoryValue(categoryTuple)).setViewHolder(makeCategoryHolder())));

        addFolderNodes();

        mCategoryHolders = extractCategoryHolders();
        mFolderHolders = extractFolderHolders();
        return mCategoryNodes;
    }

    public TreeCategoryHolder makeCategoryHolder() {
        return new TreeCategoryHolder(mContext, mNavController);
    }

    private void addFolderNodes() {
        mCategoryNodes.forEach(categoryNode ->
                mFolderTuples.stream()
                        .filter(folderTuple -> {
                            TreeCategoryHolder categoryHolder = (TreeCategoryHolder) categoryNode.getViewHolder();
                            return categoryHolder.getTupleId() == folderTuple.getParentId();
                        })
                        .forEach(folderTuple ->
                                categoryNode.addChild(new TreeNode(new FolderValue(folderTuple, mMargin)).setViewHolder(makeFolderHolder()))));
    }

    public TreeFolderHolder makeFolderHolder() {
        return new TreeFolderHolder(mContext, mFolderTuples, mNavController);
    }

    @NotNull
    private TreeCategoryHolder[] extractCategoryHolders() {
        return (TreeCategoryHolder[]) mCategoryNodes.stream()
                .map(TreeNode::getViewHolder)
                .toArray();
    }

    private TreeFolderHolder[] extractFolderHolders() {
        List<TreeNode> topFolderNodes = extractTopFolderNodes();
        LinkedList<TreeNode> allFolderNodes = new LinkedList<>();
        addAllFolderNodes(topFolderNodes, allFolderNodes);
        mFolderHolders = (TreeFolderHolder[]) allFolderNodes.stream().map(TreeNode::getValue).toArray();
        return mFolderHolders;
    }

    private List<TreeNode> extractTopFolderNodes() {
        return mCategoryNodes.stream()
                .filter(categoryNode -> !categoryNode.getChildren().isEmpty())
                .map(TreeNode::getChildren)
                .flatMap(Collection::parallelStream)
                .collect(Collectors.toList());
    }

    private void addAllFolderNodes(List<TreeNode> topFolderNodes, LinkedList<TreeNode> allFolderNodes) {
        allFolderNodes.addAll(topFolderNodes);
        List<TreeNode> childFolderNodes = topFolderNodes.stream().filter(folderNode -> !folderNode.getChildren().isEmpty()).collect(Collectors.toList());
        if (!childFolderNodes.isEmpty()) addAllFolderNodes(childFolderNodes, allFolderNodes);
    }

    public void checkIsSelectedItemParent(ParentIdTuple[] folderParentIdTuples, ParentIdTuple[] sizeParentIdTuples) {
        checkIsSelectedItemParent(folderParentIdTuples, mCategoryHolders);
        checkIsSelectedItemParent(sizeParentIdTuples, mCategoryHolders);
        checkIsSelectedItemParent(folderParentIdTuples, mFolderHolders);
        checkIsSelectedItemParent(sizeParentIdTuples, mFolderHolders);
    }

    private <T extends BaseTreeHolder<?, ?>> void checkIsSelectedItemParent(ParentIdTuple[] parentIdTuples, T[] holders) {
        Arrays.stream(parentIdTuples)
                .forEach(parentIdTuple ->
                        Arrays.stream(holders)
                                .filter(holder -> holder.getTupleId() == parentIdTuple.getParentId())
                                .forEach(holder -> holder.setUnClickable()));
    }

    public void checkIsSelectedFolder(ParentIdTuple[] folderParentIdTuples) {
        Arrays.stream(folderParentIdTuples)
                .forEach(folderParentIdTuple -> {
                    TreeFolderHolder[] selectedFolderHolders = (TreeFolderHolder[])
                            Arrays.stream(mFolderHolders)
                                    .filter(folderHolder -> folderHolder.getTupleId() == folderParentIdTuple.getId())
                                    .toArray();

                    BaseTreeHolder<?, ?>[] selectedFolderHolderParents = (BaseTreeHolder<?, ?>[])
                            Arrays.stream(selectedFolderHolders)
                                    .map(BaseTreeHolder::getParent)
                                    .toArray();

                    Arrays.stream(selectedFolderHolders).forEach(BaseTreeHolder::setUnClickable);
                    Arrays.stream(selectedFolderHolderParents).forEach(BaseTreeHolder::setUnClickable);
                });
    }

    public void checkIsParentSelectedFolder(ParentIdTuple[] folderParentTuples) {
        Arrays.stream(folderParentTuples)
                .forEach(folderParentIdTuple ->
                        Arrays.stream(mFolderHolders).filter(folderHolder -> {
                            BaseTreeHolder<?, ?> parentHolder = folderHolder.getParent();
                            return (parentHolder instanceof TreeFolderHolder && ((TreeFolderHolder) parentHolder).getTupleId() == folderParentIdTuple.getId());
                        }).forEach(BaseTreeHolder::setUnClickable));
    }

    public void showCurrentPosition(long currentPositionId) {
        Optional<TreeCategoryHolder> currentPositionHolder = Arrays.stream(mCategoryHolders)
                .filter(categoryHolder -> categoryHolder.getTupleId() == currentPositionId)
                .findFirst();
        if (currentPositionHolder.isPresent()) {
            currentPositionHolder.get().showCurrentPosition();
        } else {
            Optional<TreeFolderHolder> currentPositionHolder2 = Arrays.stream(mFolderHolders)
                    .filter(folderHolder -> folderHolder.getTupleId() == currentPositionId)
                    .findFirst();
            currentPositionHolder2.ifPresent(BaseTreeHolder::showCurrentPosition);
        }
    }

    public TreeNode getClickedNode(FolderTuple folderTuple) {
        Optional<TreeCategoryHolder> parentHolder = Arrays.stream(mCategoryHolders)
                .filter(categoryHolder -> categoryHolder.getTupleId() == folderTuple.getParentId())
                .findFirst();

        if (parentHolder.isPresent()) {
            return parentHolder.get().getNode();
        } else {
            Optional<TreeFolderHolder> parentHolder2 = Arrays.stream(mFolderHolders)
                    .filter(folderHolder -> folderHolder.getTupleId() == folderTuple.getParentId())
                    .findFirst();
            return parentHolder2.map(TreeFolderHolder::getNode).orElse(null);
        }
    }

    public int getMargin() {
        return mMargin;
    }

    public TreeCategoryHolder[] getCategoryHolders() {
        return mCategoryHolders;
    }

    public TreeFolderHolder[] getFolderHolders() {
        return mFolderHolders;
    }
}
