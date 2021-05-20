package com.example.myfit.ui.dialog.eidttext.add.folder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.ui.dialog.eidttext.add.BaseAddViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddFolderViewModel extends BaseAddViewModel {
    private final FolderRepository folderRepository;
    private long parentId;
    private String name;
    private byte parentIndex;

    @Inject
    public AddFolderViewModel(SavedStateHandle savedStateHandle, FolderRepository folderRepository) {
        super(savedStateHandle);
        this.folderRepository = folderRepository;
    }

    @Override
    protected LiveData<Boolean> query(String name) {
        return folderRepository.isExistingName((String) name, parentId);
    }

    public void setStateHandle(String inputText, long parentId, byte parentIndex) {
        this.name = inputText;
        this.parentId = parentId;
        this.parentIndex = parentIndex;
        setStateHandle(name);
    }

    @Override
    public void insert() {
        folderRepository.insert(name, parentId, parentIndex);
    }
}
