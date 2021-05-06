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

public class TreeHolderCategory extends TreeNode.BaseNodeViewHolder<TreeHolderCategory.CategoryTreeValue> {

    private final TreeHolderListener mListener;
    private List<Size> mSelectedSizeList;
    private List<Folder> mSelectedFolderList;
    private List<Long> mFolderParentIdList, mSizeParentIdList;
    private ItemTreeCategoryBinding mBinding;
    private boolean mIsClickable = true;
    private final AdapterUtil mAdapterUtil;
    private final Category mThisCategory;
    private final Folder mThisFolder;

    public TreeHolderCategory(Context context, TreeHolderListener listener, Category thisCategory, Folder thisFolder) {
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
    public View createNodeView(TreeNode node, @NotNull CategoryTreeValue value) {
        mBinding = ItemTreeCategoryBinding.inflate(LayoutInflater.from(context));
        mBinding.setCategory(value.category);

        mBinding.tvContentsSize.setText(String.valueOf(mAdapterUtil.getContentsSize(value.category.getId(),
                mFolderParentIdList, mSizeParentIdList)));

        isSelectedFolderPosition(value);
        isSelectedSizePosition(value);

        if (!node.getChildren().isEmpty())
            mBinding.layoutFolderIcon.setOnClickListener(v -> tView.toggleNode(node));
        else mBinding.iconArrow.setVisibility(View.INVISIBLE);

        if (mThisFolder == null && mThisCategory != null && mThisCategory.getId() == value.category.getId())
            mBinding.tvCurrentPosition.setVisibility(View.VISIBLE);
        else mBinding.tvCurrentPosition.setVisibility(View.GONE);

        mBinding.iconAdd.setOnClickListener(v -> mListener.addFolderIconClick(node, value.category.getId()));

        return mBinding.getRoot();
    }

    private void isSelectedFolderPosition(@NotNull CategoryTreeValue value) {
        if (!mSelectedFolderList.isEmpty())
            for (Folder selectedFolder : mSelectedFolderList)
                if (selectedFolder.getParentId() == value.category.getId()) {
                    setAlpha();
                    break;
                }
    }

    private void isSelectedSizePosition(@NotNull CategoryTreeValue value) {
        if (!mSelectedSizeList.isEmpty())
            for (Size selectedSize : mSelectedSizeList)
                if (selectedSize.getParentId() == value.category.getId()) {
                    setAlpha();
                    break;
                }
    }

    public void setAlpha() {
        if (mIsClickable) {
            mBinding.layoutFolderIcon.setAlpha(0.5f);
            mBinding.tvCategoryName.setAlpha(0.5f);
            mIsClickable = false;
        }
    }

    public boolean isClickable() {
        return mIsClickable;
    }

    public void setIconClickable() {
        mBinding.iconArrow.setVisibility(View.VISIBLE);
        mBinding.layoutFolderIcon.setOnClickListener(v -> tView.toggleNode(mNode));
    }

    public ItemTreeCategoryBinding getBinding() {
        return mBinding;
    }

    public Category getCategory() {
        return ((CategoryTreeValue) mNode.getValue()).category;
    }

    public long getCategoryId() {
        return ((CategoryTreeValue) mNode.getValue()).category.getId();
    }

    @Override
    public void toggle(boolean active) {
        mBinding.iconArrow.setImageResource(active ? R.drawable.icon_triangle_down : R.drawable.icon_triangle_right);
        mBinding.iconFolder.setImageResource(active ? R.drawable.icon_folder_open : R.drawable.icon_folder);
    }

    public static class CategoryTreeValue {
        private final Category category;

        public CategoryTreeValue(Category category) {
            this.category = category;
        }
    }
}
