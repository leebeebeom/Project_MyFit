package com.example.myfit.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.repository.dao.AutoCompleteDao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ViewModelScoped;

@ViewModelScoped
public class AutoCompleteRepository {
    private final AutoCompleteDao mAutoCompleteDao;
    private LiveData<List<String>> mOrderedWordsLive, mOrderedDeletedWordsLive;

    @Inject
    public AutoCompleteRepository(AutoCompleteDao autoCompleteDao) {
        this.mAutoCompleteDao = autoCompleteDao;
    }

    public LiveData<List<String>> getAutoCompleteWordsLive() {
        if (mOrderedWordsLive == null) {
            LiveData<List<String>> autoCompleteWordsLive = mAutoCompleteDao.getAutoCompleteWordsLive();
            mOrderedWordsLive = getOrderedLive(autoCompleteWordsLive);
        }
        return mOrderedWordsLive;
    }

    public LiveData<List<String>> getDeletedAutoCompleteWordsLive() {
        if (mOrderedDeletedWordsLive == null) {
            LiveData<List<String>> deletedAutoCompleteWordsLive = mAutoCompleteDao.getDeletedAutoCompleteWordsLive();
            mOrderedDeletedWordsLive = getOrderedLive(deletedAutoCompleteWordsLive);
        }
        return mOrderedDeletedWordsLive;
    }

    @NotNull
    private LiveData<List<String>> getOrderedLive(LiveData<List<String>> autoCompleteWordsLive) {
        return Transformations.map(autoCompleteWordsLive, autoCompleteWords -> {
            autoCompleteWords.sort(String::compareTo);
            return autoCompleteWords;
        });
    }
}
