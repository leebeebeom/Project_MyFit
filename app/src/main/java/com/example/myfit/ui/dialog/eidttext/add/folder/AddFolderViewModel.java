package com.example.myfit.ui.dialog.eidttext.add.folder;

import androidx.lifecycle.MutableLiveData;

import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.ui.dialog.eidttext.BaseEditTextViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddFolderViewModel extends BaseEditTextViewModel.BaseAddViewModel {
    private final FolderRepository mFolderRepository;
    private long mParentId;
    private int mParentIndex;

    @Inject
    public AddFolderViewModel(FolderRepository folderRepository) {
        this.mFolderRepository = folderRepository;
    }

    public void queryIsExistingName(long parentId, int parentIndex) {
        this.mParentId = parentId;
        this.mParentIndex = parentIndex;
        mFolderRepository.isExistingName(mName, parentId);
    }

    @Override
    public void insert() {
        mFolderRepository.insert(mName, mParentId, mParentIndex);
    }

    @Override
    public MutableLiveData<Boolean> getExistingNameLive() {
        return mFolderRepository.getExistingNameLive();
    }
}
