package com.example.project_myfit.util;

import android.util.Log;

import com.example.project_myfit.data.model.Category;
import com.example.project_myfit.data.model.Folder;

import java.util.List;

import static com.example.project_myfit.MyFitConstant.SORT_BRAND;
import static com.example.project_myfit.MyFitConstant.SORT_BRAND_REVERSE;
import static com.example.project_myfit.MyFitConstant.SORT_CREATE;
import static com.example.project_myfit.MyFitConstant.SORT_CREATE_REVERSE;
import static com.example.project_myfit.MyFitConstant.SORT_NAME;
import static com.example.project_myfit.MyFitConstant.SORT_NAME_REVERSE;
import static com.example.project_myfit.MyFitConstant.TAG;

public class Sort {
    public static List<Category> categorySort(int sort, List<Category> categoryList) {
        if (sort == SORT_CREATE)
            categoryList.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
        else if (sort == SORT_CREATE_REVERSE)
            categoryList.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
        else if (sort == SORT_NAME)
            categoryList.sort((o1, o2) -> o1.getCategory().compareTo(o2.getCategory()));
        else if (sort == SORT_NAME_REVERSE)
            categoryList.sort((o1, o2) -> o2.getCategory().compareTo(o1.getCategory()));
        else Log.e(TAG, "categorySort: sort가 어디에도 속하지 않음", null);
        return categoryList;
    }

    public static List<Folder> folderSort(int sort, List<Folder> folderList) {
        if (sort == SORT_CREATE)
            folderList.sort((o1, o2) -> Long.compare(o2.getId(), o1.getId()));
        else if (sort == SORT_CREATE_REVERSE)
            folderList.sort((o1, o2) -> Long.compare(o1.getId(), o2.getId()));
        else if (sort == SORT_BRAND || sort == SORT_NAME)
            folderList.sort((o1, o2) -> o1.getFolderName().compareTo(o2.getFolderName()));
        else if (sort == SORT_BRAND_REVERSE || sort == SORT_NAME_REVERSE)
            folderList.sort((o1, o2) -> o2.getFolderName().compareTo(o1.getFolderName()));
        return folderList;
    }
}
