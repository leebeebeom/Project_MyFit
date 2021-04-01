package com.example.project_myfit.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FolderDao {

    @Query("SELECT * FROM Folder WHERE folderId = :folderId AND isDeleted = :isDeleted ORDER BY orderNumber DESC")
    LiveData<List<Folder>> getFolderLiveByFolderId(long folderId, int isDeleted);

    @Query("SELECT * FROM Folder WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted ORDER BY folderName")
    List<Folder> getAllFolderListByParent(String parentCategory, int isDeleted);

    @Query("SELECT folderId FROM Folder WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted")
    List<Long> getFolderFolderIdByParent(String parentCategory, int isDeleted);

    @Query("SELECT folderName FROM Folder WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted ORDER BY folderName")
    List<String> getFolderNameList(int isDeleted, int parentIsDeleted);

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

    @Query("SELECT * FROM Folder WHERE folderId = :folderId AND isDeleted = :isDeleted")
    List<Folder> getFolderListByFolderId(long folderId, int isDeleted);

    @Query("SELECT * FROM Folder WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted")
    LiveData<List<Folder>> getAllFolderLive(int isDeleted, int parentIsDeleted);

    @Query("SELECT * FROM Folder WHERE isDeleted = :isDeleted AND parentIsDeleted = :parentIsDeleted")
    List<Folder> getAllFolder(int isDeleted, int parentIsDeleted);
}
