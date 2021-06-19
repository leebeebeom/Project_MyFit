package com.leebeebeom.closetnote.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.leebeebeom.closetnote.data.AppDataBase;
import com.leebeebeom.closetnote.data.model.model.Folder;
import com.leebeebeom.closetnote.data.model.model.Size;
import com.leebeebeom.closetnote.data.repository.dao.BaseDao;
import com.leebeebeom.closetnote.data.repository.dao.FolderDao;
import com.leebeebeom.closetnote.data.tuple.ParentIdTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.FolderTuple;
import com.leebeebeom.closetnote.util.constant.SharedPreferenceKey;
import com.leebeebeom.closetnote.util.constant.Sort;
import com.leebeebeom.closetnote.util.sharedpreferencelive.BooleanSharedPreferenceLiveData;
import com.leebeebeom.closetnote.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import lombok.Getter;
import lombok.experimental.Accessors;

@Singleton
@Accessors(prefix = "m")
public class FolderRepository extends BaseRepository<Folder, FolderTuple> {
    private final FolderDao mFolderDao;
    private final SharedPreferences mListSortPreference, mFolderTogglePreference;
    @Getter
    private final IntegerSharedPreferenceLiveData mListSortPreferenceLive;
    @Getter
    private final BooleanSharedPreferenceLiveData mFolderTogglePreferenceLive;
    private MutableLiveData<Boolean> mExistingNameLive;
    private LiveData<List<List<FolderTuple>>> mDeletedClassifiedTuplesLive, mSearchTuplesLive, mDeletedSearchTuplesLive;
    private final LiveData<List<Folder>> mAllFoldersLive;

    @Inject
    public FolderRepository(@ApplicationContext Context context) {
        this.mFolderDao = AppDataBase.getsInstance(context).folderDao();
        this.mListSortPreference = context.getSharedPreferences(SharedPreferenceKey.SORT_LIST.getValue(), Context.MODE_PRIVATE);
        this.mListSortPreferenceLive = new IntegerSharedPreferenceLiveData(mListSortPreference, SharedPreferenceKey.SORT.getValue(), Sort.SORT_CUSTOM.getValue());
        this.mFolderTogglePreference = context.getSharedPreferences(SharedPreferenceKey.FOLDER_TOGGLE.getValue(), Context.MODE_PRIVATE);
        this.mFolderTogglePreferenceLive = new BooleanSharedPreferenceLiveData(mFolderTogglePreference, SharedPreferenceKey.FOLDER_TOGGLE.getValue(), false);

        this.mAllFoldersLive = mFolderDao.getAllModelsLiveWithContentSize();
    }

    public LiveData<List<FolderTuple>> getTuplesLiveByParentId(long parentId) {
        MediatorLiveData<List<FolderTuple>> mediatorLive = new MediatorLiveData<>();
        mediatorLive.addSource(mAllFoldersLive, folders -> setValue(parentId, mediatorLive, folders));
        mediatorLive.addSource(mListSortPreferenceLive, sortInt -> setValue(parentId, mediatorLive, getAllModelsLiveValue()));
        return mediatorLive;
    }

    private void setValue(long parentId, MediatorLiveData<List<FolderTuple>> mediatorLive, List<Folder> folders) {
        Stream<Folder> undeletedSortedStream = getUndeletedSortedStream(folders);
        Stream<Folder> undeletedSortedStreamByPrentId = getFolderStreamByPrentId(undeletedSortedStream, parentId);
        mediatorLive.setValue(getFolderTuples(undeletedSortedStreamByPrentId));
    }

    public LiveData<List<List<FolderTuple>>> getDeletedClassifiedTuplesLive() {
//        if (mDeletedClassifiedTuplesLive == null) {
//            mDeletedClassifiedTuplesLive = Transformations.map(mAllFoldersLive, folders -> {
//                Stream<Folder> deletedSortedStream = getDeletedSortedStream(folders);
//                return getClassifiedTuplesByParentIndex(getFolderTuples(deletedSortedStream));
//            });
//        }
        return mDeletedClassifiedTuplesLive;
    }

    public LiveData<List<List<FolderTuple>>> getSearchTuplesListLive() {
        if (mSearchTuplesLive == null)
            mSearchTuplesLive = Transformations.map(mAllFoldersLive, folders -> {
                Stream<Folder> unDeletedSearchStream = getUnDeletedSearchStream(folders);
                return getClassifiedTuplesByParentIndex(getFolderTuples(unDeletedSearchStream));
            });
        return mSearchTuplesLive;
    }

    public LiveData<List<List<FolderTuple>>> getDeletedSearchTuplesListLive() {
//        if (mDeletedSearchTuplesLive == null)
//            mDeletedSearchTuplesLive = Transformations.map(mAllFoldersLive, folders -> {
//                Stream<Folder> deletedSortedStream = getDeletedSearchStream(folders);
//                return getClassifiedTuplesByParentIndex(getFolderTuples(deletedSortedStream));
//            });
        return mDeletedSearchTuplesLive;
    }

    public LiveData<List<FolderTuple>> getTuplesByParentIndex(int parentIndex) {
        return Transformations.map(mAllFoldersLive, folders -> {
            Stream<Folder> undeletedSortedStreamByParentIndex = getUndeletedSortedStreamByParentIndex(folders, parentIndex);
            return getFolderTuples(undeletedSortedStreamByParentIndex);
        });
    }

    public void insert(String name, long parentId, int parentIndex) {
//        Integer largestSortNumber = getLargestSortNumber(getAllModelsLiveValue());
//        Folder folder = new Folder(parentIndex, largestSortNumber + 1, name.trim(), parentId);
//        insert(folder);
    }

    public LiveData<List<FolderTuple>> getFolderPathTuples(long id) {
        return Transformations.map(mAllFoldersLive, folders -> getFolderTuples(getFolderPath(folders, id).stream()));
    }

    private List<Folder> getFolderPath(List<Folder> allFolders, long id) {
        Optional<Folder> childOptional = getSingleOptional(getAllModelsLiveValue(), id);
        if (childOptional.isPresent()) {
            List<Folder> folderPath = new LinkedList<>();
            Folder childFolder = childOptional.get();
            folderPath.add(childFolder);
            addFolderPath(allFolders, folderPath);
            return folderPath;
        } else return new ArrayList<>();
    }

    private void addFolderPath(List<Folder> allFolders, List<Folder> folderPath) {
        Folder childFolder = folderPath.get(0);
        Optional<Folder> parentFolderOptional = getSingleOptional(allFolders, childFolder.getParentId());
        parentFolderOptional.ifPresent(parentFolder -> {
            folderPath.add(0, parentFolder);
            addFolderPath(allFolders, folderPath);
        });
    }

    public LiveData<Folder> getSingleLiveById(long id) {
        return Transformations.map(mAllFoldersLive, folders -> getSingleOptional(folders, id).orElse(Folder.getDummy()));
    }

    public void update(long id, String name) {
        Optional<Folder> folderOptional = getSingleOptional(getAllModelsLiveValue(), id);
        folderOptional.ifPresent(folder -> {
            folder.setName(name.trim());
            new Thread(() -> mFolderDao.update(folder)).start();
        });
    }

    //from move dialog
    public void move(long targetId, long[] ids) {
        List<Folder> folders = getModelsByIds(getAllModelsLiveValue(), ids);
        folders.forEach(folder -> folder.setParentId(targetId));
        update(folders);
    }

    //from delete dialog, restore dialog
    public void deleteOrRestore(long[] ids, SizeRepository sizeRepository) {
        List<Folder> allFolders = getAllModelsLiveValue();
        List<Size> allSizes = sizeRepository.getAllModelsLiveValue();
        List<Folder> folders = getModelsByIds(allFolders, ids);
        setDeleted(folders);
        new Thread(() -> {
            mFolderDao.update(folders);
            mFolderDao.updateChildrenParentDeleted(ids, allFolders, allSizes);
        }).start();
    }

    public MutableLiveData<Boolean> getExistingNameLive() {
        mExistingNameLive = new MutableLiveData<>();
        return mExistingNameLive;
    }

    public void isExistingName(String name, long parentId) {
        isExistingName(getAllModelsLiveValue(), parentId, name);
        new Thread(() -> {
            boolean isExistName = mFolderDao.isExistingName(name.trim(), parentId);
            if (mExistingNameLive != null) mExistingNameLive.postValue(isExistName);
        }).start();
    }

    //to treeView
    public LiveData<List<ParentIdTuple>> getParentIdTuplesByIds(long[] ids) {
        MutableLiveData<List<ParentIdTuple>> parentIdTuplesLive = new MutableLiveData<>();
        new Thread(() -> {
            List<ParentIdTuple> parentIdTuples = mFolderDao.getParentIdTuplesByIds(ids);
            parentIdTuplesLive.postValue(parentIdTuples);
        }).start();
        return parentIdTuplesLive;
    }

    public void folderToggleChange() {
        boolean folderToggle = mFolderTogglePreference.getBoolean(SharedPreferenceKey.FOLDER_TOGGLE.getValue(), false);
        mFolderTogglePreference.edit().putBoolean(SharedPreferenceKey.FOLDER_TOGGLE.getValue(), !folderToggle).apply();
    }

    private List<FolderTuple> getFolderTuples(Stream<Folder> stream) {
        return stream.map(FolderTuple::new).collect(Collectors.toList());
    }

    @Override
    protected SharedPreferences getSortPreference() {
        return mListSortPreference;
    }

    @Override
    protected SharedPreferences getLargestSortNumberPreference() {
        //TODO
        return null;
    }

    @Override
    protected BaseDao<Folder, FolderTuple> getDao() {
        return mFolderDao;
    }

    @Override
    protected LiveData<List<Folder>> getModelsLive() {
        return mAllFoldersLive;
    }

}

