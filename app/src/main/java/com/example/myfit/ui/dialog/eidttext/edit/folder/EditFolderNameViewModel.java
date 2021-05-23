package com.example.myfit.ui.dialog.eidttext.edit.folder;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;

import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.ui.dialog.eidttext.edit.BaseEditViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditFolderNameViewModel extends BaseEditViewModel {
    private final FolderRepository folderRepository;
    private String name;
    private long id, parentId;

    @Inject
    public EditFolderNameViewModel(SavedStateHandle savedStateHandle, FolderRepository folderRepository) {
        super(savedStateHandle);
        this.folderRepository = folderRepository;
    }

    @Override
    protected LiveData<Boolean> queryIsExistingLive(String name) {
        return folderRepository.isExistingName((String) name, parentId);
    }

    public void setStateHandle(long id, String inputText, long parentId) {
        this.id = id;
        this.name = inputText;
        this.parentId = parentId;
        setStateHandle(name);
    }

    @Override
    public void update() {
        folderRepository.update(id, name);
    }
}
