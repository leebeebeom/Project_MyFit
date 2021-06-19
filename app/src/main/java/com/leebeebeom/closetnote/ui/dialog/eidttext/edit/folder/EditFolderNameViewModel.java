package com.leebeebeom.closetnote.ui.dialog.eidttext.edit.folder;

import androidx.lifecycle.MutableLiveData;

import com.leebeebeom.closetnote.data.repository.FolderRepository;
import com.leebeebeom.closetnote.ui.dialog.eidttext.BaseEditTextViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EditFolderNameViewModel extends BaseEditTextViewModel.BaseEditViewModel {
    private final FolderRepository mFolderRepository;
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
