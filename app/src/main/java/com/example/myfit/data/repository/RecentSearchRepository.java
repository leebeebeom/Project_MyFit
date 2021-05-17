package com.example.myfit.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.RecentSearch;
import com.example.myfit.data.repository.dao.RecentSearchDao;

import java.util.List;

import javax.inject.Inject;

public class RecentSearchRepository {
    private final RecentSearchDao recentSearchDao;

    @Inject
    public RecentSearchRepository(Context context) {
        this.recentSearchDao = AppDataBase.getsInstance(context).recentSearchDao();
    }

    public LiveData<List<RecentSearch>> getLiveByType(byte type) {
        return recentSearchDao.getLiveByType(type);
    }

    public void insert(String word, byte type) {
        new Thread(() -> recentSearchDao.insert(word, type)).start();
    }

    public void delete(RecentSearch recentSearch) {
        new Thread(() -> recentSearchDao.delete(recentSearch)).start();
    }

    public void deleteAll() {
        new Thread(recentSearchDao::deleteAllRecentSearch).start();
    }
}
