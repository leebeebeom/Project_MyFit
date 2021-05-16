package com.example.myfit.data.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.tuple.CategoryFolderTuple;
import com.example.myfit.data.repository.dao.FolderDao;
import com.example.myfit.util.IntegerSharedPreferenceLiveData;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.constant.SortValue;

import java.util.LinkedList;
import java.util.List;

public class FolderRepository {
    private final FolderDao folderDao;
    private final SharedPreferences listSortPreference;

    public FolderRepository(Context context) {
        this.folderDao = AppDataBase.getsInstance(context).folderDao();
        this.listSortPreference = context.getSharedPreferences(Sort.SORT_LIST.getText(), SortValue.SORT_CUSTOM.getValue());
    }

    //to recycleBin
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedClassifiedTuplesLive() {
        return folderDao.getDeletedClassifiedTuplesLive();
    }

    //to list
    public LiveData<List<CategoryFolderTuple>> getTuplesLiveByParentId(long parentId) {
        IntegerSharedPreferenceLiveData listSortPreferenceLive =
                new IntegerSharedPreferenceLiveData(listSortPreference, Sort.SORT_LIST.getText(), SortValue.SORT_CUSTOM.getValue());
        return Transformations.switchMap(listSortPreferenceLive, sort -> folderDao.getTuplesLiveByParentId(parentId, sort));
    }

    //to searchView
    public LiveData<List<List<CategoryFolderTuple>>> getSearchTuplesListLive(String keyWord) {
        return folderDao.getSearchTuplesListLive(keyWord);
    }

    public LiveData<List<List<CategoryFolderTuple>>> getDeletedSearchFolderTuplesListLive(String keyword) {
        return folderDao.getDeletedSearchFolderTuplesListLive(keyword);
    }

    //to treeView(disposable)
    public LiveData<List<CategoryFolderTuple>> getTuplesByParentIndex(byte parentIndex) {
        MutableLiveData<List<CategoryFolderTuple>> tuplesLive = new MutableLiveData<>();
        //TODO test
        new Thread(() -> {
            try {
                int sort = getSort();
                List<CategoryFolderTuple> folderTuples = folderDao.getTuplesByParentIndex(parentIndex, sort);
                tuplesLive.postValue(folderTuples);
            } catch (Exception e) {
                logE(e);
            }
        }).start();
        return tuplesLive;
    }

    //to treeView(disposable)
    public LiveData<CategoryFolderTuple> getTupleById(long id) {
        MutableLiveData<CategoryFolderTuple> folderTupleLive = new MutableLiveData<>();
        //TODO test
        new Thread(() -> {
            try {
                CategoryFolderTuple folderTuple = folderDao.getTupleById(id);
                folderTupleLive.postValue(folderTuple);
            } catch (Exception e) {
                logE(e);
            }
        }).start();
        return folderTupleLive;
    }

    //to list
    public LiveData<Folder> getSingleLiveById(long id) {
        return folderDao.getSingleLiveById(id);
    }

    //from addFolder dialog
    public LiveData<Long> insert(String name, long parentId, byte parentIndex) {
        MutableLiveData<Long> insertIdLive = new MutableLiveData<>();
        //TODO test
        new Thread(() -> {
            try {
                long insertId = folderDao.insert(name, parentId, parentIndex);
                insertIdLive.postValue(insertId);
            } catch (Exception e) {
                logE(e);
            }
        }).start();
        return insertIdLive;
    }

    //from editFolderName dialog
    public void update(long id, String name) {
        new Thread(() -> folderDao.update(id, name)).start();
    }

    //from adapter drag drop
    public void updateFolders(LinkedList<CategoryFolderTuple> folderTuple) {
        new Thread(() -> folderDao.update(folderTuple)).start();
    }

    //from move dialog
    public void move(long targetId, long[] ids) {
        new Thread(() -> folderDao.move(targetId, ids)).start();
    }

    //from deleteSelectedItems, restore dialog
    public void deleteOrRestore(long[] ids, boolean isDeleted) {
        new Thread(() -> folderDao.deleteOrRestore(ids, isDeleted)).start();
    }

    //from addFolder dialog
    public LiveData<Boolean> isExistingName(String name, long parentId) {
        MutableLiveData<Boolean> isExistNameLive = new MutableLiveData<>();
        new Thread(() -> {
            try {
                boolean isExistName = folderDao.isExistingName(name, parentId);
                isExistNameLive.postValue(isExistName);
            } catch (Exception e) {
                logE(e);
            }
        }).start();
        return isExistNameLive;
    }

    public void setListSortPreferenceValue(int sort) {
        SharedPreferences.Editor editor = listSortPreference.edit();
        editor.putInt(Sort.SORT_LIST.getText(), sort);
        editor.apply();
    }

    private int getSort() {
        return listSortPreference.getInt(Sort.SORT_LIST.getText(), SortValue.SORT_CUSTOM.getValue());
    }

    private void logE(Exception e) {
        Log.e("에러", "logE: " + e.getMessage(), e);
    }
}

