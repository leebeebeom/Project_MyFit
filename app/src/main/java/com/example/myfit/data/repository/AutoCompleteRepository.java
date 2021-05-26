package com.example.myfit.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.repository.dao.AutoCompleteDao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

public class AutoCompleteRepository {
    private final AutoCompleteDao autoCompleteDao;
    private LiveData<List<String>> orderedWordsLive, orderedDeletedWordsLive;

    @Inject
    public AutoCompleteRepository(AutoCompleteDao autoCompleteDao) {
        this.autoCompleteDao = autoCompleteDao;
    }

    public LiveData<List<String>> getAutoCompleteWordsLive() {
        if (orderedWordsLive == null) {
            LiveData<List<String>> autoCompleteWordsLive = autoCompleteDao.getAutoCompleteWordsLive();
            orderedWordsLive = getOrderedLive(autoCompleteWordsLive);
        }
        return orderedWordsLive;
    }

    public LiveData<List<String>> getDeletedAutoCompleteWordsLive() {
        if (orderedDeletedWordsLive == null) {
            LiveData<List<String>> deletedAutoCompleteWordsLive = autoCompleteDao.getDeletedAutoCompleteWordsLive();
            orderedDeletedWordsLive = getOrderedLive(deletedAutoCompleteWordsLive);
        }
        return orderedDeletedWordsLive;
    }

    @NotNull
    private LiveData<List<String>> getOrderedLive(LiveData<List<String>> autoCompleteWordsLive) {
        return Transformations.map(autoCompleteWordsLive, autoCompleteWords -> {
            autoCompleteWords.sort(String::compareTo);
            return autoCompleteWords;
        });
    }
}
