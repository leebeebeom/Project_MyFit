package com.example.myfit.ui.dialog.tree;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.data.repository.SizeRepository;
import com.example.myfit.data.tuple.ParentIdTuple;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.util.CommonUtil;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.experimental.Accessors;

@HiltViewModel
@Accessors(prefix = "m")
@Getter
public class TreeViewModel extends ViewModel {
    private static final String CATEGORY_INSERT_ID = "category insert id";
    private static final String FOLDER_INSERT_ID = "folder insert id";
    private static final String PARENT_INDEX = "parent index";
    private static final String SELECTED_FOLDER_IDS = "selected folder ids";
    private static final String SELECTED_SIZE_IDS = "selected size ids";

    private final CategoryRepository mCategoryRepository;
    private final FolderRepository mFolderRepository;
    private final SizeRepository mSizeRepository;
    private final MutableLiveData<CategoryTuple> mAddedCategoryTupleLive;
    private final MutableLiveData<FolderTuple> mAddedFolderTupleLive;
    private final SavedStateHandle mStateHandle;
    private final LiveData<ParentIdTuple[]> mFolderParentIdTuplesLive;
    private final LiveData<ParentIdTuple[]> mSizeParentIdTuplesLive;
    private final LiveData<List<CategoryTuple>> mCategoryTuplesLive;
    private final LiveData<List<FolderTuple>> mFolderTuplesLive;
    private String mParentCategory;

    @Inject
    public TreeViewModel(SavedStateHandle stateHandle, CategoryRepository categoryRepository, FolderRepository folderRepository, SizeRepository sizeRepository) {
        this.mStateHandle = stateHandle;
        this.mCategoryRepository = categoryRepository;
        this.mFolderRepository = folderRepository;
        this.mSizeRepository = sizeRepository;

        this.mAddedCategoryTupleLive = mCategoryRepository.getAddedTupleLive();
        this.mAddedFolderTupleLive = mFolderRepository.getAddedTupleLive();

        mCategoryTuplesLive = Transformations.switchMap(stateHandle.getLiveData(PARENT_INDEX),
                parentIndex -> mCategoryRepository.getTuplesByParentIndex((Integer) parentIndex));
        mFolderTuplesLive = Transformations.switchMap(stateHandle.getLiveData(PARENT_INDEX),
                parentIndex -> mFolderRepository.getTuplesByParentIndex((Integer) parentIndex));
        mFolderParentIdTuplesLive = Transformations.switchMap(stateHandle.getLiveData(SELECTED_FOLDER_IDS),
                selectedFolderIds -> mFolderRepository.getParentIdTuplesByIds((long[]) selectedFolderIds));
        mSizeParentIdTuplesLive = Transformations.switchMap(stateHandle.getLiveData(SELECTED_SIZE_IDS),
                selectedSizeIds -> mSizeRepository.getParentIdTuplesByIds((long[]) selectedSizeIds));
    }

    public void setParentIndex(int parentIndex) {
        mStateHandle.set(PARENT_INDEX, parentIndex);
        this.mParentCategory = CommonUtil.getParentCategory(parentIndex);
    }

    public void setSelectedFolderIds(long[] selectedFolderIds) {
        mStateHandle.set(SELECTED_FOLDER_IDS, selectedFolderIds);
    }

    public void setSelectedSizeIds(long[] selectedSizeIds) {
        mStateHandle.set(SELECTED_SIZE_IDS, selectedSizeIds);
    }
}
