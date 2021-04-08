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
    private final MutableLiveData<Integer> mSelectedSizeLive;
    private int mCurrentItem;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mRepository = new Repository(application);
        mSelectedSizeLive = new MutableLiveData<>();
        mSelectedCategoryList = new ArrayList<>();
    }

    public void selectAllClick(boolean checked, CategoryAdapter categoryAdapter) {
        if (!mSelectedCategoryList.isEmpty()) mSelectedCategoryList.clear();
        if (checked) {
            mSelectedCategoryList.addAll(categoryAdapter.getCurrentList());
            categoryAdapter.selectAll();
        } else categoryAdapter.deselectAll();
        mSelectedSizeLive.setValue(mSelectedCategoryList.size());
    }

    public void categoryNameEdit(String categoryName) {
        mSelectedCategoryList.get(0).setCategoryName(categoryName);
        mRepository.categoryUpdate(mSelectedCategoryList.get(0));
    }

    public void selectedCategoryDelete() {
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
        List<Folder> topFolderList = new ArrayList<>();
        for (Category category : mSelectedCategoryList)
            topFolderList.addAll(mRepository.getFolderListByFolderId(category.getId()));

        List<Folder> allFolderList = new ArrayList<>(topFolderList);
        findAllChildFolder2(topFolderList, allFolderList);
        return allFolderList;
    }

    private void findAllChildFolder2(@NotNull List<Folder> topFolderList, List<Folder> allFolderList) {
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
        List<Size> allChildSizeList = new ArrayList<>();
        for (Folder folder : allChildFolderList)
            allChildSizeList.addAll(mRepository.getSizeLiseByFolderId(folder.getId()));
        return allChildSizeList;
    }

    public void orderNumberInit() {
        List<Category> categoryList = mRepository.getAllCategoryList();
        for (int i = 0; i < categoryList.size(); i++)
            categoryList.get(i).setOrderNumber(i);
        mRepository.categoryUpdate(categoryList);
    }

    public boolean addCategoryConfirmClick(String categoryName, String parentCategory) {
        boolean isSameName = false;
        List<String> categoryNameList = mRepository.getAllCategoryNameListByParent(parentCategory);
        for (String name : categoryNameList)
            if (name.equals(categoryName)) {
                isSameName = true;
                break;
            }
        if (!isSameName)
            mRepository.categoryInsert(new Category(categoryName, parentCategory, mRepository.getCategoryLargestOrderPlus1()));
        return isSameName;
    }

    public void sameCategoryNameConfirmClick(@NotNull String categoryName, String parentCategory) {
        mRepository.categoryInsert(new Category(categoryName.trim(), parentCategory, mRepository.getCategoryLargestOrderPlus1()));
    }

    public void categorySelected(@NotNull Category category, boolean isChecked) {
        if (isChecked) mSelectedCategoryList.add(category);
        else mSelectedCategoryList.remove(category);
        mSelectedSizeLive.setValue(mSelectedCategoryList.size());
    }

    public void categoryItemDrop(List<Category> categoryList) {
        mRepository.categoryUpdate(categoryList);
    }

    public MutableLiveData<Integer> getSelectedSizeLive() {
        return mSelectedSizeLive;
    }

    //getter,setter---------------------------------------------------------------------------------
    public List<Category> getSelectedCategoryList() {
        return mSelectedCategoryList;
    }

    public String getSelectedCategoryName() {
        return mSelectedCategoryList.get(0).getCategoryName();
    }

    public int getSelectedCategorySize() {
        return mSelectedCategoryList.size();
    }

    public List<Long> getFolderFolderIdList(String parentCategory) {
        return mRepository.getFolderFolderIdByParent(parentCategory);
    }

    public List<Long> getSizeFolderIdList(String parentCategory) {
        return mRepository.getSizeFolderIdByParent(parentCategory);
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
        return mRepository.getAllCategoryLive();
    }
}