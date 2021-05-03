package com.example.project_myfit.main.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.main.main.adapter.CategoryAdapter;
import com.example.project_myfit.util.FinderUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.ETC;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.util.MyFitConstant.SORT_MAIN;
import static com.example.project_myfit.util.MyFitConstant.TOP;

public class MainViewModel extends AndroidViewModel {
    private final Repository.CategoryRepository mCategoryRepository;
    private final Repository.FolderRepository mFolderRepository;
    private final Repository.SizeRepository mSizeRepository;
    private final MutableLiveData<Integer> mSelectedCategorySizeLive;
    private final SharedPreferences mSortPreferences;
    private List<Category> mSelectedCategoryList;
    private int mCurrentItem;
    private int mSort;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mCategoryRepository = Repository.getCategoryRepository(application);
        mFolderRepository = Repository.getFolderRepository(application);
        mSizeRepository = Repository.getSizeRepository(application);

        mSelectedCategorySizeLive = new MutableLiveData<>();

        mSortPreferences = getApplication().getSharedPreferences(SORT_MAIN, Context.MODE_PRIVATE);
        mSort = mSortPreferences.getInt(SORT_MAIN, SORT_CUSTOM);
    }

    public void selectAllClick(boolean isChecked, @NotNull CategoryAdapter[] categoryAdapterArray) {
        CategoryAdapter categoryAdapter = categoryAdapterArray[mCurrentItem];

        if (!mSelectedCategoryList.isEmpty()) mSelectedCategoryList.clear();
        if (isChecked) {
            mSelectedCategoryList.addAll(categoryAdapter.getCurrentList());
            categoryAdapter.selectAll();
        } else categoryAdapter.deselectAll();
        mSelectedCategorySizeLive.setValue(mSelectedCategoryList.size());
    }

    public void selectedCategoryDelete() {
        List<Size> childSizeList = new ArrayList<>();

        for (Category category : mSelectedCategoryList) {
            category.setIsDeleted(true);
            childSizeList.addAll(mSizeRepository.getSizeList(category.getId(), false, false));
        }

        List<Folder> childFolderList = findAllChildFolder();
        childSizeList.addAll(findAllChildSize(childFolderList));

        for (Folder f : childFolderList) f.setParentIsDeleted(true);
        for (Size s : childSizeList) s.setParentIsDeleted(true);

        mCategoryRepository.categoryUpdate(mSelectedCategoryList);
        mFolderRepository.folderUpdate(childFolderList);
        mSizeRepository.sizeUpdate(childSizeList);
    }

    @NotNull
    private List<Folder> findAllChildFolder() {
        List<Folder> topFolderList = new ArrayList<>();
        for (Category category : mSelectedCategoryList)
            topFolderList.addAll(mFolderRepository.getFolderList(category.getId(), false, false));

        List<Folder> allFolderList = new ArrayList<>(topFolderList);
        FinderUtil.findAllChildFolder(topFolderList, allFolderList, false, false, getApplication());
        return allFolderList;
    }
    f
    @NotNull
    private List<Size> findAllChildSize(@NotNull List<Folder> allChildFolderList) {
        List<Size> allChildSizeList = new ArrayList<>();
        for (Folder folder : allChildFolderList)
            allChildSizeList.addAll(mSizeRepository.getSizeList(folder.getId(), false, false));
        return allChildSizeList;
    }

    public void categorySelected(@NotNull Category category, boolean isChecked, CategoryAdapter[] categoryAdapterArray) {
        if (isChecked) mSelectedCategoryList.add(category);
        else mSelectedCategoryList.remove(category);
        categoryAdapterArray[mCurrentItem].itemSelected(category.getId());
        mSelectedCategorySizeLive.setValue(mSelectedCategoryList.size());
    }

    public void categoryItemDrop(@NotNull List<Category> newOrderNumberCategoryList) {
        List<Category> newSelectedCategoryList = new ArrayList<>();
        for (Category category : newOrderNumberCategoryList)
            if (mSelectedCategoryList.contains(category)) newSelectedCategoryList.add(category);
        mSelectedCategoryList = newSelectedCategoryList;

        mCategoryRepository.categoryUpdate(newOrderNumberCategoryList);
    }

    //getter,setter---------------------------------------------------------------------------------
    public MutableLiveData<Integer> getSelectedCategorySizeLive() {
        return mSelectedCategorySizeLive;
    }

    public List<Category> getSelectedCategoryList() {
        if (mSelectedCategoryList == null) mSelectedCategoryList = new ArrayList<>();
        return mSelectedCategoryList;
    }

    public long getSelectedCategoryId() {
        return mSelectedCategoryList.get(0).getId();
    }

    public int getSelectedCategorySize() {
        return mSelectedCategoryList.size();
    }

    public List<Long> getFolderParentIdList(String parentCategory) {
        return mFolderRepository.getFolderParentIdList(parentCategory, false, false);
    }

    public List<Long> getSizeParentIdList(String parentCategory) {
        return mSizeRepository.getSizeParentIdList(parentCategory, false, false);
    }

    public int getCurrentItem() {
        return mCurrentItem;
    }

    public void setCurrentItem(int mCurrentItem) {
        this.mCurrentItem = mCurrentItem;
    }

    public String getParentCategory() {
        switch (mCurrentItem) {
            case 0:
                return TOP;
            case 1:
                return BOTTOM;
            case 2:
                return OUTER;
            case 3:
                return ETC;
        }
        return null;
    }

    public LiveData<List<Category>> getCategoryLive(boolean isDeleted) {
        return mCategoryRepository.getCategoryLive(isDeleted);
    }

    public int getSort() {
        return mSort;
    }

    public boolean sortChanged(int sort) {
        if (mSort != sort) {
            mSort = sort;
            SharedPreferences.Editor editor = mSortPreferences.edit();
            editor.putInt(SORT_MAIN, sort);
            editor.apply();
            return true;
        } else return false;
    }
}