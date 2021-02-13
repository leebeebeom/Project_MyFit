package com.example.project_myfit.searchActivity.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RecentSearchDao {
    @Query("SELECT * FROM RecentSearch ORDER BY id DESC")
    LiveData<List<RecentSearch>> getRecentSearchList();

    @Insert
    void insertRecentSearch(RecentSearch recentSearch);

    @Delete
    void deleteRecentSearch(RecentSearch recentSearch);
}
