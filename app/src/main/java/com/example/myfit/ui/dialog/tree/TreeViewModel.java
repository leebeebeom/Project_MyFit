package com.example.myfit.ui.dialog.tree;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;

import com.example.myfit.R;
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
public class TreeViewModel extends ViewModel {
    private static final String CATEGORY_INSERT_ID = "category insert id";
    private static final String FOLDER_INSERT_ID = "folder insert id";

    private final CategoryRepository mCategoryRepository;
    private final FolderRepository mFolderRepository;
    private final SizeRepository mSizeRepository;
    @Getter
    private final MutableLiveData<CategoryTuple> mAddedCategoryTupleLive;
    @Getter
    private final MutableLiveData<FolderTuple> mAddedFolderTupleLive;
    private final NavController mNavController;
    private LiveData<ParentIdTuple[]> mFolderParentIdTuplesLive;
    private LiveData<ParentIdTuple[]> mSizeParentIdTuplesLive;
    @Getter
    private String mParentCategory;
    private int mParentIndex;

    @Inject
    public TreeViewModel(CategoryRepository categoryRepository, FolderRepository folderRepository, SizeRepository sizeRepository, NavController navController) {
        this.mCategoryRepository = categoryRepository;
        this.mFolderRepository = folderRepository;
        this.mSizeRepository = sizeRepository;
        this.mNavController = navController;

        this.mAddedCategoryTupleLive = mCategoryRepository.getAddedTupleLive();
        this.mAddedFolderTupleLive = mFolderRepository.getAddedTupleLive();
    }

    public LiveData<List<CategoryTuple>> getCategoryTuplesLive() {
        return mCategoryRepository.getTuplesByParentIndex(mParentIndex);
    }

    public LiveData<List<FolderTuple>> getFolderTuplesLive() {
        return mFolderRepository.getTuplesByParentIndex(mParentIndex);
    }

    public LiveData<ParentIdTuple[]> getFolderParentIdTuplesLive(long[] selectedFolderIds) {
        if (mFolderParentIdTuplesLive == null)
            mFolderParentIdTuplesLive = mFolderRepository.getParentIdTuplesByIds(selectedFolderIds);
        return mFolderParentIdTuplesLive;
    }

    public LiveData<ParentIdTuple[]> getSizeParentIdTuplesLive(long[] selectedSizeIds) {
        if (mSizeParentIdTuplesLive == null)
            mSizeParentIdTuplesLive = mSizeRepository.getParentIdTuplesByIds(selectedSizeIds);
        return mSizeParentIdTuplesLive;
    }

    public void setParentCategory(int parentIndex) {
        this.mParentIndex = parentIndex;
        this.mParentCategory = CommonUtil.getParentCategory(parentIndex);
    }

    public void navigateAddCategoryDialog() {
        CommonUtil.navigate(mNavController, R.id.treeViewDialog,
                TreeViewDialogDirections.toAddCategoryDialog(mParentIndex));
    }
}
