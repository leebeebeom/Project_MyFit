package com.example.myfit.data.repository;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.folder.FolderTuple;
import com.example.myfit.data.model.tuple.ParentIdTuple;
import com.example.myfit.data.repository.dao.FolderDao;
import com.example.myfit.di.Qualifiers;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

public class FolderRepository extends BaseRepository {
    private final FolderDao folderDao;
    private final SharedPreferences listSortPreference;
    private final IntegerSharedPreferenceLiveData listSortPreferenceLive;
    private MutableLiveData<Long> insertIdLive;
    private MutableLiveData<Boolean> isExistingNameLive;
    private LiveData<List<FolderTuple>> tuplesLiveByParentId;
    private LiveData<List<List<FolderTuple>>> deletedClassifiedTuplesLive, searchTuplesLive, deletedSearchTuplesLive;
    private long parentId;

    @Inject
    public FolderRepository(FolderDao folderDao,
                            @Qualifiers.ListSortPreference SharedPreferences listSortPreference,
                            @Qualifiers.ListSortPreferenceLive IntegerSharedPreferenceLiveData listSortPreferenceLive) {
        this.folderDao = folderDao;
        this.listSortPreference = listSortPreference;
        this.listSortPreferenceLive = listSortPreferenceLive;
    }

    //to list
    public LiveData<List<FolderTuple>> getTuplesLiveByParentId(long parentId) {
        if (this.parentId != parentId) {
            this.parentId = parentId;
            tuplesLiveByParentId = Transformations.switchMap(listSortPreferenceLive, sort -> folderDao.getTuplesLiveByParentId(parentId, Sort.values()[sort]));
        }
        return tuplesLiveByParentId;
    }

    //to recycleBin
    public LiveData<List<List<FolderTuple>>> getDeletedClassifiedTuplesLive() {
        if (deletedClassifiedTuplesLive == null)
            deletedClassifiedTuplesLive = folderDao.getDeletedClassifiedTuplesLive();
        return deletedClassifiedTuplesLive;
    }

    //to searchView
    public LiveData<List<List<FolderTuple>>> getSearchTuplesListLive() {
        if (searchTuplesLive == null)
            searchTuplesLive = folderDao.getSearchTuplesListLive();
        return searchTuplesLive;
    }

    //to recycleBin search
    public LiveData<List<List<FolderTuple>>> getDeletedSearchFolderTuplesListLive() {
        if (deletedSearchTuplesLive == null)
            deletedSearchTuplesLive = folderDao.getDeletedSearchTuplesListLive();
        return deletedSearchTuplesLive;
    }

    //to treeView(disposable)
    public LiveData<List<FolderTuple>> getTuplesByParentIndex(int parentIndex) {
        MutableLiveData<List<FolderTuple>> tuplesLive = new MutableLiveData<>();
        new Thread(() -> {
            Sort sort = getSort();
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

    //from addFolder dialog
    public void isExistingName(String name, long parentId) {
        new Thread(() -> {
            boolean isExistName = folderDao.isExistingName(name, parentId);
            if (isExistingNameLive != null) isExistingNameLive.postValue(isExistName);
        }).start();
    }

    public MutableLiveData<Boolean> getIsExistingNameLive() {
        if (isExistingNameLive == null)
            isExistingNameLive = new MutableLiveData<>();
        return isExistingNameLive;
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

