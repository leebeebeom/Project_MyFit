package com.example.myfit.data.repository;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myfit.data.model.folder.Folder;
import com.example.myfit.data.model.folder.FolderTuple;
import com.example.myfit.data.model.tuple.ParentIdTuple;
import com.example.myfit.data.repository.dao.FolderDao;
import com.example.myfit.di.Qualifiers;
import com.example.myfit.util.constant.Sort;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ViewModelScoped;

import static com.example.myfit.di.DataModule.FOLDER_TOGGLE;

@ViewModelScoped
public class FolderRepository extends BaseRepository<FolderTuple> {
    private final FolderDao mFolderDao;
    private final SharedPreferences mListSortPreference;
    private final SharedPreferences mFolderTogglePreference;
    private MutableLiveData<Long> mInsertIdLive;
    private MutableLiveData<Boolean> mExistingNameLive;
    private LiveData<List<List<FolderTuple>>> mDeletedClassifiedTuplesLive, mSearchTuplesLive, mDeletedSearchTuplesLive;

    @Inject
    public FolderRepository(FolderDao folderDao,
                            @Qualifiers.ListSortPreference SharedPreferences listSortPreference,
                            @Qualifiers.FolderTogglePreference SharedPreferences folderTogglePreference) {
        this.mFolderDao = folderDao;
        this.mListSortPreference = listSortPreference;
        this.mFolderTogglePreference = folderTogglePreference;
    }

    //to list
    public LiveData<List<FolderTuple>> getTuplesLiveByParentId(long parentId) {
        return mFolderDao.getTuplesLiveByParentId(parentId);
    }

    //to recycleBin
    public LiveData<List<List<FolderTuple>>> getDeletedClassifiedTuplesLive() {
        if (mDeletedClassifiedTuplesLive == null)
            mDeletedClassifiedTuplesLive = mFolderDao.getDeletedClassifiedTuplesLive();
        return mDeletedClassifiedTuplesLive;
    }

    //to searchView
    public LiveData<List<List<FolderTuple>>> getSearchTuplesListLive() {
        if (mSearchTuplesLive == null)
            mSearchTuplesLive = mFolderDao.getSearchTuplesListLive();
        return mSearchTuplesLive;
    }

    //to recycleBin search
    public LiveData<List<List<FolderTuple>>> getDeletedSearchFolderTuplesListLive() {
        if (mDeletedSearchTuplesLive == null)
            mDeletedSearchTuplesLive = mFolderDao.getDeletedSearchTuplesListLive();
        return mDeletedSearchTuplesLive;
    }

    //to treeView(disposable)
    public LiveData<List<FolderTuple>> getTuplesByParentIndex(int parentIndex) {
        MutableLiveData<List<FolderTuple>> tuplesLive = new MutableLiveData<>();
        new Thread(() -> {
            Sort sort = getSort();
            List<FolderTuple> folderTuples = mFolderDao.getTuplesByParentIndex(parentIndex, sort);
            tuplesLive.postValue(folderTuples);
        }).start();
        return tuplesLive;
    }

    public LiveData<List<FolderTuple>> getFolderPathTuples(long id) {
        MutableLiveData<List<FolderTuple>> folderPathTuplesLive = new MutableLiveData<>();
        new Thread(() -> {
            LinkedList<FolderTuple> folderPathIds = mFolderDao.getFolderPathTuples(id);
            folderPathTuplesLive.postValue(folderPathIds);
        }).start();
        return folderPathTuplesLive;
    }

    //to treeView(disposable)
    public LiveData<FolderTuple> getTupleById(long id) {
        MutableLiveData<FolderTuple> folderTupleLive = new MutableLiveData<>();
        new Thread(() -> {
            FolderTuple folderTuple = mFolderDao.getTupleById(id);
            folderTupleLive.postValue(folderTuple);
        }).start();
        return folderTupleLive;
    }

    //to list
    public LiveData<Folder> getSingleLiveById(long id) {
        return mFolderDao.getSingleLiveById(id);
    }

    public MutableLiveData<Long> getInsertIdLive() {
        mInsertIdLive = new MutableLiveData<>();
        return mInsertIdLive;
    }

    //from addFolder dialog
    public void insert(String name, long parentId, int parentIndex) {
        new Thread(() -> {
            long insertId = mFolderDao.insert(name, parentId, parentIndex);
            if (mInsertIdLive != null) mInsertIdLive.postValue(insertId);
        }).start();
    }

    //from editFolderName dialog
    public void update(long id, String name) {
        new Thread(() -> mFolderDao.update(id, name)).start();
    }

    @Override
    //from adapter drag drop
    public void updateTuples(List<FolderTuple> folderTuples) {
        new Thread(() -> mFolderDao.update(folderTuples)).start();
    }

    //from move dialog
    public void move(long targetId, long[] ids) {
        new Thread(() -> mFolderDao.move(targetId, ids)).start();
    }

    @Override
    //from delete dialog, restore dialog
    public void deleteOrRestore(long[] ids, boolean isDeleted) {
        new Thread(() -> mFolderDao.deleteOrRestore(ids, isDeleted)).start();
    }

    @Override
    protected SharedPreferences getPreference() {
        return mListSortPreference;
    }

    public MutableLiveData<Boolean> getExistingNameLive() {
        mExistingNameLive = new MutableLiveData<>();
        return mExistingNameLive;
    }

    //from addFolder dialog
    public void isExistingName(String name, long parentId) {
        new Thread(() -> {
            boolean isExistName = mFolderDao.isExistingName(name, parentId);
            if (mExistingNameLive != null) mExistingNameLive.postValue(isExistName);
        }).start();
    }

    //to treeView
    public LiveData<ParentIdTuple[]> getParentIdTuplesByIds(long[] ids) {
        MutableLiveData<ParentIdTuple[]> parentIdTuplesLive = new MutableLiveData<>();
        new Thread(() -> {
            ParentIdTuple[] parentIdTuples = mFolderDao.getParentIdTuplesByIds(ids);
            parentIdTuplesLive.postValue(parentIdTuples);
        });
        return parentIdTuplesLive;
    }

    public void folderToggleChange() {
        boolean folderToggle = mFolderTogglePreference.getBoolean(FOLDER_TOGGLE, false);
        mFolderTogglePreference.edit().putBoolean(FOLDER_TOGGLE, !folderToggle).apply();
    }
}

