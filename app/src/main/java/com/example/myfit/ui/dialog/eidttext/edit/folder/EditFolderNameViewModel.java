package com.example.myfit.ui.dialog.eidttext.edit.folder;

import androidx.lifecycle.MutableLiveData;

import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditFolderNameViewModel extends BaseEditTextViewModel.BaseEditViewModel {
    private final FolderRepository mFolderRepository;
    private String mName;
    private long mId;

    @Inject
    public EditFolderNameViewModel(FolderRepository folderRepository) {
        this.mFolderRepository = folderRepository;
    }

    public void queryIsExistingName(long id, long parentId) {
        this.mId = id;
        mFolderRepository.isExistingName(mName, parentId);
    }

    @Override
    public void update() {
        mFolderRepository.update(mId, mName);
    }

    @Override
    public MutableLiveData<Boolean> getExistingNameLive() {
        return mFolderRepository.getExistingNameLive();
    }
}
