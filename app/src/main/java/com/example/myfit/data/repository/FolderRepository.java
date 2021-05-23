package com.example.myfit.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.folder.FolderTuple;
import com.example.myfit.data.model.tuple.ParentIdTuple;
import com.example.myfit.data.repository.dao.FolderDao;
import com.example.myfit.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.constant.SortValue;

import java.util.LinkedList;
import java.util.List;

public class FolderRepository extends BaseRepository{
    private final FolderDao folderDao;
    private final SharedPreferences listSortPreference;
    private MutableLiveData<Long> insertIdLive;

    public FolderRepository(Context context) {
        this.folderDao = AppDataBase.getsInstance(context).folderDao();
        this.listSortPreference = context.getSharedPreferences(Sort.SORT_LIST.getText(), Context.MODE_PRIVATE);
    }

    //to list
    public LiveData<List<FolderTuple>> getTuplesLiveByParentId(long parentId) {
        IntegerSharedPreferenceLiveData listSortPreferenceLive =
                new IntegerSharedPreferenceLiveData(listSortPreference, Sort.SORT_LIST.getText(), SortValue.SORT_CUSTOM.getValue());
        return Transformations.switchMap(listSortPreferenceLive, sort -> folderDao.getTuplesLiveByParentId(parentId, sort));
    }

    //to recycleBin
    public LiveData<List<List<FolderTuple>>> getDeletedClassifiedTuplesLive() {
        return folderDao.getDeletedClassifiedTuplesLive();
    }

    //to searchView
    public LiveData<List<List<FolderTuple>>> getSearchTuplesListLive() {
        return folderDao.getSearchTuplesListLive();
    }

    //to recycleBin search
    public LiveData<List<List<FolderTuple>>> getDeletedSearchFolderTuplesListLive() {
        return folderDao.getDeletedSearchTuplesListLive();
    }

    //to treeView(disposable)
    public LiveData<List<FolderTuple>> getTuplesByParentIndex(int parentIndex) {
        MutableLiveData<List<FolderTuple>> tuplesLive = new MutableLiveData<>();
        new Thread(() -> {
                int sort = getSort();
                List<FolderTuple> folderTuples = folderDao.getTuplesByParentIndex(parentIndex, sort);
                tuplesLive.postValue(folderTuples);
        }).start();
        return tuplesLive;
    }

    //to treeView(disposable)
    public LiveData<FolderTuple> getTupleById(long id) {
        MutableLiveData<FolderTuple> folderTupleLive = new MutableLiveData<>();
        new Thread(() -> {
                FolderTuple folderTuple = folderDao.getTupleById(id);
                folderTupleLive.postValue(folderTuple);
        }).start();
        return folderTupleLive;
    }

    //to list
    public LiveData<Folder> getSingleLiveById(long id) {
        return folderDao.getSingleLiveById(id);
    }

    //from addFolder dialog
    public void insert(String name, long parentId, int parentIndex) {
        new Thread(() -> {
            long insertId = folderDao.insert(name, parentId, parentIndex);
            if (insertIdLive != null) insertIdLive.postValue(insertId);
        }).start();
    }

    public MutableLiveData<Long> getInsertIdLive() {
        if (insertIdLive == null)
            insertIdLive = new MutableLiveData<>();
        return insertIdLive;
    }

    //from editFolderName dialog
    public void update(long id, String name) {
        new Thread(() -> folderDao.update(id, name)).start();
    }

    //from adapter drag drop
    public void updateFolders(LinkedList<FolderTuple> folderTuple) {
        new Thread(() -> folderDao.update(folderTuple)).start();
    }

    //from move dialog
    public void move(long targetId, long[] ids) {
        new Thread(() -> folderDao.move(targetId, ids)).start();
    }

    @Override
    //from delete dialog, restore dialog
    public void deleteOrRestore(long[] ids, boolean isDeleted) {
        new Thread(() -> folderDao.deleteOrRestore(ids, isDeleted)).start();
    }

    @Override
    protected SharedPreferences getPreference() {
        return listSortPreference;
    }

    @Override
    protected String getPreferenceKey() {
        return Sort.SORT_LIST.getText();
    }

    //from addFolder dialog
    public LiveData<Boolean> isExistingName(String name, long parentId) {
        MutableLiveData<Boolean> isExistNameLive = new MutableLiveData<>();
        new Thread(() -> {
            boolean isExistName = folderDao.isExistingName(name, parentId);
            isExistNameLive.postValue(isExistName);
        }).start();
        return isExistNameLive;
    }

    //to treeView
    public LiveData<ParentIdTuple[]> getParentIdTuplesByIds(long[] ids) {
        MutableLiveData<ParentIdTuple[]> parentIdTuplesLive = new MutableLiveData<>();
        new Thread(() -> {
            ParentIdTuple[] parentIdTuples = folderDao.getParentIdTuplesByIds(ids);
            parentIdTuplesLive.postValue(parentIdTuples);
        });
        return parentIdTuplesLive;
    }
}

