package com.example.myfit.data.repository;

import androidx.lifecycle.LiveData;

import com.example.myfit.data.model.RecentSearch;
import com.example.myfit.data.repository.dao.RecentSearchDao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class
RecentSearchRepository {
    private final RecentSearchDao recentSearchDao;
    private int type;
    private LiveData<List<RecentSearch>> recentSearchLive;

    @Inject
    public RecentSearchRepository(RecentSearchDao recentSearchDao) {
        this.recentSearchDao = recentSearchDao;
    }

    //to searchView, recycleBin search
    public LiveData<List<RecentSearch>> getLiveByType(int type) {
        if (this.type != type) {
            this.type = type;
            recentSearchLive = recentSearchDao.getLiveByType(type);
        }
        return recentSearchLive;
    }

    //from searchView, recycleBin search
    public void insert(String word, int type) {
        new Thread(() -> recentSearchDao.insert(word, type)).start();
    }

    //from searchView, recycleBin search
    public void delete(RecentSearch recentSearch) {
        new Thread(() -> recentSearchDao.delete(recentSearch)).start();
    }

    //from searchView, recycleBin search
    public void deleteAll() {
        new Thread(recentSearchDao::deleteAll).start();
    }
}
