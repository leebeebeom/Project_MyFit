package com.example.myfit.ui.dialog.eidttext.edit.folder;

import androidx.lifecycle.MutableLiveData;

import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditFolderNameViewModel extends BaseEditTextViewModel.BaseEditViewModel {
    private final FolderRepository folderRepository;
    private String name;
    private long id;

    @Inject
    public EditFolderNameViewModel(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public void queryIsExistingName(String inputText, long id, long parentId) {
        this.name = inputText;
        this.id = id;
        folderRepository.isExistingName(name, parentId);
    }

    @Override
    public void update() {
        folderRepository.update(id, name);
    }

    @Override
    public MutableLiveData<Boolean> getIsExistingMutable() {
        return folderRepository.getIsExistingNameLive();
    }
}
