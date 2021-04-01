package com.example.project_myfit.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FolderDao {

    @Query("SELECT * FROM Folder WHERE folderId = :folderId AND isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted ORDER BY orderNumber DESC")
    LiveData<List<Folder>> getFolderLiveByFolderId(long folderId, boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT * FROM Folder WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted ORDER BY folderName")
    List<Folder> getAllFolderListByParent(String parentCategory, boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT folderId FROM Folder WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted")
    List<Long> getFolderFolderIdByParent(String parentCategory, boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT folderName FROM Folder WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted ORDER BY folderName")
    List<String> getFolderNameList(boolean isDeleted, boolean parentIsDeleted);

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

    @Query("SELECT * FROM Folder WHERE folderId = :folderId AND isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted")
    List<Folder> getFolderListByFolderId(long folderId, boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT * FROM Folder WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted")
    LiveData<List<Folder>> getAllFolderLive(boolean isDeleted, boolean parentIsDeleted);

    @Query("SELECT * FROM Folder WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted")
    List<Folder> getAllFolder(boolean isDeleted, boolean parentIsDeleted);
}
