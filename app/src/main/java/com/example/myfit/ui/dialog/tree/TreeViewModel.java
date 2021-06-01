package com.example.myfit.ui.dialog.tree;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.data.repository.SizeRepository;
import com.example.myfit.data.tuple.ParentIdTuple;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.data.tuple.tuple.FolderTuple;

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
    private final MutableLiveData<CategoryTuple> mAddedCategoryTupleLive;
    private final MutableLiveData<FolderTuple> mAddedFolderTupleLive;

    @Inject
    public TreeViewModel(CategoryRepository categoryRepository, FolderRepository folderRepository, SizeRepository sizeRepository) {
        this.mCategoryRepository = categoryRepository;
        this.mFolderRepository = folderRepository;
        this.mSizeRepository = sizeRepository;

        this.mAddedCategoryTupleLive = mCategoryRepository.getAddedTupleLive();
        this.mAddedFolderTupleLive = mFolderRepository.getAddedTupleLive();
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

    public MutableLiveData<CategoryTuple> getAddedCategoryTupleLive() {
        return mAddedCategoryTupleLive;
    }

    public MutableLiveData<FolderTuple> getAddedFolderTupleLive() {
        return mAddedFolderTupleLive;
    }
}
