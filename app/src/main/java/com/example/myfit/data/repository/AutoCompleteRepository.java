package com.example.myfit.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.repository.dao.AutoCompleteDao;

import java.util.List;

import javax.inject.Inject;

public class AutoCompleteRepository {
    private final AutoCompleteDao autoCompleteDao;

    @Inject
    public AutoCompleteRepository(AutoCompleteDao autoCompleteDao) {
        this.autoCompleteDao = autoCompleteDao;
    }

    public LiveData<List<String>> getAutoCompleteWordsLive() {
        LiveData<List<String>> autoCompleteListWordsLive = autoCompleteDao.getAutoCompleteWordsLive();

        return Transformations.map(autoCompleteListWordsLive, autoCompleteWords -> {
            autoCompleteWords.sort(String::compareTo);
            return autoCompleteWords;
        });
    }

    public LiveData<List<String>> getDeletedAutoCompleteWordsLive() {
        LiveData<List<String>> deletedAutoCompleteWordsLive = autoCompleteDao.getDeletedAutoCompleteWordsLive();

        return Transformations.map(deletedAutoCompleteWordsLive, deletedAutoCompleteWords -> {
            deletedAutoCompleteWords.sort(String::compareTo);
            return deletedAutoCompleteWords;
        });
    }
}
