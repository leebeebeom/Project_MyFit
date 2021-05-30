package com.example.myfit.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.tuple.AutoCompleteTuple;
import com.example.myfit.data.repository.dao.AutoCompleteDao;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ViewModelScoped;

@ViewModelScoped
public class AutoCompleteRepository {
    private LiveData<List<String>> mWordsLive, mDeletedWordsLive;
    private final LiveData<List<AutoCompleteTuple>> mOrderedWordsLive;

    @Inject
    public AutoCompleteRepository(AutoCompleteDao autoCompleteDao) {
        LiveData<List<AutoCompleteTuple>> autoCompleteWordsLive = autoCompleteDao.getAutoCompleteWordsLive();
        this.mOrderedWordsLive = Transformations.map(autoCompleteWordsLive, tuples -> {
            tuples.sort((o1, o2) -> o1.getWord().compareTo(o2.getWord()));
            return tuples;
        });
    }

    public LiveData<List<String>> getAutoCompleteWordsLive() {
        if (mWordsLive == null) {
            mWordsLive = Transformations.map(mOrderedWordsLive,
                    tuples -> tuples.stream()
                            .filter(autoCompleteTuple -> !autoCompleteTuple.isDeleted())
                            .map(AutoCompleteTuple::getWord)
                            .collect(Collectors.toList()));
        }
        return mWordsLive;
    }

    public LiveData<List<String>> getDeletedAutoCompleteWordsLive() {
        if (mDeletedWordsLive == null) {
            mDeletedWordsLive = Transformations.map(mOrderedWordsLive,
                    tuples -> tuples.stream()
                            .filter(AutoCompleteTuple::isDeleted)
                            .map(AutoCompleteTuple::getWord)
                            .collect(Collectors.toList()));
        }
        return mDeletedWordsLive;
    }
}
