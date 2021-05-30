package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public abstract class AutoCompleteDao {
    //to searchView
    @Query("SELECT(SELECT name FROM Category WHERE deleted = 0) + " +
            "(SELECT name FROM Folder WHERE deleted = 0 AND parentDeleted = 0) + " +
            "(SELECT name FROM Size WHERE deleted = 0 AND parentDeleted = 0) + " +
            "(SELECT brand FROM Size WHERE deleted = 0 AND parentDeleted = 0)")
    public abstract LiveData<List<String>> getAutoCompleteWordsLive();

    //to recycleBin search
    @Query("SELECT(SELECT name FROM Category WHERE deleted = 1) + " +
            "(SELECT name FROM Folder WHERE deleted = 1) + " +
            "(SELECT name FROM Size WHERE deleted = 1) + " +
            "(SELECT brand FROM Size WHERE deleted = 1)")
    public abstract LiveData<List<String>> getDeletedAutoCompleteWordsLive();
}
