package com.leebeebeom.closetnote.ui.main.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.leebeebeom.closetnote.data.repository.CategoryRepository;
import com.leebeebeom.closetnote.data.repository.FolderRepository;
import com.leebeebeom.closetnote.data.repository.SizeRepository;
import com.leebeebeom.closetnote.data.tuple.BaseTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.FolderTuple;
import com.leebeebeom.closetnote.data.tuple.tuple.FolderTuple2;
import com.leebeebeom.closetnote.data.tuple.tuple.SizeTuple;
import com.leebeebeom.closetnote.util.CommonUtil;
import com.leebeebeom.closetnote.util.SizeLiveSet;
import com.leebeebeom.closetnote.util.constant.Sort;
import com.leebeebeom.closetnote.util.constant.ViewType;
import com.leebeebeom.closetnote.util.sharedpreferencelive.BooleanSharedPreferenceLiveData;
import com.leebeebeom.closetnote.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.experimental.Accessors;

@HiltViewModel
@Accessors(prefix = "m")
@Getter
public class ListViewModel extends ViewModel {
    private static final String FOLDER_ID = "folder id";
    private static final String PARENT_ID = "parent id";
    private static final String CATEGORY_ID = "category id";

    private final SavedStateHandle mSavedStateHandle;

    private final CategoryRepository mCategoryRepository;
    private final FolderRepository mFolderRepository;
    private final SizeRepository mSizeRepository;

    private MediatorLiveData<Object> mParentLive;

    private final SizeLiveSet<FolderTuple> mSelectedFolderTuples;
    private final SizeLiveSet<SizeTuple> mSelectedSizeTuples;

    private final BooleanSharedPreferenceLiveData mFolderTogglePreferenceLive;
    private final IntegerSharedPreferenceLiveData mViewTypePreferenceLive, mListSortPreferenceLive;

    private final LiveData<List<FolderTuple>> mFolderPathTuplesLive, mFolderTuplesLive;
    private final LiveData<List<SizeTuple>> mSizeTuplesLive;
    private MediatorLiveData<Integer> mSelectedSizeLive;
    private final MutableLiveData<Boolean> mFavoriteLive = new MutableLiveData<>(false);
    private final LiveData<String> mCategoryNameLive;

    @Inject
    public ListViewModel(SavedStateHandle savedStateHandle,
                         CategoryRepository categoryRepository,
                         FolderRepository folderRepository,
                         SizeRepository sizeRepository,
                         SizeLiveSet<FolderTuple> selectedFolderTuples,
                         SizeLiveSet<SizeTuple> selectedSizeTuples) {
        this.mSavedStateHandle = savedStateHandle;
        this.mCategoryRepository = categoryRepository;
        this.mFolderRepository = folderRepository;
        this.mSizeRepository = sizeRepository;
        this.mSelectedFolderTuples = selectedFolderTuples;
        this.mSelectedSizeTuples = selectedSizeTuples;

        this.mListSortPreferenceLive = mFolderRepository.getListSortPreferenceLive();
        this.mFolderTogglePreferenceLive = mFolderRepository.getFolderTogglePreferenceLive();
        this.mViewTypePreferenceLive = mSizeRepository.getViewTypePreferenceLive();

        mFolderPathTuplesLive = Transformations.switchMap(mSavedStateHandle.getLiveData(FOLDER_ID),
                id -> mFolderRepository.getFolderPathTuples((Long) id));

        mFolderTuplesLive = Transformations.switchMap(mSavedStateHandle.getLiveData(PARENT_ID),
                id -> mFolderRepository.getTuplesLiveByParentId((Long) id));

        mSizeTuplesLive = Transformations.switchMap(mSavedStateHandle.getLiveData(PARENT_ID),
                id -> mSizeRepository.getTuplesLiveByParentId((Long) id));

        mCategoryNameLive = Transformations.switchMap(mSavedStateHandle.getLiveData(CATEGORY_ID),
                id -> mCategoryRepository.getNameLive((Long) id));
    }

    public void setParentId(long parentId) {
        mSavedStateHandle.set(PARENT_ID, parentId);
    }

    public void setCategoryId(long categoryId) {
        mSavedStateHandle.set(CATEGORY_ID, categoryId);
    }

    public LiveData<Object> getParentLive(long mParentId) {
        if (mParentLive == null) {
            //TODO
//            LiveData<String> categoryNameLive = mCategoryRepository.getNameLive(mParentId);
//            TODO 만들기
//            LiveData<FolderTuple2> folderTuple2Live = mFolderRepository.getSingleLiveById(mParentId);
//            mParentLive = new MediatorLiveData<>();
//            mParentLive.addSource(categoryNameLive, categoryName -> {
//                if (!categoryName.equals("")) mParentLive.setValue(categoryName);
//            });
//            mParentLive.addSource(folderTuple2Live, folderTuple2 -> {
//                if (folderTuple2 != null) mParentLive.setValue(folderTuple2);
//            });
        }
        return mParentLive;
    }

    public void setFolderId(long id) {
        mSavedStateHandle.set(FOLDER_ID, id);
    }

    public Sort getSort() {
        return mFolderRepository.getSort();
    }

    public LiveData<Integer> getSelectedSizeLive() {
        if (mSelectedSizeLive == null) {
            mSelectedSizeLive = new MediatorLiveData<>();
            mSelectedSizeLive.addSource(mSelectedFolderTuples.getLiveData(), integer -> {
                int size = mSelectedSizeTuples.getLiveDataValue() + integer;
                mSelectedSizeLive.setValue(size);
            });
            mSelectedSizeLive.addSource(mSelectedSizeTuples.getLiveData(), integer -> {
                int size = mSelectedFolderTuples.getLiveDataValue() + integer;
                mSelectedSizeLive.setValue(size);
            });
        }
        return mSelectedSizeLive;
    }

    public long[] getFolderPathCompleteIds() {
        List<FolderTuple> folderPathTuples = mFolderPathTuplesLive.getValue();
        if (folderPathTuples != null)
            return folderPathTuples.stream().mapToLong(BaseTuple::getId).toArray();
        else return new long[0];
    }

    public void changeFolderToggleState() {
        mFolderRepository.folderToggleChange();
    }

    public ViewType getViewType() {
        return mSizeRepository.getViewType();
    }

    public void changeViewType() {
        mSizeRepository.changeViewType();
    }

    public void changeFavorite() {
        if (mFavoriteLive.getValue() != null)
            mFavoriteLive.setValue(!mFavoriteLive.getValue());
    }

    public boolean isFavorite() {
        if (mFavoriteLive.getValue() != null)
            return mFavoriteLive.getValue();
        else return false;
    }

    public long[] getSelectedFolderIds() {
        return mSelectedFolderTuples.stream().mapToLong(BaseTuple::getId).toArray();
    }

    public long[] getSelectedSizeIds() {
        return mSelectedSizeTuples.stream().mapToLong(BaseTuple::getId).toArray();
    }

    public void folderDragDrop(List<FolderTuple> newOrderList) {
        CommonUtil.replaceNewOrderSelectedItems(newOrderList, mSelectedFolderTuples);
        mFolderRepository.updateTuples(newOrderList);
    }

    public void sizeDragDrop(List<SizeTuple> newOrderList) {
        CommonUtil.replaceNewOrderSelectedItems(newOrderList, mSelectedSizeTuples);
        mSizeRepository.updateTuples(newOrderList);
    }

    public void sizeTupleUpdate(SizeTuple tuple) {
        mSizeRepository.update(tuple);
    }
}