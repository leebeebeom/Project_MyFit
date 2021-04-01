package com.example.project_myfit.util;

import android.util.Log;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.example.project_myfit.MyFitConstant.TAG;

public class SelectedItemTreat {
    //move------------------------------------------------------------------------------------------
    public static void folderSizeMove(long folderId, @NotNull Repository repository, List<Folder> selectedFolderList, List<Size> selectedSizeList) {
        Category originCategory = getOriginCategory(repository, selectedFolderList, selectedSizeList);
        Category targetCategory = repository.getCategory(folderId);
        Folder originFolder = getOriginFolder(repository, selectedFolderList, selectedSizeList);
        Folder targetFolder = repository.getFolder(folderId);

        categorySetDummy(originCategory);
        categorySetDummy(targetCategory);
        folderSetDummy(originFolder);
        folderSetDummy(targetFolder);

        for (Folder f : selectedFolderList) f.setFolderId(folderId);
        for (Size s : selectedSizeList) s.setFolderId(folderId);

        repository.folderUpdate(selectedFolderList);
        repository.sizeUpdate(selectedSizeList);

        List<Category> categoryList = new ArrayList<>();
        if (originCategory != null) categoryList.add(originCategory);
        if (targetCategory != null) categoryList.add(targetCategory);
        repository.categoryUpdate(categoryList);

        List<Folder> folderList = new ArrayList<>();
        if (originFolder != null) folderList.add(originFolder);
        if (targetFolder != null) folderList.add(targetFolder);
        repository.folderUpdate(folderList);
    }

    private static Category getOriginCategory(Repository repository, @NotNull List<Folder> selectedItemFolder, List<Size> selectedItemSize) {
        if (!selectedItemFolder.isEmpty())
            return repository.getCategory(selectedItemFolder.get(0).getFolderId());
        else return repository.getCategory(selectedItemSize.get(0).getFolderId());
    }

    private static Folder getOriginFolder(Repository repository, @NotNull List<Folder> selectedFolderList, List<Size> selectedSizeList) {
        if (!selectedFolderList.isEmpty())
            return repository.getFolder(selectedFolderList.get(0).getFolderId());
        else return repository.getFolder(selectedSizeList.get(0).getFolderId());
    }

    private static void categorySetDummy(Category category) {
        if (category != null)
            category.setDummy(!category.getDummy());
        else Log.e(TAG, "categorySetDummy: category가 null임", null);
    }

    private static void folderSetDummy(Folder folder) {
        if (folder != null)
            folder.setDummy(!folder.getDummy());
        else Log.e(TAG, "categorySetDummy: folder가 null임", null);
    }
    //----------------------------------------------------------------------------------------------

    //delete----------------------------------------------------------------------------------------
    public static void folderSizeDelete(boolean isSearchView, Repository repository, List<Folder> selectedFolderList, List<Size> selectedSizeList) {
        deleteFolder(repository, selectedFolderList);
        deleteSize(repository, selectedSizeList);
        if (isSearchView) {
            Category category = getOriginCategory(repository, selectedFolderList, selectedSizeList);
            Folder folder = getOriginFolder(repository, selectedFolderList, selectedSizeList);

            categorySetDummy(category);
            folderSetDummy(folder);

            repository.categoryUpdate(category);
            repository.folderUpdate(folder);
        }
    }

    private static void deleteFolder(Repository repository, @NotNull List<Folder> selectedFolderList) {
        List<Folder> childFolderList = new ArrayList<>();
        List<Size> childSizeList = new ArrayList<>();

        for (Folder f : selectedFolderList) {
            f.setIsDeleted(true);
            childFolderList.addAll(repository.getFolderListByFolderId(f.getId()));
            childSizeList.addAll(repository.getSizeByFolderId(f.getId()));
        }

        if (!childFolderList.isEmpty()) getFolderChild(repository, childFolderList, childSizeList);

        for (Folder f : childFolderList) f.setParentIsDeleted(true);
        for (Size s : childSizeList) s.setParentIsDeleted(true);

        selectedFolderList.addAll(childFolderList);

        repository.folderUpdate(selectedFolderList);
        repository.sizeUpdate(childSizeList);
    }

    private static void getFolderChild(Repository repository, @NotNull List<Folder> parentFolderList, List<Size> sizeList) {
        List<Folder> childFolderList = new ArrayList<>();
        for (Folder f : parentFolderList) {
            sizeList.addAll(repository.getSizeByFolderId(f.getId()));
            childFolderList.addAll(repository.getFolderListByFolderId(f.getId()));
            if (!childFolderList.isEmpty()) getFolderChild(repository, childFolderList, sizeList);
        }
        parentFolderList.addAll(childFolderList);
    }

    private static void deleteSize(Repository repository, @NotNull List<Size> selectedSizeList) {
        for (Size s : selectedSizeList) s.setIsDeleted(true);
        repository.sizeUpdate(selectedSizeList);
    }
    //----------------------------------------------------------------------------------------------

    //categoryDeleted-------------------------------------------------------------------------------
    public static void categoryDelete(Repository repository, @NotNull List<Category> selectedCategoryList) {
        List<Folder> folderList = new ArrayList<>();
        List<Size> sizeList = new ArrayList<>();

        for (Category category : selectedCategoryList) {
            category.setIsDeleted(true);
            sizeList.addAll(repository.getSizeByFolderId(category.getId()));
            folderList.addAll(repository.getFolderListByFolderId(category.getId()));
        }

        if (!folderList.isEmpty()) getFolderChild(repository, folderList, sizeList);
        for (Folder f : folderList) f.setParentIsDeleted(true);
        for (Size s : sizeList) s.setParentIsDeleted(true);

        repository.categoryUpdate(selectedCategoryList);
        repository.folderUpdate(folderList);
        repository.sizeUpdate(sizeList);
    }
}
