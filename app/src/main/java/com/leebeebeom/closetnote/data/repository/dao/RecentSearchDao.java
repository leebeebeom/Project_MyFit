package com.leebeebeom.closetnote.data.repository.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.leebeebeom.closetnote.data.model.model.RecentSearch;
import com.leebeebeom.closetnote.data.tuple.tuple.RecentSearchTuple;
import com.leebeebeom.closetnote.util.CommonUtil;

import java.util.List;

@Dao
public abstract class RecentSearchDao {

    //to searchView, recycleBin search
    @Query("SELECT word, date FROM RecentSearch WHERE type = :type ORDER BY id DESC LIMIT 20")
    public abstract LiveData<List<RecentSearchTuple>> getLiveByType(int type);

    @Transaction
    //from searchView, recycleBin search
    public void insert(String word, int type) {
        if (isExistingWord(word, type)) delete(word, type);
        String date = CommonUtil.getRecentSearchDate();
        RecentSearch recentSearch = new RecentSearch(word, date, type);
        insert(recentSearch);
    }

    @Query("SELECT EXISTS(SELECT word FROM RecentSearch WHERE word = :word AND type = :type)")
    protected abstract boolean isExistingWord(String word, int type);

    @Query("DELETE FROM RecentSearch WHERE word = :word AND type = :type")
    public abstract void delete(String word, int type);

    @Insert
    protected abstract void insert(RecentSearch recentSearch);

    @Query("DELETE FROM RecentSearch WHERE type = :type")
    public abstract void deleteAll(int type);
}
