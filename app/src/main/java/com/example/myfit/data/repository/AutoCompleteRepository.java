package com.example.myfit.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.repository.dao.AutoCompleteDao;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

public class AutoCompleteRepository {
    private final AutoCompleteDao autoCompleteDao;

    @Inject
    public AutoCompleteRepository(Context context) {
        this.autoCompleteDao = AppDataBase.getsInstance(context).autoCompleteDao();
    }

    public LiveData<List<String>> getAutoCompleteWordsLive() {
        LiveData<List<String>> autoCompleteWordsLive = autoCompleteDao.getAutoCompleteWordsLive();

        return getOrderedLive(autoCompleteWordsLive);
    }

    public LiveData<List<String>> getDeletedAutoCompleteWordsLive() {
        LiveData<List<String>> deletedAutoCompleteWordsLive = autoCompleteDao.getDeletedAutoCompleteWordsLive();

        return getOrderedLive(deletedAutoCompleteWordsLive);
    }

    @NotNull
    private LiveData<List<String>> getOrderedLive(LiveData<List<String>> autoCompleteWordsLive) {
        return Transformations.map(autoCompleteWordsLive, autoCompleteWords -> {
            autoCompleteWords.sort(String::compareTo);
            return autoCompleteWords;
        });
    }
}
