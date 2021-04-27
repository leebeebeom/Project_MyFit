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
    @Query("SELECT * FROM RecentSearch WHERE isRecycleBin = :isRecycleBin ORDER BY id DESC LIMIT 20")
    LiveData<List<RecentSearch>> getRecentSearchLive(boolean isRecycleBin);

    @Query("SELECT * FROM RecentSearch WHERE word = :word")
    RecentSearch getRecentSearch(String word);

    @Insert
    void recentSearchInsert(RecentSearch recentSearch);

    @Delete
    void recentSearchDelete(RecentSearch recentSearch);

    @Query("DELETE FROM RecentSearch")
    void deleteAllRecentSearch();
}
