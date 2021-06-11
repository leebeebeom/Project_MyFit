package com.example.myfit.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myfit.data.AppDataBase;
import com.example.myfit.data.model.model.Folder;
import com.example.myfit.data.repository.dao.FolderDao;
import com.example.myfit.data.tuple.ParentIdTuple;
import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.util.constant.SharedPreferenceKey;
import com.example.myfit.util.constant.Sort;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.android.scopes.ViewModelScoped;

@ViewModelScoped
public class FolderRepository extends BaseRepository<FolderTuple> {
    private final FolderDao mFolderDao;
    private final SharedPreferences mListSortPreference;
    private final SharedPreferences mFolderTogglePreference;
    private MutableLiveData<Boolean> mExistingNameLive;
    private LiveData<List<List<FolderTuple>>> mDeletedClassifiedTuplesLive, mSearchTuplesLive, mDeletedSearchTuplesLive;
    private MutableLiveData<FolderTuple> mAddedTupleLive;

    @Inject
    public FolderRepository(@ApplicationContext Context context) {
        this.mFolderDao = AppDataBase.getsInstance(context).folderDao();
        this.mListSortPreference = context.getSharedPreferences(SharedPreferenceKey.SORT_LIST.getValue(), Context.MODE_PRIVATE);
        this.mFolderTogglePreference = context.getSharedPreferences(SharedPreferenceKey.FOLDER_TOGGLE.getValue(), Context.MODE_PRIVATE);
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

    //to searchView, recycleBin search
    public LiveData<List<List<FolderTuple>>> getSearchTuplesListLive(boolean deleted) {
        if (!deleted) {
            if (mSearchTuplesLive == null)
                mSearchTuplesLive = mFolderDao.getSearchTuplesListLive(false);
            return mSearchTuplesLive;
        } else {
            if (mDeletedSearchTuplesLive == null)
                mDeletedSearchTuplesLive = mFolderDao.getSearchTuplesListLive(true);
            return mDeletedSearchTuplesLive;
        }
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

    public MutableLiveData<FolderTuple> getAddedTupleLive() {
        mAddedTupleLive = new MutableLiveData<>();
        return mAddedTupleLive;
    }

    //from addFolder dialog
    public void insert(String name, long parentId, int parentIndex) {
        new Thread(() -> {
            long insertId = mFolderDao.insert(name, parentId, parentIndex);
            if (mAddedTupleLive != null) {
                FolderTuple addedTuple = mFolderDao.getTupleById(insertId);
                mAddedTupleLive.postValue(addedTuple);
            }
        }).start();
    }

    //to listFragment
    public LiveData<List<FolderTuple>> getFolderPathTuples(long id) {
        MutableLiveData<List<FolderTuple>> folderPathTuplesLive = new MutableLiveData<>();
        new Thread(() -> {
            LinkedList<FolderTuple> folderPathIds = mFolderDao.getFolderPathTuples(id);
            folderPathTuplesLive.postValue(folderPathIds);
        }).start();
        return folderPathTuplesLive;
    }

    //to listFragment
    public LiveData<Folder> getSingleLiveById(long id) {
        return mFolderDao.getSingleLiveById(id);
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
    public void deleteOrRestore(long[] ids) {
        new Thread(() -> mFolderDao.deleteOrRestore(ids)).start();
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
            boolean isExistName = mFolderDao.isExistingName(name.trim(), parentId);
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
        boolean folderToggle = mFolderTogglePreference.getBoolean(SharedPreferenceKey.FOLDER_TOGGLE.getValue(), false);
        mFolderTogglePreference.edit().putBoolean(SharedPreferenceKey.FOLDER_TOGGLE.getValue() , !folderToggle).apply();
    }
}

