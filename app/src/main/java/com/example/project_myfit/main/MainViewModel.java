package com.example.project_myfit.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;
import com.example.project_myfit.main.adapter.CategoryAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.BOTTOM;
import static com.example.project_myfit.MyFitConstant.ETC;
import static com.example.project_myfit.MyFitConstant.OUTER;
import static com.example.project_myfit.MyFitConstant.TOP;

public class MainViewModel extends AndroidViewModel {
    private final Repository mRepository;
    private final List<Category> mSelectedCategoryList;
    private final MutableLiveData<Integer> mSelectedCategorySizeLive;
    private int mCurrentItem;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mSelectedCategorySizeLive = new MutableLiveData<>();
        mSelectedCategoryList = new ArrayList<>();
    }

    public void orderNumberInit() {
        //tested
        List<Category> categoryList = mRepository.getAllCategoryList();
        for (int i = 0; i < categoryList.size(); i++)
            categoryList.get(i).setOrderNumber(i);
        mRepository.categoryUpdate(categoryList);
    }

    public void selectAllClick(boolean checked, CategoryAdapter categoryAdapter) {
        //tested
        if (!mSelectedCategoryList.isEmpty()) mSelectedCategoryList.clear();
        if (checked) {
            mSelectedCategoryList.addAll(categoryAdapter.getCurrentList());
            categoryAdapter.selectAll();
        } else categoryAdapter.deselectAll();
        mSelectedCategorySizeLive.setValue(mSelectedCategoryList.size());
    }

    public void selectedCategoryDelete() {
        //tested
        List<Size> childSizeList = new ArrayList<>();

        for (Category category : mSelectedCategoryList) {
            category.setIsDeleted(true);
            childSizeList.addAll(mRepository.getSizeLiseByFolderId(category.getId()));
        }

        List<Folder> childFolderList = findAllChildFolder();
        childSizeList.addAll(findAllChildSize(childFolderList));

        for (Folder f : childFolderList) f.setParentIsDeleted(true);
        for (Size s : childSizeList) s.setParentIsDeleted(true);

        mRepository.categoryUpdate(mSelectedCategoryList);
        mRepository.folderUpdate(childFolderList);
        mRepository.sizeUpdate(childSizeList);
    }

    @NotNull
    private List<Folder> findAllChildFolder() {
        //tested
        List<Folder> topFolderList = new ArrayList<>();
        for (Category category : mSelectedCategoryList)
            topFolderList.addAll(mRepository.getFolderListByFolderId(category.getId()));

        List<Folder> allFolderList = new ArrayList<>(topFolderList);
        findAllChildFolder2(topFolderList, allFolderList);
        return allFolderList;
    }

    private void findAllChildFolder2(@NotNull List<Folder> topFolderList, List<Folder> allFolderList) {
        //tested
        List<Folder> childFolderList = new ArrayList<>();
        for (Folder folder : topFolderList) {
            if (!childFolderList.isEmpty()) childFolderList.clear();
            childFolderList.addAll(mRepository.getFolderListByFolderId(folder.getId()));
            if (!childFolderList.isEmpty()) {
                allFolderList.addAll(childFolderList);
                findAllChildFolder2(childFolderList, allFolderList);
            }
        }
    }

    @NotNull
    private List<Size> findAllChildSize(@NotNull List<Folder> allChildFolderList) {
        //tested
        List<Size> allChildSizeList = new ArrayList<>();
        for (Folder folder : allChildFolderList)
            allChildSizeList.addAll(mRepository.getSizeLiseByFolderId(folder.getId()));
        return allChildSizeList;
    }

    public void categorySelected(@NotNull Category category, boolean isChecked) {
        //tested
        if (isChecked) mSelectedCategoryList.add(category);
        else mSelectedCategoryList.remove(category);
        mSelectedCategorySizeLive.setValue(mSelectedCategoryList.size());
    }

    public void categoryItemDrop(List<Category> categoryList) {
        //tested
        mRepository.categoryUpdate(categoryList);
    }

    public MutableLiveData<Integer> getSelectedCategorySizeLive() {
        //tested
        return mSelectedCategorySizeLive;
    }

    //getter,setter---------------------------------------------------------------------------------
    public List<Category> getSelectedCategoryList() {
        //tested
        return mSelectedCategoryList;
    }

    public long getSelectedCategoryId() {
        //tested
        return mSelectedCategoryList.get(0).getId();
    }

    public int getSelectedCategorySize() {
        //tested
        return mSelectedCategoryList.size();
    }

    public List<Long> getFolderFolderIdList(String parentCategory) {
        //tested
        return mRepository.getFolderFolderIdByParent(parentCategory);
    }

    public List<Long> getSizeFolderIdList(String parentCategory) {
        //tested
        return mRepository.getSizeFolderIdByParent(parentCategory);
    }

    public int getCurrentItem() {
        //tested
        return mCurrentItem;
    }

    public void setCurrentItem(int mCurrentItem) {
        //tested
        this.mCurrentItem = mCurrentItem;
    }

    public String getParentCategory() {
        //tested
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
        //tested
        return mRepository.getAllCategoryLive();
    }
}