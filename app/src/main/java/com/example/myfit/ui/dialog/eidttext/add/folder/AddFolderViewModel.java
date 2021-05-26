package com.example.myfit.ui.dialog.eidttext.add.folder;

import androidx.lifecycle.MutableLiveData;

import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddFolderViewModel extends BaseEditTextViewModel.BaseAddViewModel {
    private final FolderRepository folderRepository;
    private long parentId;
    private String name;
    private int parentIndex;

    @Inject
    public AddFolderViewModel(FolderRepository folderRepository) {
        this.folderRepository = folderRepository;
    }

    public void queryIsExistingName(long parentId, String inputText, int parentIndex) {
        this.parentId = parentId;
        this.parentIndex = parentIndex;
        this.name = inputText;
        folderRepository.isExistingName(name, parentId);
    }

    @Override
    public void insert() {
        folderRepository.insert(name, parentId, parentIndex);
    }

    @Override
    public MutableLiveData<Boolean> getIsExistingMutable() {
        return folderRepository.getIsExistingNameLive();
    }
}
