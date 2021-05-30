package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.myfit.data.model.tuple.AutoCompleteTuple;

import java.util.List;

@Dao
public abstract class AutoCompleteDao {
    @Query("SELECT(SELECT id, name, deleted FROM Category) + " +
            "(SELECT id, name, deleted FROM Folder WHERE parentDeleted = 0) + " +
            "(SELECT id, name, deleted FROM Size WHERE parentDeleted = 0) + " +
            "(SELECT id, brand, deleted FROM Size WHERE parentDeleted = 0)")
    public abstract LiveData<List<AutoCompleteTuple>> getAutoCompleteWordsLive();

//    to recycleBin search
//    @Query("SELECT(SELECT name FROM Category WHERE deleted = 1) + " +
//            "(SELECT name FROM Folder WHERE deleted = 1) + " +
//            "(SELECT name FROM Size WHERE deleted = 1) + " +
//            "(SELECT brand FROM Size WHERE deleted = 1)")
//    public abstract LiveData<List<String>> getDeletedAutoCompleteWordsLive();
}
