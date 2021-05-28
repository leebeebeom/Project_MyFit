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
    private String mName;
    private int mParentIndex;

    @Inject
    public AddFolderViewModel(FolderRepository folderRepository) {
        this.mFolderRepository = folderRepository;
    }

    public void queryIsExistingName(long parentId, String inputText, int parentIndex) {
        this.mParentId = parentId;
        this.mParentIndex = parentIndex;
        this.mName = inputText;
        mFolderRepository.isExistingName(mName, parentId);
    }

    @Override
    public void insert() {
        mFolderRepository.insert(mName, mParentId, mParentIndex);
    }

    @Override
    public MutableLiveData<Boolean> getIsExistingMutable() {
        return mFolderRepository.getIsExistingNameLive();
    }
}
