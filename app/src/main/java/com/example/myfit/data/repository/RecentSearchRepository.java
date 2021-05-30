package com.example.myfit.data.repository;

import androidx.lifecycle.LiveData;

import com.example.myfit.data.model.model.RecentSearch;
import com.example.myfit.data.repository.dao.RecentSearchDao;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ViewModelScoped;

@ViewModelScoped
public class RecentSearchRepository {
    private final RecentSearchDao mRecentSearchDao;

    @Inject
    public RecentSearchRepository(RecentSearchDao recentSearchDao) {
        this.mRecentSearchDao = recentSearchDao;
    }

    //to searchView, recycleBin search
    public LiveData<List<RecentSearch>> getLiveByType(int type) {
        return mRecentSearchDao.getLiveByType(type);
    }

    //from searchView, recycleBin search
    public void insert(String word, int type) {
        new Thread(() -> mRecentSearchDao.insert(word, type)).start();
    }

    //from searchView, recycleBin search
    public void delete(RecentSearch recentSearch) {
        new Thread(() -> mRecentSearchDao.delete(recentSearch)).start();
    }

    //from searchView, recycleBin search
    public void deleteAll() {
        new Thread(mRecentSearchDao::deleteAll).start();
    }
}
