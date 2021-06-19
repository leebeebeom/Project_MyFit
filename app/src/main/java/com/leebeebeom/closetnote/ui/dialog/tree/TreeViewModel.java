package com.leebeebeom.closetnote.ui.dialog.tree;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.leebeebeom.closetnote.data.repository.CategoryRepository;
import com.leebeebeom.closetnote.data.repository.FolderRepository;
import com.leebeebeom.closetnote.data.repository.SizeRepository;
import com.leebeebeom.closetnote.data.tuple.ParentIdTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.CategoryTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.FolderTuple;
import com.leebeebeom.closetnote.util.CommonUtil;

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

    private final CategoryRepository mCategoryRepository;
    private final FolderRepository mFolderRepository;
    private final SizeRepository mSizeRepository;
    private LiveData<List<ParentIdTuple>> mFolderParentIdTuplesLive, mSizeParentIdTuplesLive;
    private String mParentCategory;

    @Inject
    public TreeViewModel(CategoryRepository categoryRepository, FolderRepository folderRepository, SizeRepository sizeRepository) {
        this.mCategoryRepository = categoryRepository;
        this.mFolderRepository = folderRepository;
        this.mSizeRepository = sizeRepository;
    }

    public void setParentCategory(int parentIndex) {
        this.mParentCategory = CommonUtil.getParentCategory(parentIndex);
    }

    public LiveData<List<CategoryTuple>> getCategoryTuplesLive(int parentIndex) {
        return mCategoryRepository.getTuplesLiveByParentIndex(parentIndex);
    }

    public LiveData<List<FolderTuple>> getFolderTuplesLive(int parentIndex) {
        return mFolderRepository.getTuplesByParentIndex(parentIndex);
    }

    public LiveData<List<ParentIdTuple>> getFolderParentIdTuplesLive(long[] selectedFolderIds) {
        if (mFolderParentIdTuplesLive == null)
            mFolderParentIdTuplesLive = mFolderRepository.getParentIdTuplesByIds(selectedFolderIds);
        return mFolderParentIdTuplesLive;
    }

    public LiveData<List<ParentIdTuple>> getSizeParentIdTuplesLive(long[] selectedSizeIds) {
        if (mSizeParentIdTuplesLive == null)
            mSizeParentIdTuplesLive = mSizeRepository.getParentIdTuplesByIds(selectedSizeIds);
        return mSizeParentIdTuplesLive;
    }
}
