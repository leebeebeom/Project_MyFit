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

    @Query("SELECT * FROM ListFolder")
    LiveData<List<ListFolder>> getAllListFolder();

    @Insert
    void insert(ListFolder listFolder);

    @Update
    void update(ListFolder listFolder);

    @Delete
    void Delete(ListFolder listFolder);

}
