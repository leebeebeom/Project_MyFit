package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.myfit.data.model.ModelFactory;
import com.example.myfit.data.model.RecentSearch;

import java.util.List;

@Dao
public abstract class RecentSearchDao {
    //to searchView, recycleBin search
    @Query("SELECT * FROM RecentSearch WHERE type = :type ORDER BY date DESC LIMIT 20")
    public abstract LiveData<List<RecentSearch>> getLiveByType(int type);

    @Transaction
    //from searchView, recycleBin search
    public void insert(String word, int type) {
        if (isExistingWord(word, type)) delete(word);

        RecentSearch recentSearch = ModelFactory.makeRecentSearch(word, type);
        insert(recentSearch);
    }

    @Query("SELECT EXISTS(SELECT word FROM RecentSearch WHERE word = :word AND type = :type)")
    protected abstract boolean isExistingWord(String word, int type);

    @Query("DELETE FROM RecentSearch WHERE word = :word")
    protected abstract void delete(String word);

    @Insert
    protected abstract void insert(RecentSearch recentSearch);

    @Delete
    //from searchView, recycleBin search
    public abstract void delete(RecentSearch recentSearch);

    @Query("DELETE FROM RecentSearch")
    //from searchView, recycleBin search
    public abstract void deleteAll();
}
