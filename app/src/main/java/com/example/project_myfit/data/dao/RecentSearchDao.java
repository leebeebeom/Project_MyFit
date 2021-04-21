package com.example.project_myfit.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.project_myfit.data.model.RecentSearch;

import java.util.List;

@Dao
public interface RecentSearchDao {
    @Query("SELECT * FROM RecentSearch ORDER BY id DESC")
    LiveData<List<RecentSearch>> getAllRecentSearchLive();

    @Query("SELECT * FROM RecentSearch WHERE word = :word")
    RecentSearch getRecentSearchByWord(String word);

    @Insert
    void insertRecentSearch(RecentSearch recentSearch);

    @Delete
    void deleteRecentSearch(RecentSearch recentSearch);

    @Query("DELETE FROM RecentSearch")
    void deleteAllRecentSearch();
}
