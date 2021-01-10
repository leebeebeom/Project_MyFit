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

    @Query("SELECT * FROM Folder WHERE folderId = :folderId ORDER BY orderNumber")
    LiveData<List<Folder>> getAllFolder(int folderId);

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
