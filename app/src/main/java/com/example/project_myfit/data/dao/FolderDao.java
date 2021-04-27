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
    @Query("SELECT * FROM Folder WHERE isDeleted = 0 AND parentIsDeleted = 0 ORDER BY folderName")
    LiveData<List<Folder>> getFolderLive();

    @Query("SELECT * FROM Folder WHERE parentId = :parentId AND isDeleted = 0 AND parentIsDeleted = 0 ORDER BY orderNumber")
    LiveData<List<Folder>> getFolderLive(long parentId);

    @Query("SELECT * FROM Folder WHERE isDeleted = 1")
    LiveData<List<Folder>> getDeletedFolderLive();

    @Query("SELECT * FROM Folder WHERE isDeleted = 0 AND parentIsDeleted = 0 ORDER BY orderNumber")
    List<Folder> getFolderList();

    @Query("SELECT * FROM Folder WHERE parentCategory = :parentCategory AND isDeleted = 0 AND parentIsDeleted = 0 ORDER BY orderNumber")
    List<Folder> getFolderList(String parentCategory);

    @Query("SELECT * FROM Folder WHERE parentId = :parentId AND isDeleted = 0 AND parentIsDeleted = 0")
    List<Folder> getFolderList(long parentId);

    @Query("SELECT folderName FROM Folder WHERE isDeleted =0 AND parentIsDeleted = 0")
    LiveData<List<String>> getFolderNameLive();

    @Query("SELECT folderName FROM Folder WHERE isDeleted =1 OR parentIsDeleted =1")
    LiveData<List<String>> getDeletedFolderNameLive();

    @Query("SELECT folderName FROM Folder WHERE isDeleted = 0 AND parentIsDeleted = 0 AND parentId = :parentId")
    List<String> getFolderNameList(long parentId);

    @Query("SELECT parentId FROM Folder WHERE parentCategory = :parentCategory AND isDeleted = 0 AND parentIsDeleted = 0")
    List<Long> getFolderParentIdList(String parentCategory);

    @Query("SELECT parentId FROM Folder WHERE isDeleted AND parentIsDeleted = 1")
    List<Long> getParentDeletedFolderParentIdList();

    @Query("SELECT * FROM Folder WHERE id = :id")
    LiveData<Folder> getSingleFolderLive(long id);

    @Query("SELECT * FROM Folder WHERE id = :id")
    Folder getFolder(long id);

    @Query("SELECT * FROM Folder ORDER BY id DESC limit 1")
    Folder getLatestFolder();

    @Query("SELECT max(orderNumber) FROM Folder")
    int getFolderLargestOrder();

    @Insert
    void folderInsert(Folder folder);

    @Update
    void folderUpdate(Folder folder);

    @Update
    void folderUpdate(List<Folder> folderList);
}
