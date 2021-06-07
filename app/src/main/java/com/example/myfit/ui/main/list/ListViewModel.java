package com.example.myfit.ui.main.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.myfit.data.model.model.Folder;
import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.data.repository.SizeRepository;
import com.example.myfit.data.tuple.BaseTuple;
import com.example.myfit.data.tuple.tuple.CategoryTuple;
import com.example.myfit.data.tuple.tuple.FolderTuple;
import com.example.myfit.data.tuple.tuple.SizeTuple;
import com.example.myfit.di.Qualifiers;
import com.example.myfit.util.SizeLiveSet;
import com.example.myfit.util.constant.Sort;
import com.example.myfit.util.constant.ViewType;
import com.example.myfit.util.sharedpreferencelive.BooleanSharePreferenceLiveData;
import com.example.myfit.util.sharedpreferencelive.IntegerSharedPreferenceLiveData;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@HiltViewModel
@Accessors(prefix = "m")
public class ListViewModel extends ViewModel {
    private static final String FOLDER_ID = "folder id";

    private final CategoryRepository mCategoryRepository;
    private final FolderRepository mFolderRepository;
    private final SizeRepository mSizeRepository;
    private final SavedStateHandle mSavedStateHandle;
    @Getter
    private final SizeLiveSet<FolderTuple> mSelectedFolders;
    @Getter
    private final SizeLiveSet<SizeTuple> mSelectedSizes;
    @Getter
    private final BooleanSharePreferenceLiveData mFolderTogglePreferenceLive;
    @Getter
    private final IntegerSharedPreferenceLiveData mViewTypePreferenceLive;
    private LiveData<CategoryTuple> mCategoryLive;
    private MediatorLiveData<Object> mParentLive;
    @Getter
    private final LiveData<List<FolderTuple>> mFolderPathTuplesLive;
    private LiveData<List<FolderTuple>> mFolderTuplesLive;
    private final LiveData<Boolean> mFavoriteViewLive = new MutableLiveData<>();
    private LiveData<List<SizeTuple>> mSizeTuplesLive;
    private MediatorLiveData<Integer> mSelectedSizeLive;
    @Getter
    private final MutableLiveData<Boolean> mFavoriteLive = new MutableLiveData<>(false);
    @Setter
    private long mParentId;

    @Inject
    public ListViewModel(SavedStateHandle savedStateHandle,
                         CategoryRepository categoryRepository,
                         FolderRepository folderRepository,
                         SizeRepository sizeRepository,
                         SizeLiveSet<FolderTuple> selectedFolders,
                         SizeLiveSet<SizeTuple> selectedSizes,
                         @Qualifiers.FolderTogglePreferenceLive BooleanSharePreferenceLiveData folderTogglePreferenceLive,
                         @Qualifiers.ViewTypePreferenceLive IntegerSharedPreferenceLiveData viewTypePreferenceLive) {
        this.mSavedStateHandle = savedStateHandle;
        this.mCategoryRepository = categoryRepository;
        this.mFolderRepository = folderRepository;
        this.mSizeRepository = sizeRepository;
        this.mSelectedFolders = selectedFolders;
        this.mSelectedSizes = selectedSizes;
        this.mFolderTogglePreferenceLive = folderTogglePreferenceLive;
        this.mViewTypePreferenceLive = viewTypePreferenceLive;

        mFolderPathTuplesLive = Transformations.switchMap(mSavedStateHandle.getLiveData(FOLDER_ID),
                id -> mFolderRepository.getFolderPathTuples((Long) id));
    }

    public LiveData<CategoryTuple> getCategoryLive(long categoryId) {
        if (mCategoryLive == null)
            mCategoryLive = mCategoryRepository.getTupleLiveById(categoryId);
        return mCategoryLive;
    }

    public LiveData<Object> getParentLive(long mParentId) {
        if (mParentLive == null) {
            LiveData<CategoryTuple> categoryTupleLive = mCategoryRepository.getTupleLiveById(mParentId);
            LiveData<Folder> folderTupleLive = mFolderRepository.getSingleLiveById(mParentId);
            mParentLive.addSource(categoryTupleLive, categoryTuple -> {
                if (categoryTuple != null) mParentLive.setValue(categoryTuple);
            });
            mParentLive.addSource(folderTupleLive, folderTuple -> {
                if (folderTuple != null) mParentLive.setValue(folderTuple);
            });
        }
        return mParentLive;
    }

    public void setStateHandleFolderId(long id) {
        mSavedStateHandle.set(FOLDER_ID, id);
    }

    public LiveData<List<FolderTuple>> getFolderTuplesLive() {
        if (mFolderTuplesLive == null)
            mFolderTuplesLive = mFolderRepository.getTuplesLiveByParentId(mParentId);
        return mFolderTuplesLive;
    }

    public Sort getSort() {
        return mFolderRepository.getSort();
    }

    public LiveData<List<SizeTuple>> getSizeLive() {
        if (mSizeTuplesLive == null)
            mSizeTuplesLive = mSizeRepository.getTuplesLiveByParentId(mParentId);
        return mSizeTuplesLive;
    }

    public LiveData<Integer> getSelectedItemSizeLive() {
        if (mSelectedSizeLive == null) {
            mSelectedSizeLive = new MediatorLiveData<>();
            mSelectedSizeLive.addSource(mSelectedFolders.getLiveData(), integer -> {
                int size = mSelectedSizes.getLiveDataValue() + integer;
                mSelectedSizeLive.setValue(size);
            });
            mSelectedSizeLive.addSource(mSelectedSizes.getLiveData(), integer -> {
                int size = mSelectedFolders.getLiveDataValue() + integer;
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
        return mSelectedFolders.stream().mapToLong(BaseTuple::getId).toArray();
    }

    public long[] getSelectedSizeIds() {
        return mSelectedSizes.stream().mapToLong(BaseTuple::getId).toArray();
    }

    public void folderDragDrop(List<FolderTuple> newOrderList) {
        List<FolderTuple> newOrderSelectedItems =
                newOrderList.stream()
                        .filter(mSelectedFolders::contains)
                        .collect(Collectors.toList());
        mSelectedFolders.clear();
        mSelectedFolders.addAll(newOrderSelectedItems);

        mFolderRepository.updateTuples(newOrderList);
    }

    public void sizeDragDrop(List<SizeTuple> newOrderList) {
        List<SizeTuple> newOrderSelectedItems =
                newOrderList.stream()
                        .filter(mSelectedSizes::contains)
                        .collect(Collectors.toList());
        mSelectedSizes.clear();
        mSelectedSizes.addAll(newOrderSelectedItems);

        mSizeRepository.updateTuples(newOrderList);
    }

    public void sizeTupleUpdate(SizeTuple tuple) {
        mSizeRepository.update(tuple);
    }
}