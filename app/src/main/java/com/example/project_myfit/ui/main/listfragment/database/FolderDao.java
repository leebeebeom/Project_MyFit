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
    LiveData<List<Folder>> getFolderLive(long folderId, boolean isDeleted);

    @Query("SELECT * FROM Folder WHERE isDeleted = :isDeleted ORDER BY orderNumber ASC")
    List<Folder> getFolderList(boolean isDeleted);

    @Query("SELECT MAX(orderNumber) FROM Folder")
    int getLargestOrder();

    @Insert
    void insert(Folder folder);

    @Update
    void update(Folder folder);

    @Update
    void update(List<Folder> folderList);

    @Delete
    void delete(List<Folder> folderList);
}
