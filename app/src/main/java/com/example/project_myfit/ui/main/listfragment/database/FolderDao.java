package com.example.project_myfit.ui.main.listfragment.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FolderDao {

    @Query("SELECT * FROM Folder WHERE folderId = :folderId AND isDeleted = :isDeleted ORDER BY orderNumber DESC")
    LiveData<List<Folder>> getFolderLiveByFolder(long folderId, int isDeleted);

    @Query("SELECT * FROM Folder WHERE isDeleted = :isDeleted ORDER BY folderName")
    LiveData<List<Folder>> getAllFolderLive(int isDeleted);

    @Query("SELECT * FROM Folder WHERE isDeleted = :isDeleted ORDER BY orderNumber DESC")
    List<Folder> getAllFolderList(int isDeleted);

    @Query("SELECT * FROM Folder WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted ORDER BY folderName")
    List<Folder> getAllFolderListByParent(String parentCategory, int isDeleted);

    @Query("SELECT folderName FROM Folder WHERE isDeleted = :isDeleted ORDER BY folderName")
    List<String> getFolderNameList(int isDeleted);

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
}
