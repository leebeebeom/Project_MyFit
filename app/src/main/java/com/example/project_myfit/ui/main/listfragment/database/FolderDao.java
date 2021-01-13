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

    @Query("SELECT * FROM Folder WHERE folderId = :folderId AND isDeleted = :idDeleted ORDER BY orderNumber ASC")
    LiveData<List<Folder>> getAllFolder(long folderId, boolean idDeleted);

    @Query("SELECT * FROM Folder")
    List<Folder> getAllFolder();

    @Query("SELECT * FROM folder")
    LiveData<List<Folder>> getAllFolderLive();

    @Query("SELECT MAX(orderNumber) FROM Folder")
    int getLargestOrder();

    @Insert
    void insert(Folder folder);

    @Update
    void update(Folder folder);

    @Delete
    void delete(List<Folder> folderList);

    @Update
    void updateOrder(List<Folder> folderList);

}
