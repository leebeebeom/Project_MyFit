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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.util.MyFitConstant.SORT_LIST;
import static com.example.project_myfit.util.MyFitConstant.SORT_MAIN;

public class TreeViewModel extends AndroidViewModel {
    private final Repository.CategoryRepository mCategoryRepository;
    private final Repository.FolderRepository mFolderRepository;
    private String mParentCategory;
    private List<Folder> mFolderList, mSelectedFolderList;
    private List<Size> mSelectedSizeList;
    private List<Category> mCategoryList;
    private List<Long> mFolderParentIdList, mSizeParentIdList;
    private TreeNode mClickedNode;
    private final int mMargin, mMainSort, mListSort;

    public TreeViewModel(@NonNull @NotNull Application application) {
        super(application);
        mCategoryRepository = Repository.getCategoryRepository(application);
        mFolderRepository = Repository.getFolderRepository(application);
        mMargin = (int) application.getResources().getDimension(R.dimen._12sdp);
        mMainSort = getApplication().getSharedPreferences(SORT_MAIN, Context.MODE_PRIVATE).getInt(SORT_MAIN, SORT_CUSTOM);
        mListSort = getApplication().getSharedPreferences(SORT_LIST, Context.MODE_PRIVATE).getInt(SORT_LIST, SORT_CUSTOM);
    }

    public TreeHolderCategory getCategoryViewHolder(@NotNull TreeHolderCategory treeHolderCategory) {
        return treeHolderCategory.setItems(mSelectedFolderList, mSelectedSizeList,
                getFolderParentIdList(), getSizeParentIdList());
    }

    public TreeHolderFolder getFolderViewHolder(@NotNull TreeHolderFolder treeHolderFolder) {
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
        for (TreeNode folderNode : findAllFolderNode(nodeRoot)) {
            TreeHolderFolder newFolderViewHolder = (TreeHolderFolder) folderNode.getViewHolder();
            if (folderViewHolder.getFolderId() == newFolderViewHolder.getFolderId()) {
                mClickedNode = folderNode;
                break;
            }
        }
        return mClickedNode;
    }

    @NotNull
    private List<TreeNode> findAllFolderNode(@NotNull TreeNode nodeRoot) {
        List<TreeNode> topFolderNodeList = getTopFolderNode(nodeRoot);
        List<TreeNode> allFolderNodeList = new ArrayList<>(topFolderNodeList);
        findAllFolderNode2(topFolderNodeList, allFolderNodeList);
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

    private void findAllFolderNode2(@NotNull List<TreeNode> topFolderNodeList, List<TreeNode> allFolderNodeList) {
        for (TreeNode topFolderNode : topFolderNodeList)
            if (!topFolderNode.getChildren().isEmpty()) {
                allFolderNodeList.addAll(topFolderNode.getChildren());
                findAllFolderNode2(topFolderNode.getChildren(), allFolderNodeList);
            }
    }

    public void categoryAddFolder(@NotNull TreeHolderCategory categoryViewHolder) {
        categoryViewHolder.setIconClickable();

        int size = Integer.parseInt(categoryViewHolder.getBinding().tvItemTreeCategoryContentsSize.getText().toString());
        categoryViewHolder.getBinding().tvItemTreeCategoryContentsSize.setText(String.valueOf(size + 1));

        Category dummy = categoryViewHolder.getCategory();
        dummy.setDummy(!dummy.getDummy());
        mCategoryRepository.categoryUpdate(dummy);
    }

    public void folderAddFolder(@NotNull TreeHolderFolder folderViewHolder) {
        folderViewHolder.setIconClickable();

        int size = Integer.parseInt(folderViewHolder.getBinding().tvItemTreeFolderContentsSize.getText().toString());
        folderViewHolder.getBinding().tvItemTreeFolderContentsSize.setText(String.valueOf(size + 1));

        Folder dummy = folderViewHolder.getFolder();
        dummy.setDummy(!dummy.getDummy());
        mFolderRepository.folderUpdate(dummy);
    }

    public void treeViewDestroy() {
        //for new category, new folder node
        mCategoryList = null;
        mFolderList = null;
        mFolderParentIdList = null;
        mSizeParentIdList = null;
    }

    public int getMargin() {
        return mMargin;
    }

    public int getPlusMargin() {
        int originMargin = ((TreeHolderFolder) getClickedNode().getViewHolder()).getMargin();
        int plusMarin = (int) getApplication().getResources().getDimension(R.dimen._8sdp);
        return originMargin + plusMarin;
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
        if (mCategoryList == null)
            mCategoryList = Sort.categorySort(mMainSort, mCategoryRepository.getCategoryList(mParentCategory, false));
        return mCategoryList;
    }

    public List<Folder> getFolderList() {
        if (mFolderList == null)
            mFolderList = Sort.folderSort(mListSort, mFolderRepository.getFolderList(mParentCategory, false, false));
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
            mFolderParentIdList = mFolderRepository.getFolderParentIdList(mParentCategory, false, false);
        return mFolderParentIdList;
    }

    private List<Long> getSizeParentIdList() {
        if (mSizeParentIdList == null)
            mSizeParentIdList = Repository.getSizeRepository(getApplication()).getSizeParentIdList(mParentCategory, false, false);
        return mSizeParentIdList;
    }

    private long getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(System.currentTimeMillis())));
    }
}
