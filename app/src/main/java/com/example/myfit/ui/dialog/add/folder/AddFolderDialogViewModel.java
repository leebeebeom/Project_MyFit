package com.example.myfit.ui.dialog.add.folder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.myfit.data.repository.FolderRepository;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddFolderDialogViewModel extends ViewModel {
    public static final String FOLDER_NAME = "folder name";
    private final SavedStateHandle savedStateHandle;
    private final FolderRepository folderRepository;
    private long parentId;
    private String name;
    private final LiveData<Boolean> isExistingLive;
    private byte parentIndex;

    @Inject
    public AddFolderDialogViewModel(@NotNull SavedStateHandle savedStateHandle, FolderRepository folderRepository) {
        this.savedStateHandle = savedStateHandle;
        this.folderRepository = folderRepository;
        this.isExistingLive = Transformations.switchMap(savedStateHandle.getLiveData(FOLDER_NAME),
                name -> folderRepository.isExistingName((String) name, parentId));
    }

    public void queryIsExistingFolderName(String name, long parentId, byte parentIndex) {
        this.name = name;
        this.parentId = parentId;
        this.parentIndex = parentIndex;
        savedStateHandle.set(FOLDER_NAME, name);
    }

    public void insert() {
        folderRepository.insert(name, parentId, parentIndex);
    }

    public LiveData<Boolean> getIsExistingLive() {
        return isExistingLive;
    }
}
