package com.example.project_myfit.ui.main.listfragment.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ListFolderDao {

    @Query("SELECT * FROM ListFolder WHERE folderId = :folderId ORDER BY orderNumberFolder")
    LiveData<List<ListFolder>> getListFolder(int folderId);

    @Query("SELECT MAX(orderNumberFolder) FROM ListFolder WHERE folderId = :folderId")
    int getLargestOrder(int folderId);

    @Insert
    void insert(ListFolder listFolder);

    @Update
    void update(ListFolder listFolder);

    @Delete
    void Delete(ListFolder listFolder);

}
