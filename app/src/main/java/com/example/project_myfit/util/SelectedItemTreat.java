package com.example.project_myfit.util;

import android.content.Context;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SelectedItemTreat {
    private final Repository.CategoryRepository mCategoryRepository;
    private final Repository.FolderRepository mFolderRepository;
    private final Repository.SizeRepository mSizeRepository;

    public SelectedItemTreat(Context context) {
        mCategoryRepository = Repository.getCategoryRepository(context);
        mFolderRepository = Repository.getFolderRepository(context);
        mSizeRepository = Repository.getSizeRepository(context);
    }

    //move------------------------------------------------------------------------------------------
    public void folderSizeMove(boolean isSearchView, long targetId, List<Folder> selectedFolderList, List<Size> selectedSizeList) {
        if (isSearchView){
            List<Category> originCategoryList = getOriginCategory(selectedFolderList, selectedSizeList);
            Category targetCategory = mCategoryRepository.getCategory(targetId);
            List<Folder> originFolderList = getOriginFolder(selectedFolderList, selectedSizeList);
            Folder targetFolder = mFolderRepository.getFolder(targetId);

            categorySetDummy(originCategoryList);
            categorySetDummy(targetCategory);
            folderSetDummy(originFolderList);
            folderSetDummy(targetFolder);

            if (targetCategory != null) originCategoryList.add(targetCategory);
            mCategoryRepository.categoryUpdate(originCategoryList);

            if (targetFolder != null) originFolderList.add(targetFolder);
            mFolderRepository.folderUpdate(originFolderList);
        }

        for (Folder f : selectedFolderList) f.setParentId(targetId);
        for (Size s : selectedSizeList) s.setParentId(targetId);

        mFolderRepository.folderUpdate(selectedFolderList);
        mSizeRepository.sizeUpdate(selectedSizeList);
    }

    @NotNull
    private List<Category> getOriginCategory(@NotNull List<Folder> selectedItemFolder, List<Size> selectedItemSize) {
        HashSet<Long> originCategoryIdList = new HashSet<>();
        for (Folder folder : selectedItemFolder)
            originCategoryIdList.add(folder.getParentId());
        for (Size size : selectedItemSize)
            originCategoryIdList.add(size.getParentId());

        List<Category> originCategoryList = new ArrayList<>();
        for (long categoryId : originCategoryIdList)
            originCategoryList.add(mCategoryRepository.getCategory(categoryId));
        return originCategoryList;
    }

    @NotNull
    private List<Folder> getOriginFolder(@NotNull List<Folder> selectedFolderList, List<Size> selectedSizeList) {
        HashSet<Long> originFolderIdList = new HashSet<>();
        for (Folder folder : selectedFolderList)
            originFolderIdList.add(folder.getParentId());
        for (Size size : selectedSizeList)
            originFolderIdList.add(size.getParentId());

        List<Folder> originFolderList = new ArrayList<>();
        for (long folderId : originFolderIdList)
            originFolderList.add(mFolderRepository.getFolder(folderId));
        return originFolderList;
    }

    private void categorySetDummy(Category category) {
        if (category != null)
            category.setDummy(!category.getDummy());
    }

    private void categorySetDummy(@NotNull List<Category> categoryList) {
        for (Category category : categoryList)
            category.setDummy(!category.getDummy());
    }

    private void folderSetDummy(Folder folder) {
        if (folder != null)
            folder.setDummy(!folder.getDummy());
    }

    private void folderSetDummy(@NotNull List<Folder> folderList) {
        for (Folder folder : folderList)
            folder.setDummy(!folder.getDummy());
    }
    //----------------------------------------------------------------------------------------------

    //delete----------------------------------------------------------------------------------------
    public void folderSizeDelete(boolean isSearchView, List<Folder> selectedFolderList, List<Size> selectedSizeList) {
        deleteFolder(selectedFolderList);
        deleteSize(selectedSizeList);
        if (isSearchView) {
            List<Category> originCategoryList = getOriginCategory(selectedFolderList, selectedSizeList);
            List<Folder> originFolderList = getOriginFolder(selectedFolderList, selectedSizeList);

            categorySetDummy(originCategoryList);
            folderSetDummy(originFolderList);

            mCategoryRepository.categoryUpdate(originCategoryList);
            mFolderRepository.folderUpdate(originFolderList);
        }
    }

    private void deleteFolder(@NotNull List<Folder> selectedFolderList) {
        List<Folder> childFolderList = new ArrayList<>();
        List<Size> childSizeList = new ArrayList<>();

        for (Folder f : selectedFolderList) {
            f.setIsDeleted(true);
            childFolderList.addAll(mFolderRepository.getFolderListByParentId(f.getId()));
            childSizeList.addAll(mSizeRepository.getSizeListByParentId(f.getId()));
        }

        if (!childFolderList.isEmpty()) getFolderChild(childFolderList, childSizeList);

        for (Folder f : childFolderList) f.setParentIsDeleted(true);
        for (Size s : childSizeList) s.setParentIsDeleted(true);

        selectedFolderList.addAll(childFolderList);

        mFolderRepository.folderUpdate(selectedFolderList);
        mSizeRepository.sizeUpdate(childSizeList);
    }

    private void getFolderChild(@NotNull List<Folder> parentFolderList, List<Size> sizeList) {
        List<Folder> childFolderList = new ArrayList<>();

        for (Folder f : parentFolderList) {
            sizeList.addAll(mSizeRepository.getSizeListByParentId(f.getId()));

            childFolderList.addAll(mFolderRepository.getFolderListByParentId(f.getId()));
            if (!childFolderList.isEmpty()) getFolderChild(childFolderList, sizeList);
        }
        parentFolderList.addAll(childFolderList);
    }

    private void deleteSize(@NotNull List<Size> selectedSizeList) {
        for (Size s : selectedSizeList) s.setIsDeleted(true);
        mSizeRepository.sizeUpdate(selectedSizeList);
    }
    //----------------------------------------------------------------------------------------------
}
