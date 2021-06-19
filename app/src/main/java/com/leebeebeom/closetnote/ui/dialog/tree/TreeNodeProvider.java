package com.leebeebeom.closetnote.ui.dialog.tree;

import android.content.Context;

import androidx.navigation.NavController;

import com.leebeebeom.closetnote.data.tuple.ParentIdTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.CategoryTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.FolderTuple;
import com.leebeebeom.closetnote.ui.dialog.tree.holder.BaseTreeHolder;
import com.leebeebeom.closetnote.ui.dialog.tree.holder.TreeCategoryHolder;
import com.leebeebeom.closetnote.ui.dialog.tree.holder.TreeFolderHolder;
import com.unnamed.b.atv.model.TreeNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ActivityContext;
import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class TreeNodeProvider {
    private final Context mContext;
    private final NavController mNavController;
    private LinkedList<TreeNode> mCategoryNodes, mFolderNodes;
    @Getter
    private List<TreeCategoryHolder> mCategoryHolders;
    @Getter
    private List<TreeFolderHolder> mFolderHolders;


    @Inject
    public TreeNodeProvider(@ActivityContext Context context, NavController navController) {
        this.mContext = context;
        this.mNavController = navController;
    }

    public List<TreeNode> makeNodes(List<CategoryTuple> categoryTuples, List<FolderTuple> folderTuples) {
        this.mCategoryNodes = new LinkedList<>();
        categoryTuples.forEach(
                categoryTuple -> mCategoryNodes.add(new TreeNode(new BaseTreeValue.CategoryValue(categoryTuple)).setViewHolder(makeCategoryHolder())));

        this.mFolderNodes = new LinkedList<>();
        folderTuples.forEach(
                folderTuple -> mFolderNodes.add(new TreeNode(new BaseTreeValue.FolderValue(folderTuple)).setViewHolder(makeFolderHolder())));

        addCategoryChild();
        addFolderChild();
        return mCategoryNodes;
    }

    public TreeCategoryHolder makeCategoryHolder() {
        return new TreeCategoryHolder(mContext, mNavController);
    }

    public TreeFolderHolder makeFolderHolder() {
        return new TreeFolderHolder(mContext, mNavController);
    }

    private void addCategoryChild() {
        mCategoryHolders = mCategoryNodes.stream().map(node -> (TreeCategoryHolder) node.getViewHolder()).collect(Collectors.toList());
        mCategoryHolders.forEach(categoryHolder -> categoryHolder.addChild(mFolderNodes));
    }

    private void addFolderChild() {
        mFolderHolders = mFolderNodes.stream().map(node -> (TreeFolderHolder) node.getViewHolder()).collect(Collectors.toList());
        mFolderHolders.forEach(folderHolder -> folderHolder.addChild(mFolderNodes));
    }

    public void checkIsSelectedItemParent(List<ParentIdTuple> parentIdTuples) {
        checkIsSelectedItemParent(parentIdTuples, mCategoryHolders);
        checkIsSelectedItemParent(parentIdTuples, mFolderHolders);
    }

    private <T extends BaseTreeHolder<?>> void checkIsSelectedItemParent(List<ParentIdTuple> parentIdTuples, List<T> holders) {
        parentIdTuples
                .forEach(parentIdTuple ->
                        holders.stream()
                                .filter(holder -> holder.getTupleId() == parentIdTuple.getParentId())
                                .forEach(holder -> holder.setUnClickable()));
    }

    public void checkIsSelectedFolder(List<ParentIdTuple> folderParentIdTuples) {
        folderParentIdTuples
                .forEach(folderParentIdTuple -> {
                    List<TreeFolderHolder> selectedFolderHolders =
                            mFolderHolders.stream()
                                    .filter(folderHolder -> folderHolder.getTupleId() == folderParentIdTuple.getId())
                                    .collect(Collectors.toList());

                    List<BaseTreeHolder<?>> selectedFolderHolderParents =
                            selectedFolderHolders.stream()
                                    .map(BaseTreeHolder::getParentViewHolder)
                                    .collect(Collectors.toList());

                    selectedFolderHolders.forEach(BaseTreeHolder::setUnClickable);
                    selectedFolderHolderParents.forEach(BaseTreeHolder::setUnClickable);
                });
    }

    public void checkIsParentSelectedFolder() {
        mFolderHolders.stream()
                .filter(folderHolder -> {
                    BaseTreeHolder<?> parentViewHolder = folderHolder.getParentViewHolder();
                    if (parentViewHolder instanceof TreeFolderHolder)
                        return !parentViewHolder.isClickable();
                    else return false;
                }).forEach(BaseTreeHolder::setUnClickable);
    }

    public void showCurrentPosition(long currentPositionId) {
        Optional<TreeCategoryHolder> currentPositionHolder =
                mCategoryHolders.stream()
                        .filter(categoryHolder -> categoryHolder.getTupleId() == currentPositionId)
                        .findFirst();

        if (currentPositionHolder.isPresent())
            currentPositionHolder.get().showCurrentPosition();
        else {
            Optional<TreeFolderHolder> currentPositionHolder2 =
                    mFolderHolders.stream()
                            .filter(folderHolder -> folderHolder.getTupleId() == currentPositionId)
                            .findFirst();
            currentPositionHolder2.ifPresent(BaseTreeHolder::showCurrentPosition);
        }
    }
}
