package com.example.project_myfit.fragment.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.fragment.main.adapter.CategoryAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.BOTTOM;
import static com.example.project_myfit.util.MyFitConstant.ETC;
import static com.example.project_myfit.util.MyFitConstant.OUTER;
import static com.example.project_myfit.util.MyFitConstant.TOP;

public class MainViewModel extends AndroidViewModel {
    private final Repository.CategoryRepository mCategoryRepository;
    private final Repository.FolderRepository mFolderRepository;
    private final Repository.SizeRepository mSizeRepository;
    private List<Category> mSelectedCategoryList;
    private final MutableLiveData<Integer> mSelectedCategorySizeLive;
    private int mCurrentItem;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mCategoryRepository = Repository.getCategoryRepository(application);
        mFolderRepository = Repository.getFolderRepository(application);
        mSizeRepository = Repository.getSizeRepository(application);
        mSelectedCategorySizeLive = new MutableLiveData<>();
        mSelectedCategoryList = new ArrayList<>();
    }

    public void selectAllClick(boolean isChecked, CategoryAdapter categoryAdapter) {
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
            childSizeList.addAll(mSizeRepository.getSizeListByParentId(category.getId()));
        }

        List<Folder> childFolderList = findAllChildFolder();
        childSizeList.addAll(findAllChildSize(childFolderList));

        for (Folder f : childFolderList) f.setParentIsDeleted(true);
        for (Size s : childSizeList) s.setParentIsDeleted(true);

        mCategoryRepository.categoryUpdate(mSelectedCategoryList);
        mFolderRepository.folderUpdate(childFolderList);
        mSizeRepository.sizeUpdate(childSizeList);

        mSelectedCategoryList.clear();
    }

    @NotNull
    private List<Folder> findAllChildFolder() {
        List<Folder> topFolderList = new ArrayList<>();
        for (Category category : mSelectedCategoryList)
            topFolderList.addAll(mFolderRepository.getFolderListByParentId(category.getId()));

        List<Folder> allFolderList = new ArrayList<>(topFolderList);
        findAllChildFolder2(topFolderList, allFolderList);
        return allFolderList;
    }

    private void findAllChildFolder2(@NotNull List<Folder> topFolderList, List<Folder> allFolderList) {
        List<Folder> childFolderList = new ArrayList<>();
        for (Folder folder : topFolderList) {
            if (!childFolderList.isEmpty()) childFolderList.clear();
            childFolderList.addAll(mFolderRepository.getFolderListByParentId(folder.getId()));
            if (!childFolderList.isEmpty()) {
                allFolderList.addAll(childFolderList);
                findAllChildFolder2(childFolderList, allFolderList);
            }
        }
    }

    @NotNull
    private List<Size> findAllChildSize(@NotNull List<Folder> allChildFolderList) {
        List<Size> allChildSizeList = new ArrayList<>();
        for (Folder folder : allChildFolderList)
            allChildSizeList.addAll(mSizeRepository.getSizeListByParentId(folder.getId()));
        return allChildSizeList;
    }

    public void categorySelected(@NotNull Category category, boolean isChecked) {
        if (isChecked) mSelectedCategoryList.add(category);
        else mSelectedCategoryList.remove(category);
        mSelectedCategorySizeLive.setValue(mSelectedCategoryList.size());
    }

    public void categoryItemDrop(@NotNull List<Category> newOrderNumberCategoryList) {
        List<Category> newSelectedCategoryList = new ArrayList<>();
        for (Category category : newOrderNumberCategoryList)
            if (mSelectedCategoryList.contains(category)) newSelectedCategoryList.add(category);
        mSelectedCategoryList = newSelectedCategoryList;

        mCategoryRepository.categoryUpdate(newOrderNumberCategoryList);
    }

    public MutableLiveData<Integer> getSelectedCategorySizeLive() {
        return mSelectedCategorySizeLive;
    }

    //getter,setter---------------------------------------------------------------------------------
    public List<Category> getSelectedCategoryList() {
        return mSelectedCategoryList;
    }

    public long getSelectedCategoryId() {
        return mSelectedCategoryList.get(0).getId();
    }

    public int getSelectedCategorySize() {
        return mSelectedCategoryList.size();
    }

    public List<Long> getFolderParentIdList(String parentCategory) {
        return mFolderRepository.getFolderParentIdListByParentCategory(parentCategory);
    }

    public List<Long> getSizeParentIdList(String parentCategory) {
        return mSizeRepository.getSizeParentIdListByParentCategory(parentCategory);
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

    public LiveData<List<Category>> getCategoryLive() {
        return mCategoryRepository.getAllCategoryLive();
    }

    public List<Category> getSelectedCategory() {
        return mSelectedCategoryList;
    }
}