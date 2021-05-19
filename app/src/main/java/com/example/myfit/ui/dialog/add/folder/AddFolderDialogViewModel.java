package com.example.myfit.ui.dialog.add.folder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.ui.dialog.BaseDialogViewModel;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddFolderDialogViewModel extends BaseDialogViewModel {
    public static final String FOLDER_NAME = "folder name";
    private final SavedStateHandle savedStateHandle;
    private final FolderRepository folderRepository;
    private long parentId;
    private String name;
    private byte parentIndex;

    @Inject
    public AddFolderDialogViewModel(@NotNull SavedStateHandle savedStateHandle, FolderRepository folderRepository) {
        this.savedStateHandle = savedStateHandle;
        this.folderRepository = folderRepository;
    }

    @Override
    public void insert() {
        folderRepository.insert(name, parentId, parentIndex);
    }

    @Override
    public void update() {

    }

    @Override
    protected LiveData<Boolean> getIsExistingLive() {
        return Transformations.switchMap(savedStateHandle.getLiveData(FOLDER_NAME),
                name -> folderRepository.isExistingName((String) name, parentId));
    }

    @Override
    public void queryIsExistingName(String inputText, byte parentIndex) {
        
    }

    @Override
    public void queryIsExistingName(String inputText, long parentId, byte parentIndex) {
        this.name = inputText;
        this.parentId = parentId;
        this.parentIndex = parentIndex;
        savedStateHandle.set(FOLDER_NAME, name);
    }

    @Override
    public void queryIsExistingName(long id, String inputText, byte parentIndex) {

    }

    @Override
    public void queryIsExistingName(long id, String inputText, long parentId) {

    }
}
