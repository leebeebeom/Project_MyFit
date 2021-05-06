package com.example.project_myfit.dialog;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.project_myfit.R;
import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.util.Sort;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.util.MyFitConstant.SORT_LIST;
import static com.example.project_myfit.util.MyFitConstant.SORT_MAIN;

public class TreeViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private String mParentCategory;
    private List<Folder> mFolderList, mSelectedFolderList;
    private List<Size> mSelectedSizeList;
    private List<Category> mCategoryList;
    private List<Long> mFolderParentIdList, mSizeParentIdList;
    private TreeNode mClickedNode;
    private int mMargin;

    public TreeViewModel(@NonNull @NotNull Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    public TreeHolderCategory setCategoryViewHolderItem(@NotNull TreeHolderCategory treeHolderCategory) {
        return treeHolderCategory.setItems(mSelectedFolderList, mSelectedSizeList,
                getFolderParentIdList(), getSizeParentIdList());
    }

    public TreeHolderFolder setFolderViewHolderItem(@NotNull TreeHolderFolder treeHolderFolder) {
        return treeHolderFolder.setItems(mSelectedFolderList, mSelectedSizeList,
                getFolderList(), getFolderParentIdList(), getSizeParentIdList());
    }

    public TreeNode findCategoryClickedNode(@NotNull TreeNode nodeRoot, TreeHolderCategory categoryViewHolder) {
        for (TreeNode newNode : nodeRoot.getChildren()) {
            TreeHolderCategory newCategoryViewHolder = (TreeHolderCategory) newNode.getViewHolder();
            if (categoryViewHolder.getCategoryId() == newCategoryViewHolder.getCategoryId()) {
                mClickedNode = newNode;
                break;
            }
        }
        return mClickedNode;
    }

    public TreeNode findFolderClickedNode(TreeNode nodeRoot, TreeHolderFolder folderViewHolder) {
        for (TreeNode folderNode : getAllFolderNode(nodeRoot)) {
            TreeHolderFolder newFolderViewHolder = (TreeHolderFolder) folderNode.getViewHolder();
            if (folderViewHolder.getFolderId() == newFolderViewHolder.getFolderId()) {
                mClickedNode = folderNode;
                break;
            }
        }
        return mClickedNode;
    }

    @NotNull
    private List<TreeNode> getAllFolderNode(@NotNull TreeNode nodeRoot) {
        List<TreeNode> topFolderNodeList = getTopFolderNode(nodeRoot);
        List<TreeNode> allFolderNodeList = new ArrayList<>(topFolderNodeList);
        findAllFolderNode(topFolderNodeList, allFolderNodeList);
        return allFolderNodeList;
    }

    @NotNull
    private List<TreeNode> getTopFolderNode(@NotNull TreeNode nodeRoot) {
        List<TreeNode> topFolderNodeList = new ArrayList<>();
        for (TreeNode categoryNode : nodeRoot.getChildren())
            if (!categoryNode.getChildren().isEmpty())
                topFolderNodeList.addAll(categoryNode.getChildren());
        return topFolderNodeList;
    }

    private void findAllFolderNode(@NotNull List<TreeNode> topFolderNodeList, List<TreeNode> allFolderNodeList) {
        for (TreeNode topFolderNode : topFolderNodeList)
            if (!topFolderNode.getChildren().isEmpty()) {
                allFolderNodeList.addAll(topFolderNode.getChildren());
                findAllFolderNode(topFolderNode.getChildren(), allFolderNodeList);
            }
    }

    public void setResourcesNull() {
        //for new category, new folder node
        mCategoryList = null;
        mFolderList = null;
        mFolderParentIdList = null;
        mSizeParentIdList = null;
    }

    public int getMargin() {
        if (mMargin == 0)
            mMargin = (int) getApplication().getResources().getDimension(R.dimen._12sdp);
        return mMargin;
    }

    public void setSelectedItemList(List<Folder> selectedFolderList, List<Size> selectedSizeList) {
        this.mSelectedFolderList = selectedFolderList;
        this.mSelectedSizeList = selectedSizeList;
    }

    public void setParentCategory(String mParentCategory) {
        this.mParentCategory = mParentCategory;
    }

    public String getParentCategory() {
        return mParentCategory;
    }

    public List<Category> getCategoryList() {
        if (mCategoryList == null) {
            int mainSort = getApplication().getSharedPreferences(SORT_MAIN, Context.MODE_PRIVATE).getInt(SORT_MAIN, SORT_CUSTOM);
            mCategoryList = Sort.categorySort(mainSort, mRepository.getCategoryRepository().getCategoryList(mParentCategory, false));
        }
        return mCategoryList;
    }

    public List<Folder> getFolderList() {
        if (mFolderList == null) {
            int folderSort = getApplication().getSharedPreferences(SORT_LIST, Context.MODE_PRIVATE).getInt(SORT_LIST, SORT_CUSTOM);
            mFolderList = Sort.folderSort(folderSort, mRepository.getFolderRepository().getFolderList(mParentCategory, false, false));
        }
        return mFolderList;
    }

    public int getSelectedItemSize() {
        return mSelectedFolderList.size() + mSelectedSizeList.size();
    }

    public void setClickedNode(TreeNode node) {
        mClickedNode = node;
    }

    public TreeNode getClickedNode() {
        return mClickedNode;
    }

    private List<Long> getFolderParentIdList() {
        if (mFolderParentIdList == null)
            mFolderParentIdList = mRepository.getFolderRepository().getFolderParentIdList(mParentCategory, false, false);
        return mFolderParentIdList;
    }

    private List<Long> getSizeParentIdList() {
        if (mSizeParentIdList == null)
            mSizeParentIdList = mRepository.getSizeRepository().getSizeParentIdList(mParentCategory, false, false);
        return mSizeParentIdList;
    }
}
