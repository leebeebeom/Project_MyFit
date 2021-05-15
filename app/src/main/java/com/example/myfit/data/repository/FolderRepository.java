package com.example.myfit.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

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

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

public class FolderRepository extends BaseRepository implements FolderDao.FolderDaoInterface {
    private final FolderDao folderDao;
    private final SharedPreferences listSortPreference;

    @Inject
    public FolderRepository(Context context) {
        super(context);
        this.folderDao = AppDataBase.getsInstance(context).folderDao();
        listSortPreference = context.getSharedPreferences(Sort.SORT_LIST.getText(), SortValue.SORT_CUSTOM.getValue());
    }

    @Override
    //to recycleBin
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedClassifiedFolderTuplesLive() {
        return folderDao.getDeletedClassifiedFolderTuplesLive();
    }

    @Override
    //to list
    public LiveData<List<CategoryFolderTuple>> getFolderTuplesLiveByParentId(long parentId) {
        IntegerSharedPreferenceLiveData listSortPreferenceLive = getListSortPreferenceLive();
        return Transformations.switchMap(listSortPreferenceLive, sort -> folderDao.getFolderTuplesLiveByParentId(parentId, sort));
    }

    @Override
    //to search
    public LiveData<List<List<CategoryFolderTuple>>> getSearchFolderTuplesListLive(String keyWord) {
        IntegerSharedPreferenceLiveData listSortPreferenceLive = getListSortPreferenceLive();
        return Transformations.switchMap(listSortPreferenceLive, sort -> folderDao.getSearchFolderTuplesListLive(keyWord, sort));
    }

    @NotNull
    private IntegerSharedPreferenceLiveData getListSortPreferenceLive() {
        return new IntegerSharedPreferenceLiveData(listSortPreference, Sort.SORT_LIST.getText(), SortValue.SORT_CUSTOM.getValue());
    }

    @Override
    //to recycleBin search
    public LiveData<List<List<CategoryFolderTuple>>> getDeletedSearchFolderTuplesListLive(String keyWord) {
        return folderDao.getDeletedSearchFolderTuplesListLive(keyWord);
    }

    @Override
    //to treeView(disposable)
    public LiveData<List<CategoryFolderTuple>> getFolderTuplesByParentIndex(byte parentIndex) {
        MutableLiveData<List<CategoryFolderTuple>> folderTuplesLive = new MutableLiveData<>();
        new Thread(() -> {
            try {
                int sort = getListSort();
                List<CategoryFolderTuple> folderTuples = folderDao.getFolderTuplesByParentIndex(parentIndex, sort);
                folderTuplesLive.postValue(folderTuples);
            } catch (Exception e) {
                logE(e);
            }
        });
        return folderTuplesLive;
    }

    @Override
    //to treeView(disposable)
    public LiveData<CategoryFolderTuple> getFolderTupleById(long id) {
        MutableLiveData<CategoryFolderTuple> folderTupleLive = new MutableLiveData<>();
        new Thread(() -> {
            try {
                CategoryFolderTuple folderTuple = folderDao.getFolderTupleById(id);
                folderTupleLive.postValue(folderTuple);
            } catch (Exception e) {
                logE(e);
            }
        });
        return folderTupleLive;
    }

    @Override
    //to list
    public LiveData<Folder> getFolderLiveById(long id) {
        return folderDao.getFolderLiveById(id);
    }

    @Override
    //from addFolder dialog
    public LiveData<Long> insertFolder(String name, long parentId, byte parentIndex) {
        MutableLiveData<Long> insertIdLive = new MutableLiveData<>();
        new Thread(() -> {
            try {
                long insertId = folderDao.insertFolder(name, parentId, parentIndex);
                insertIdLive.postValue(insertId);
            } catch (Exception e) {
                logE(e);
            }
        }).start();
        return insertIdLive;
    }

    @Override
    //from editFolderName dialog
    public void updateFolder(long id, String name) {
        new Thread(() -> folderDao.updateFolder(id, name)).start();
    }

    @Override
    //from adapter drag drop
    public void updateFolders(LinkedList<CategoryFolderTuple> folderTuple) {
        new Thread(() -> folderDao.updateTuples(folderTuple)).start();
    }

    @Override
    //from move dialog
    public void moveFolders(long targetId, long[] folderIds) {
        new Thread(() -> folderDao.moveFolders(targetId, folderIds)).start();
    }

    @Override
    //from deleteSelectedItems, restore dialog
    public void deleteOrRestoreFolders(long[] folderIds, boolean isDeleted) {
        new Thread(() -> folderDao.deleteOrRestoreFolders(folderIds, isDeleted)).start();
    }

    @Override
    //from addFolder dialog
    public LiveData<Boolean> isExistingFolderName(String folderName, long parentId) {
        MutableLiveData<Boolean> existFolderNameLive = new MutableLiveData<>();
        new Thread(() -> {
            try {
                boolean isExistCategoryName = folderDao.isExistingFolderName(folderName, parentId);
                existFolderNameLive.postValue(isExistCategoryName);
            } catch (Exception e) {
                logE(e);
            }
        }).start();
        return existFolderNameLive;
    }

    public void setListSortPreferenceValue(int sort) {
        SharedPreferences.Editor editor = listSortPreference.edit();
        editor.putInt(Sort.SORT_LIST.getText(), sort);
        editor.apply();
    }

    private int getListSort() {
        return listSortPreference.getInt(Sort.SORT_LIST.getText(), SortValue.SORT_CUSTOM.getValue());
    }
}

