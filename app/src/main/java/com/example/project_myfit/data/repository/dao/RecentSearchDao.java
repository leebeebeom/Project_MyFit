package com.example.project_myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.project_myfit.data.model.RecentSearch;

@Dao
public interface RecentSearchDao {
    @Query("SELECT * FROM RecentSearch WHERE type = :type ORDER BY id DESC LIMIT 20")
    LiveData<RecentSearch[]> getRecentSearchesLive(int type);

    @Query("SELECT word FROM RecentSearch")
    String[] getRecentSearchStrings();

    @Query("SELECT * FROM RecentSearch WHERE word = :word")
    RecentSearch getRecentSearch(String word);

    @Insert
    long insertRecentSearch(RecentSearch recentSearch);

    @Delete
    void deleteRecentSearch(RecentSearch recentSearch);

    @Query("DELETE FROM RecentSearch")
    void deleteAllRecentSearch();
}
