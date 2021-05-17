package com.example.myfit.data.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.category.Category;
import com.example.myfit.data.model.tuple.CategoryFolderTuple;
import com.example.myfit.data.repository.dao.BaseDao;

import java.util.List;

public class BaseRepository {
    private final BaseDao<Category, CategoryFolderTuple> baseDao;

    public BaseRepository(Context context) {
        AppDataBase appDataBase = AppDataBase.getsInstance(context);
        baseDao = appDataBase.baseDao();
    }

    public LiveData<List<String>> getAutoCompleteWordsLive() {
        LiveData<List<String>> autoCompleteListWordsLive = baseDao.getAutoCompleteWordsLive();

        return Transformations.map(autoCompleteListWordsLive, autoCompleteWords -> {
            autoCompleteWords.sort(String::compareTo);
            return autoCompleteWords;
        });
    }

    public LiveData<List<String>> getDeletedAutoCompleteWordsLive() {
        LiveData<List<String>> deletedAutoCompleteWordsLive = baseDao.getDeletedAutoCompleteWordsLive();

        return Transformations.map(deletedAutoCompleteWordsLive, deletedAutoCompleteWords -> {
            deletedAutoCompleteWords.sort(String::compareTo);
            return deletedAutoCompleteWords;
        });
    }

    protected void logE(Exception e) {
        Log.e("에러", "logE: " + e.getMessage(), e);
    }
}