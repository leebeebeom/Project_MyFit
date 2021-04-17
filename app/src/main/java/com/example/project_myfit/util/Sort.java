package com.example.project_myfit.util;

import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;

import java.util.List;

import static com.example.project_myfit.util.MyFitConstant.SORT_BRAND;
import static com.example.project_myfit.util.MyFitConstant.SORT_BRAND_REVERSE;
import static com.example.project_myfit.util.MyFitConstant.SORT_CREATE;
import static com.example.project_myfit.util.MyFitConstant.SORT_CREATE_REVERSE;
import static com.example.project_myfit.util.MyFitConstant.SORT_CUSTOM;
import static com.example.project_myfit.util.MyFitConstant.SORT_NAME;
import static com.example.project_myfit.util.MyFitConstant.SORT_NAME_REVERSE;

public class Sort {
    public static List<Category> categorySort(int sort, List<Category> categoryList) {
        if (sort == SORT_CUSTOM)
            categoryList.sort((o1, o2) -> Integer.compare(o1.getOrderNumber(), o2.getOrderNumber()));
        else if (sort == SORT_CREATE)
            categoryList.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
        else if (sort == SORT_CREATE_REVERSE)
            categoryList.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
        else if (sort == SORT_NAME)
            categoryList.sort((o1, o2) -> o1.getCategoryName().compareTo(o2.getCategoryName()));
        else if (sort == SORT_NAME_REVERSE)
            categoryList.sort((o1, o2) -> o2.getCategoryName().compareTo(o1.getCategoryName()));
        return categoryList;
    }

    public static List<Folder> folderSort(int sort, List<Folder> folderList) {
        if (sort == SORT_CUSTOM)
            folderList.sort((o1, o2) -> Integer.compare(o1.getOrderNumber(), o2.getOrderNumber()));
        else if (sort == SORT_CREATE)
            folderList.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
        else if (sort == SORT_CREATE_REVERSE)
            folderList.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
        else if (sort == SORT_BRAND || sort == SORT_NAME)
            folderList.sort((o1, o2) -> o1.getFolderName().compareTo(o2.getFolderName()));
        else if (sort == SORT_BRAND_REVERSE || sort == SORT_NAME_REVERSE)
            folderList.sort((o1, o2) -> o2.getFolderName().compareTo(o1.getFolderName()));
        return folderList;
    }

    public static List<Size> sizeSort(int sort, List<Size> sizeList) {
        if (sort == SORT_CUSTOM)
            sizeList.sort((o1, o2) -> Integer.compare(o1.getOrderNumber(), o2.getOrderNumber()));
        else if (sort == SORT_CREATE)
            sizeList.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
        else if (sort == SORT_CREATE_REVERSE)
            sizeList.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
        else if (sort == SORT_BRAND)
            sizeList.sort((o1, o2) -> o1.getBrand().compareTo(o2.getBrand()));
        else if (sort == SORT_BRAND_REVERSE)
            sizeList.sort((o1, o2) -> o2.getBrand().compareTo(o1.getBrand()));
        else if (sort == SORT_NAME)
            sizeList.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        else if (sort == SORT_NAME_REVERSE)
            sizeList.sort((o1, o2) -> o2.getName().compareTo(o1.getName()));
        return sizeList;
    }
}
