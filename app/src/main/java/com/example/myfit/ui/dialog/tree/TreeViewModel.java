package com.example.myfit.ui.dialog.tree;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.data.tuple.ParentIdTuple;
import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.data.repository.SizeRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class TreeViewModel extends ViewModel {
    private static final String CATEGORY_INSERT_ID = "category insert id";
    private static final String FOLDER_INSERT_ID = "folder insert id";

    private final CategoryRepository mCategoryRepository;
    private final FolderRepository mFolderRepository;
    private final SizeRepository mSizeRepository;
    private final SavedStateHandle mSavedStateHandle;
    private final MediatorLiveData<CategoryTuple> mCategoryTupleMutable = new MediatorLiveData<>();
    private final MediatorLiveData<FolderTuple> mFolderTupleMutable = new MediatorLiveData<>();

    @Inject
    public TreeViewModel(SavedStateHandle savedStateHandle, CategoryRepository categoryRepository, FolderRepository folderRepository, SizeRepository sizeRepository) {
        this.mSavedStateHandle = savedStateHandle;
        this.mCategoryRepository = categoryRepository;
        this.mFolderRepository = folderRepository;
        this.mSizeRepository = sizeRepository;

        categoryTupleMutableAddSource();
        folderTupleMutableAddSource();
    }

    private void categoryTupleMutableAddSource() {
        LiveData<CategoryTuple> categoryTupleLive = Transformations.switchMap(mSavedStateHandle.getLiveData(CATEGORY_INSERT_ID),
                insertId -> mCategoryRepository.getTupleById((Long) insertId));
        mCategoryTupleMutable.addSource(categoryTupleLive, mCategoryTupleMutable::setValue);
    }

    private void folderTupleMutableAddSource() {
        LiveData<FolderTuple> folderTupleLive = Transformations.switchMap(mSavedStateHandle.getLiveData(FOLDER_INSERT_ID),
                insertId -> mFolderRepository.getTupleById((Long) insertId));
        mFolderTupleMutable.addSource(folderTupleLive, mFolderTupleMutable::setValue);
    }

    public LiveData<List<CategoryTuple>> getCategoryTuplesLive(int parentIndex) {
        return mCategoryRepository.getTuplesByParentIndex(parentIndex);
    }

    public LiveData<List<FolderTuple>> getFolderTuplesLive(int parentIndex) {
        return mFolderRepository.getTuplesByParentIndex(parentIndex);
    }

    public LiveData<ParentIdTuple[]> getFolderParentIdTuplesLive(long[] selectedFolderIds) {
        return mFolderRepository.getParentIdTuplesByIds(selectedFolderIds);
    }

    public LiveData<ParentIdTuple[]> getSizeParentIdTuplesLive(long[] selectedSizeIds) {
        return mSizeRepository.getParentIdTuplesByIds(selectedSizeIds);
    }

    public MutableLiveData<Long> getCategoryInsertIdLive() {
        return mCategoryRepository.getInsertIdLive();
    }

    public MutableLiveData<Long> getFolderInsertIdLive() {
        return mFolderRepository.getInsertIdLive();
    }

    public MutableLiveData<CategoryTuple> getCategoryTupleMutable() {
        return mCategoryTupleMutable;
    }

    public void setCategoryInsertId(long id) {
        mSavedStateHandle.set(CATEGORY_INSERT_ID, id);
    }

    public MutableLiveData<FolderTuple> getFolderTupleMutable() {
        return mFolderTupleMutable;
    }

    public void setFolderInsertId(long id) {
        mSavedStateHandle.set(FOLDER_INSERT_ID, id);
    }
}
