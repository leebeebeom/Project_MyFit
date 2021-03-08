package com.example.project_myfit.searchActivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.Repository;
import com.example.project_myfit.searchActivity.adapter.SearchAdapter;
import com.example.project_myfit.ui.main.database.Category;
import com.example.project_myfit.ui.main.listfragment.database.Folder;
import com.example.project_myfit.ui.main.listfragment.database.Size;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderCategory;
import com.example.project_myfit.ui.main.listfragment.treeview.TreeHolderFolder;
import com.unnamed.b.atv.model.TreeNode;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static com.example.project_myfit.MyFitConstant.BOTTOM;
import static com.example.project_myfit.MyFitConstant.ETC;
import static com.example.project_myfit.MyFitConstant.OUTER;
import static com.example.project_myfit.MyFitConstant.TOP;

public class SearchViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private final List<Object> mSelectedItem;
    private final MutableLiveData<Integer> mSelectedAmount;
    private TreeNode mAddNode;
    private TreeHolderCategory.CategoryTreeHolder mCategoryAddValue;
    private TreeHolderFolder.FolderTreeHolder mFolderAddValue;
    private String mParentCategory;
    private TreeNode mTreeNodeRoot;

    public SearchViewModel(@NonNull @NotNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mSelectedItem = new ArrayList<>();
        mSelectedAmount = new MutableLiveData<>();
    }

    public Repository getRepository() {
        return mRepository;
    }

    public List<Object> getSelectedItem() {
        return mSelectedItem;
    }

    public void setSelectedAmount() {
        mSelectedAmount.setValue(mSelectedItem.size());
    }

    public MutableLiveData<Integer> getSelectedAmount() {
        return mSelectedAmount;
    }

    public void setSelectedPosition(HashSet<Integer> mSelectedPosition) {
    }

    public void sizeSelected(Size size, boolean isChecked, int position, @NotNull SearchAdapter adapter) {
        adapter.setSelectedPosition(position);
        if (isChecked) mSelectedItem.add(size);
        else mSelectedItem.remove(size);
        setSelectedAmount();
    }

    public void folderSelected(Folder folder, boolean isChecked, int position, @NotNull SearchAdapter adapter) {
        adapter.setSelectedPosition(position);
        if (isChecked) mSelectedItem.add(folder);
        else mSelectedItem.remove(folder);
        setSelectedAmount();
    }

    public void selectedItemDelete() {
        List<Folder> folderList = new ArrayList<>();
        List<Size> sizeList = new ArrayList<>();
        List<Category> categoryList = new ArrayList<>();
        for (Object o : mSelectedItem) {
            if (o instanceof Folder) {
                Category category = mRepository.getCategory(((Folder) o).getFolderId());
                ((Folder) o).setIsDeleted(1);
                folderList.add((Folder) o);
                categoryRefresh(category, categoryList);
            } else if (o instanceof Size) {
                Category category = mRepository.getCategory(((Size) o).getFolderId());
                ((Size) o).setIsDeleted(1);
                sizeList.add((Size) o);
                categoryRefresh(category, categoryList);
            }
        }
        mRepository.folderUpdate(folderList);
        mRepository.sizeUpdate(sizeList);
        mRepository.categoryUpdate(categoryList);
    }

    public void nodeAddClick(TreeNode node, TreeHolderCategory.CategoryTreeHolder value) {
        mAddNode = node;
        mCategoryAddValue = value;
        mFolderAddValue = null;
    }

    public void nodeAddClick(TreeNode node, TreeHolderFolder.FolderTreeHolder value) {
        mAddNode = node;
        mFolderAddValue = value;
        mCategoryAddValue = null;
    }

    public TreeNode getAddNode() {
        return mAddNode;
    }

    public TreeHolderCategory.CategoryTreeHolder getCategoryAddValue() {
        return mCategoryAddValue;
    }

    public long getCurrentTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return Long.parseLong(dateFormat.format(new Date(System.currentTimeMillis())));
    }

    public List<Category> getCategoryListByParent() {
        return null;
    }

    public void setParentCategory(int position) {
        switch (position) {
            case 0:
                mParentCategory = TOP;
                break;
            case 1:
                mParentCategory = BOTTOM;
                break;
            case 2:
                mParentCategory = OUTER;
                break;
            case 3:
                mParentCategory = ETC;
                break;
        }
    }

    public String getParentCategory() {
        return mParentCategory;
    }

    public void setTreeNodeRoot(TreeNode treeNodeRoot) {
        this.mTreeNodeRoot = treeNodeRoot;
    }

    public TreeNode getTreeNodeRoot() {
        return mTreeNodeRoot;
    }

    public TreeHolderFolder.FolderTreeHolder getFolderAddValue() {
        return mFolderAddValue;
    }

    public void selectedItemMove(long folderId) {
        List<Folder> folderList = new ArrayList<>();
        List<Size> sizeList = new ArrayList<>();
        List<Category> categoryList = new ArrayList<>();
        for (Object o : mSelectedItem) {
            if (o instanceof Folder) {
                Category category = mRepository.getCategory(((Folder) o).getFolderId());
                Category category2 = mRepository.getCategory(folderId);
                ((Folder) o).setFolderId(folderId);
                folderList.add((Folder) o);
                categoryRefresh(category, categoryList);
                categoryRefresh(category2, categoryList);
            } else if (o instanceof Size) {
                Category category = mRepository.getCategory(((Size) o).getFolderId());
                Category category2 = mRepository.getCategory(folderId);
                ((Size) o).setFolderId(folderId);
                sizeList.add((Size) o);
                categoryRefresh(category, categoryList);
                categoryRefresh(category2, categoryList);
            }
        }
        mRepository.folderUpdate(folderList);
        mRepository.sizeUpdate(sizeList);
        mRepository.categoryUpdate(categoryList);
    }

    private void categoryRefresh(Category category, List<Category> categoryList) {
        if (category != null && !categoryList.contains(category)) {
            if (category.getDummy() == 0) category.setDummy(1);
            else category.setDummy(0);
            categoryList.add(category);
        }
    }
}