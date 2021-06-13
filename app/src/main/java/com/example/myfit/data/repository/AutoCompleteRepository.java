package com.example.myfit.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.repository.dao.AutoCompleteDao;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ViewModelScoped;

@Singleton
public class AutoCompleteRepository {
    private final AutoCompleteDao mAutoCompleteDao;
    private LiveData<Set<String>> mWordsLive, mDeletedWordsLive;

    @Inject
    public AutoCompleteRepository(@ApplicationContext Context context) {
        mAutoCompleteDao = AppDataBase.getsInstance(context).autoCompleteDao();
    }

    public LiveData<Set<String>> getAutoCompleteWordsLive() {
        if (mWordsLive == null) mWordsLive = getWordsLive(false);
        return mWordsLive;
    }

    public LiveData<Set<String>> getDeletedAutoCompleteWordsLive() {
        if (mDeletedWordsLive == null) mDeletedWordsLive = getWordsLive(true);
        return mDeletedWordsLive;
    }

    private LiveData<Set<String>> getWordsLive(boolean deleted) {
        return Transformations.map(mAutoCompleteDao.getAutoCompleteWordsLive(deleted), words -> {
            words.forEach(s -> s = s.trim());
            words.sort(String::compareTo);
            return new LinkedHashSet<>(words);
        });
    }
}
