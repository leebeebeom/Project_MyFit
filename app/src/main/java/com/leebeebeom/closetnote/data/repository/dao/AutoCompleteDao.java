package com.leebeebeom.closetnote.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public abstract class AutoCompleteDao {

    @Query("SELECT(SELECT name FROM Category WHERE deleted = :deleted) + " +
            "(SELECT name FROM Folder WHERE deleted = :deleted AND parentDeleted = 0) + " +
            "(SELECT name FROM Size WHERE deleted = :deleted AND parentDeleted = 0) + " +
            "(SELECT brand FROM Size WHERE deleted = :deleted AND parentDeleted = 0)")
    public abstract LiveData<List<String>> getAutoCompleteWordsLive(boolean deleted);
}
