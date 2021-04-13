package com.example.project_myfit.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.project_myfit.data.model.Folder;

import java.util.List;

@Dao
public interface FolderDao {
    @Query("SELECT * FROM Folder WHERE parentId = :folderId AND isDeleted = 0 AND parentIsDeleted = 0 ORDER BY orderNumber")
    LiveData<List<Folder>> getFolderLiveByFolderId(long folderId);

    @Query("SELECT * FROM Folder WHERE parentCategory = :parentCategory AND isDeleted = 0 AND parentIsDeleted = 0 ORDER BY orderNumber")
    List<Folder> getFolderListByParent(String parentCategory);

    @Query("SELECT parentId FROM Folder WHERE parentCategory = :parentCategory AND isDeleted = 0 AND parentIsDeleted = 0")
    List<Long> getFolderFolderIdByParent(String parentCategory);

    @Query("SELECT folderName FROM Folder WHERE isDeleted = 0 AND parentIsDeleted = 0 ORDER BY folderName")
    List<String> getFolderNameList();

    @Query("SELECT max(orderNumber) FROM Folder")
    int getFolderLargestOrder();

    @Query("SELECT * FROM Folder WHERE id = :id")
    Folder getFolder(long id);

    @Insert
    void folderInsert(Folder folder);

    @Update
    void folderUpdate(Folder folder);

    @Update
    void folderUpdate(List<Folder> folderList);

    @Query("SELECT * FROM Folder WHERE parentId = :folderId AND isDeleted = 0 AND parentIsDeleted = 0")
    List<Folder> getFolderListByFolderId(long folderId);

    @Query("SELECT * FROM Folder WHERE isDeleted = 0 AND parentIsDeleted = 0")
    LiveData<List<Folder>> getAllFolderLive();

    @Query("SELECT * FROM Folder WHERE isDeleted = 0 AND parentIsDeleted = 0")
    List<Folder> getAllFolder();

    @Query("SELECT folderName FROM Folder WHERE isDeleted = 0 AND parentIsDeleted = 0 AND parentCategory = :parentCategory")
    List<String> getFolderNameListByParent(String parentCategory);

    @Query("SELECT * FROM Folder ORDER BY id DESC limit 1")
    Folder getLatestFolder();
}
