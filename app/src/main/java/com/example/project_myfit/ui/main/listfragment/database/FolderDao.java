package com.example.project_myfit.ui.main.listfragment.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface FolderDao {

    @Query("SELECT * FROM Folder WHERE folderId = :folderId AND isDeleted = :isDeleted ORDER BY orderNumber DESC")
    LiveData<List<Folder>> getFolderLive(long folderId, int isDeleted);

    @Query("SELECT * FROM Folder WHERE isDeleted = :isDeleted ORDER BY orderNumber DESC")
    List<Folder> getAllFolderList(int isDeleted);

    @Query("SELECT * FROM Folder WHERE parentCategory = :parentCategory AND isDeleted = :isDeleted ORDER BY folderName")
    List<Folder> getAllFolderList2(String parentCategory, int isDeleted);

    @Query("SELECT folderName FROM Folder WHERE isDeleted = :isDeleted ORDER BY folderName")
    List<String> getFolderNameList(int isDeleted);

    @Query("SELECT MAX(orderNumber) FROM Folder")
    int getLargestOrder();

    @Query("SELECT * FROM Folder WHERE id = :id")
    Folder getFolder(long id);

    @Insert
    void insert(Folder folder);

    @Update
    void update(Folder folder);

    @Update
    void update(List<Folder> folderList);

    @Delete
    void delete(List<Folder> folderList);
}
