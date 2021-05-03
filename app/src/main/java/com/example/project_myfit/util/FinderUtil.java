package com.example.project_myfit.util;

import android.content.Context;

import com.example.project_myfit.data.Repository;
import com.example.project_myfit.data.model.Folder;
import com.example.project_myfit.data.model.Size;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FinderUtil {

    public static void findAllChildFolder(List<Folder> topFolderList, @NotNull List<Folder> allFolderList, boolean parentIsDeleted, Context context) {
        allFolderList.addAll(topFolderList);
        findAllChildFolder2(topFolderList, allFolderList, parentIsDeleted, context);
    }

    public static void findAllChildFolder2(@NotNull List<Folder> topFolderList, List<Folder> allFolderList, boolean parentIsDeleted, Context context) {
        Repository.FolderRepository folderRepository = Repository.getFolderRepository(context);

        List<Folder> childFolderList = new ArrayList<>();
        for (Folder folder : topFolderList) {
            if (!childFolderList.isEmpty()) childFolderList.clear();
            childFolderList.addAll(folderRepository.getFolderList(folder.getId(), false, parentIsDeleted));
            if (!childFolderList.isEmpty()) {
                allFolderList.addAll(childFolderList);
                findAllChildFolder2(childFolderList, allFolderList, parentIsDeleted, context);
            }
        }
    }

    public static void findAllChildSize(@NotNull List<Folder> allFolderList, List<Size> allSizeList, boolean parentIsDeleted, Context context) {
        Repository.SizeRepository sizeRepository = Repository.getSizeRepository(context);
        for (Folder folder : allFolderList)
            allSizeList.addAll(sizeRepository.getSizeList(folder.getId(), false, parentIsDeleted));
    }
}
