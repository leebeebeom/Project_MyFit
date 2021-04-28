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
    @Query("SELECT * FROM Folder WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted ORDER BY folderName")
    LiveData<List<Folder>> getFolderLive(boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT * FROM Folder WHERE parentId = :parentId AND isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted ORDER BY orderNumber")
    LiveData<List<Folder>> getFolderLive(long parentId, boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT * FROM Folder WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted ORDER BY orderNumber")
    List<Folder> getFolderList(boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT * FROM Folder WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted")
    List<Folder> getFolderList(String parentCategory, boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT * FROM Folder WHERE parentId = :parentId AND isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted")
    List<Folder> getFolderList(long parentId, boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT folderName FROM Folder WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIdDeleted")
    LiveData<List<String>> getFolderNameLive(boolean isDeleted, boolean parentIdDeleted);

    @Query("SELECT folderName FROM Folder WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted AND parentId = :parentId")
    List<String> getFolderNameList(long parentId, boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT parentId FROM Folder WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted")
    List<Long> getFolderParentIdList(boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT parentId FROM Folder WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted")
    List<Long> getFolderParentIdList(String parentCategory, boolean isDeleted, boolean parentIsDeleted);

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
