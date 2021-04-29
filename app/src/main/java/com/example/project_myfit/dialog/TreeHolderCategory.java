package com.example.project_myfit.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemTreeCategoryBinding;
import com.example.project_myfit.util.adapter.AdapterUtil;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreeHolderCategory extends TreeNode.BaseNodeViewHolder<TreeHolderCategory.CategoryTreeHolder> {

    private final TreeViewCategoryFolderAddListener mListener;
    private List<Size> mSelectedSizeList;
    private List<Folder> mSelectedFolderList;
    private List<Long> mFolderParentIdList, mSizeParentIdList;
    private ItemTreeCategoryBinding mBinding;
    private boolean mIsClickable = true;
    private final AdapterUtil mAdapterUtil;
    private final Category mThisCategory;
    private final Folder mThisFolder;

    public TreeHolderCategory(Context context, TreeViewCategoryFolderAddListener listener, Category thisCategory, Folder thisFolder) {
        super(context);
        this.mListener = listener;
        this.mThisCategory = thisCategory;
        this.mThisFolder = thisFolder;
        mAdapterUtil = new AdapterUtil(context);
    }

    public TreeHolderCategory setItems(List<Folder> selectedFolderList, List<Size> selectedSizeList,
                                       List<Long> folderParentIdList, List<Long> sizeParentIdList) {
        this.mSelectedFolderList = selectedFolderList;
        this.mSelectedSizeList = selectedSizeList;
        this.mFolderParentIdList = folderParentIdList;
        this.mSizeParentIdList = sizeParentIdList;
        return this;
    }

    @Override
    public View createNodeView(TreeNode node, @NotNull CategoryTreeHolder value) {
        mBinding = ItemTreeCategoryBinding.inflate(LayoutInflater.from(context));
        mBinding.setCategory(value.category);

        mBinding.tvItemTreeCategoryContentsSize.setText(String.valueOf(mAdapterUtil.getContentsSize(value.category.getId(),
                mFolderParentIdList, mSizeParentIdList)));

        selectedFolderCheck(value);
        selectedSizeCheck(value);

        setExpandable(node);
        setCurrentPosition(value);

        mBinding.iconItemTreeCategoryAdd.setOnClickListener(v -> mListener.treeViewCategoryAddFolderClick(node, value));

        return mBinding.getRoot();
    }

    private void selectedSizeCheck(@NotNull CategoryTreeHolder value) {
        if (!mSelectedSizeList.isEmpty())
            for (Size selectedSize : mSelectedSizeList)
                if (selectedSize.getParentId() == value.category.getId()) {
                    setAlpha();
                    break;
                }
    }

    private void selectedFolderCheck(@NotNull CategoryTreeHolder value) {
        if (!mSelectedFolderList.isEmpty())
            for (Folder selectedFolder : mSelectedFolderList)
                if (selectedFolder.getParentId() == value.category.getId()) {
                    setAlpha();
                    break;
                }
    }

    public void setAlpha() {
        if (mIsClickable) {
            mBinding.iconItemTreeCategoryFolderLayout.setAlpha(0.5f);
            mBinding.tvItemTreeCategoryCategory.setAlpha(0.5f);
            mIsClickable = false;
        }
    }

    private void setExpandable(@NotNull TreeNode node) {
        if (!node.getChildren().isEmpty())
            mBinding.iconItemTreeCategoryFolderLayout.setOnClickListener(v -> tView.toggleNode(node));
        else mBinding.iconItemTreeCategoryArrow.setVisibility(View.INVISIBLE);
    }

    private void setCurrentPosition(@NotNull CategoryTreeHolder value) {
        if (mThisFolder == null && mThisCategory != null && mThisCategory.getId() == value.category.getId())
            mBinding.tvItemTreeCategoryCurrentPosition.setVisibility(View.VISIBLE);
        else mBinding.tvItemTreeCategoryCurrentPosition.setVisibility(View.GONE);
    }

    public boolean isClickable() {
        return mIsClickable;
    }

    public void setIconClickable() {
        mBinding.iconItemTreeCategoryArrow.setVisibility(View.VISIBLE);
        mBinding.iconItemTreeCategoryFolderLayout.setOnClickListener(v -> tView.toggleNode(mNode));
    }

    public ItemTreeCategoryBinding getBinding() {
        return mBinding;
    }

    public Category getCategory() {
        return ((CategoryTreeHolder) mNode.getValue()).category;
    }

    public long getCategoryId() {
        return ((CategoryTreeHolder) mNode.getValue()).category.getId();
    }

    @Override
    public void toggle(boolean active) {
        mBinding.iconItemTreeCategoryArrow.setImageResource(active ? R.drawable.icon_triangle_down : R.drawable.icon_triangle_right);
        mBinding.iconItemTreeCategoryFolder.setImageResource(active ? R.drawable.icon_folder_open : R.drawable.icon_folder);
    }

    public static class CategoryTreeHolder {
        private final Category category;

        public CategoryTreeHolder(Category category) {
            this.category = category;
        }
    }

    public interface TreeViewCategoryFolderAddListener {
        void treeViewCategoryAddFolderClick(TreeNode node, TreeHolderCategory.CategoryTreeHolder value);
    }
}
