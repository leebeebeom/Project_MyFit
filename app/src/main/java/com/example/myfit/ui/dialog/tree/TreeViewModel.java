package com.example.myfit.ui.dialog.tree;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.myfit.data.model.category.CategoryTuple;
import com.example.myfit.data.model.folder.FolderTuple;
import com.example.myfit.data.model.tuple.ParentIdTuple;
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

    private final SavedStateHandle savedStateHandle;
    private final CategoryRepository categoryRepository;
    private final FolderRepository folderRepository;
    private final SizeRepository sizeRepository;
    private LiveData<ParentIdTuple[]> folderParentIdTuplesLive, sizeParentIdTuplesLive;
    private final MediatorLiveData<CategoryTuple> categoryTupleMutable = new MediatorLiveData<>();
    private final MediatorLiveData<FolderTuple> folderTupleMutable = new MediatorLiveData<>();

    @Inject
    public TreeViewModel(SavedStateHandle savedStateHandle, CategoryRepository categoryRepository, FolderRepository folderRepository, SizeRepository sizeRepository) {
        this.savedStateHandle = savedStateHandle;
        this.categoryRepository = categoryRepository;
        this.folderRepository = folderRepository;
        this.sizeRepository = sizeRepository;

        categoryTupleMutableAddSource();
        folderTupleMutableAddSource();
    }

    private void categoryTupleMutableAddSource() {
        LiveData<CategoryTuple> categoryTupleLive = Transformations.switchMap(savedStateHandle.getLiveData(CATEGORY_INSERT_ID),
                insertId -> categoryRepository.getTupleById((Long) insertId));
        categoryTupleMutable.addSource(categoryTupleLive, categoryTupleMutable::setValue);
    }

    private void folderTupleMutableAddSource() {
        LiveData<FolderTuple> folderTupleLive = Transformations.switchMap(savedStateHandle.getLiveData(FOLDER_INSERT_ID),
                insertId -> folderRepository.getTupleById((Long) insertId));
        folderTupleMutable.addSource(folderTupleLive, folderTupleMutable::setValue);
    }

    public LiveData<List<CategoryTuple>> getCategoryTuplesLive(int parentIndex) {
        return categoryRepository.getTuplesByParentIndex(parentIndex);
    }

    public LiveData<List<FolderTuple>> getFolderTuplesLive(int parentIndex) {
        return folderRepository.getTuplesByParentIndex(parentIndex);
    }

    public LiveData<ParentIdTuple[]> getFolderParentIdTuplesLive(long[] selectedFolderIds) {
        if (folderParentIdTuplesLive == null)
            folderParentIdTuplesLive = folderRepository.getParentIdTuplesByIds(selectedFolderIds);
        return folderParentIdTuplesLive;
    }

    public LiveData<ParentIdTuple[]> getSizeParentIdTuplesLive(long[] selectedSizeIds) {
        if (sizeParentIdTuplesLive == null)
            sizeParentIdTuplesLive = sizeRepository.getParentIdTuplesByIds(selectedSizeIds);
        return sizeParentIdTuplesLive;
    }

    public MutableLiveData<Long> getCategoryInsertIdLive() {
        return categoryRepository.getInsertIdLive();
    }

    public MutableLiveData<Long> getFolderInsertIdLive() {
        return folderRepository.getInsertIdLive();
    }

    public MutableLiveData<CategoryTuple> getCategoryTupleMutable() {
        return categoryTupleMutable;
    }

    public void setCategoryInsertId(long id) {
        savedStateHandle.set(CATEGORY_INSERT_ID, id);
    }

    public MutableLiveData<FolderTuple> getFolderTupleMutable() {
        return folderTupleMutable;
    }

    public void setFolderInsertId(long id) {
        savedStateHandle.set(FOLDER_INSERT_ID, id);
    }

}
