package com.example.myfit.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.myfit.data.model.RecentSearch;

import java.util.List;

@Dao
public interface RecentSearchDao {
    @Query("SELECT * FROM RecentSearch WHERE type = :type LIMIT 20")
    LiveData<List<RecentSearch>> getRecentSearchesLiveByType(int type);

    @Query("SELECT word FROM RecentSearch")
    List<String> getRecentSearchStrings();

    @Query("SELECT * FROM RecentSearch WHERE word = :word")
    RecentSearch getRecentSearchByWord(String word);

    @Insert
    long insertRecentSearch(RecentSearch recentSearch);

    @Delete
    void deleteRecentSearch(RecentSearch recentSearch);

    @Query("DELETE FROM RecentSearch")
    void deleteAllRecentSearch();
}
