package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public abstract class AutoCompleteDao {
    //to searchView
    @Query("SELECT(SELECT name FROM Category WHERE isDeleted = 0) + " +
            "(SELECT name FROM Folder WHERE isDeleted = 0 AND isParentDeleted = 0) + " +
            "(SELECT name FROM Size WHERE isDeleted = 0 AND isParentDeleted = 0) + " +
            "(SELECT brand FROM Size WHERE isDeleted = 0 AND isParentDeleted = 0)")
    public abstract LiveData<List<String>> getAutoCompleteWordsLive();

    //to recycleBin search
    @Query("SELECT(SELECT name FROM Category WHERE isDeleted = 1) + " +
            "(SELECT name FROM Folder WHERE isDeleted = 1) + " +
            "(SELECT name FROM Size WHERE isDeleted = 1) + " +
            "(SELECT brand FROM Size WHERE isDeleted = 1)")
    public abstract LiveData<List<String>> getDeletedAutoCompleteWordsLive();
}
