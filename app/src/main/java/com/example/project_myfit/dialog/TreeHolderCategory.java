package com.example.project_myfit.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.project_myfit.R;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.databinding.ItemTreeCategoryBinding;
import com.example.project_myfit.main.list.ListViewModel;
import com.example.project_myfit.util.AdapterUtil;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TreeHolderCategory extends TreeNode.BaseNodeViewHolder<TreeHolderCategory.CategoryTreeHolder> {

    private final TreeViewCategoryFolderAddListener mListener;
    private List<Size> mSelectedSizeList;
    private List<Folder> mSelectedFolderList;
    private List<Long> mFolderFolderIdList, mSizeFolderIdList;
    private ItemTreeCategoryBinding mBinding;
    private boolean mIsClickable = true;
    private final AdapterUtil mAdapterUtil;
    private final ListViewModel mListViewModel;

    public TreeHolderCategory(Context context, TreeViewCategoryFolderAddListener listener, ListViewModel listViewModel) {
        super(context);
        this.mListener = listener;
        this.mListViewModel = listViewModel;
        mAdapterUtil = new AdapterUtil(context);
    }

    public TreeHolderCategory setItems(List<Folder> selectedFolderList, List<Size> selectedSizeList,
                                       List<Long> folderFolderIdList, List<Long> sizeFolderIdList) {
        this.mSelectedFolderList = selectedFolderList;
        this.mSelectedSizeList = selectedSizeList;
        this.mFolderFolderIdList = folderFolderIdList;
        this.mSizeFolderIdList = sizeFolderIdList;
        return this;
    }

    @Override
    public View createNodeView(TreeNode node, @NotNull CategoryTreeHolder value) {
        mBinding = ItemTreeCategoryBinding.inflate(LayoutInflater.from(context));
        mBinding.setCategory(value.category);

        mBinding.contentsSize.setText(String.valueOf(mAdapterUtil.getCategoryContentsSize(value.category,
                mFolderFolderIdList, mSizeFolderIdList)));

        //선택된 폴더가 이 카테고리노드라면
        if (!mSelectedFolderList.isEmpty()) {
            Folder selectedFolder = mSelectedFolderList.get(0);
            if (selectedFolder.getFolderId() == value.category.getId())
                setAlpha();
        }

        //선택된 사이즈가 이 카테고리노드라면
        if (!mSelectedSizeList.isEmpty()) {
            Size selectedSize = mSelectedSizeList.get(0);
            if (selectedSize.getFolderId() == value.category.getId())
                setAlpha();
        }

        //expandable
        if (!node.getChildren().isEmpty())
            mBinding.iconLayout.setOnClickListener(v -> tView.toggleNode(node));
        else mBinding.arrowIconCategory.setVisibility(View.INVISIBLE);

        //currentPosition
        if (mListViewModel != null && mListViewModel.getThisFolder() == null &&
                value.category.getId() == mListViewModel.getThisCategory().getId())
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
