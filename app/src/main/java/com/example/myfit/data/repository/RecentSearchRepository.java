package com.example.myfit.data.repository;

import androidx.lifecycle.LiveData;

import com.example.myfit.data.repository.dao.RecentSearchDao;
import com.example.myfit.data.tuple.tuple.RecentSearchTuple;
import com.example.myfit.util.constant.RecentSearchType;

import java.util.List;

import javax.inject.Inject;

public class RecentSearchRepository {
    private final RecentSearchDao mRecentSearchDao;
    private LiveData<List<RecentSearchTuple>> mSearchLive, mRecycleBinLive, mDailyLookLive, mMWishListLive;

    @Inject
    public RecentSearchRepository(RecentSearchDao recentSearchDao) {
        this.mRecentSearchDao = recentSearchDao;
    }

    public LiveData<List<RecentSearchTuple>> getLiveByType(RecentSearchType type) {
        switch (type) {
            case SEARCH:
                return getRecentSearchLive(mSearchLive, type);
            case RECYCLE_BIN:
                return getRecentSearchLive(mRecycleBinLive, type);
            case DAILY_LOOK:
                return getRecentSearchLive(mDailyLookLive, type);
            default:
                return getRecentSearchLive(mMWishListLive, type);
        }

    }

    public LiveData<List<RecentSearchTuple>> getRecentSearchLive(LiveData<List<RecentSearchTuple>> liveData, RecentSearchType type) {
        if (liveData == null)
            liveData = mRecentSearchDao.getLiveByType(type.getValue());
        return liveData;
    }

    public void insert(String word, int type) {
        new Thread(() -> mRecentSearchDao.insert(word, type)).start();
    }

    public void delete(String word, RecentSearchType type) {
        new Thread(() -> mRecentSearchDao.delete(word, type.getValue())).start();
    }

    public void deleteAll(RecentSearchType type) {
        new Thread(() -> mRecentSearchDao.deleteAll(type.getValue())).start();
    }
}
