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
    private Category mThisCategory;
    private Folder mThisFolder;

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

        mBinding.contentsSize.setText(String.valueOf(mAdapterUtil.getCategoryContentsSize(value.category,
                mFolderParentIdList, mSizeParentIdList)));

        //선택된 폴더가 이 카테고리노드라면
        if (!mSelectedFolderList.isEmpty())
            for (Folder selectedFolder : mSelectedFolderList)
                if (selectedFolder.getParentId() == value.category.getId()) {
                    setAlpha();
                    break;
                }

        //선택된 사이즈가 이 카테고리노드라면
        if (!mSelectedSizeList.isEmpty())
            for (Size selectedSize : mSelectedSizeList)
                if (selectedSize.getParentId() == value.category.getId()) {
                    setAlpha();
                    break;
                }

        //expandable
        if (!node.getChildren().isEmpty())
            mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(node));
        else mBinding.arrowIconCategory.setVisibility(View.INVISIBLE);

        //currentPosition
        if (mThisFolder == null && mThisCategory.getId() == value.category.getId())
            mBinding.currentPosition.setVisibility(View.VISIBLE);
        else mBinding.currentPosition.setVisibility(View.GONE);

        mBinding.addIcon.setOnClickListener(v -> mListener.treeViewCategoryAddFolderClick(node, value));

        return mBinding.getRoot();
    }

    public void setAlpha() {
        if (mIsClickable) {
            mBinding.iconLayout.setAlpha(0.5f);
            mBinding.text.setAlpha(0.5f);
            mIsClickable = false;
        }
    }

    public boolean isClickable() {
        return mIsClickable;
    }

    public void setIconClickable() {
        mBinding.arrowIconCategory.setVisibility(View.VISIBLE);
        mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(mNode));
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
        mBinding.arrowIconCategory.setImageResource(active ? R.drawable.icon_triangle_down : R.drawable.icon_triangle_right);
        mBinding.folderIconCategory.setImageResource(active ? R.drawable.icon_folder_open : R.drawable.icon_folder);
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
