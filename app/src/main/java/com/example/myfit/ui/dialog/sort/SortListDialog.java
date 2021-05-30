package com.example.myfit.ui.dialog.sort;

import com.example.myfit.R;
import com.example.myfit.data.repository.FolderRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SortListDialog extends BaseSortDialog {
    @Inject
    FolderRepository mFolderRepository;

    @Override
    protected int getCheckedNumber() {
        return mFolderRepository.getSort().getValue();
    }

    @Override
    protected FolderRepository getRepository() {
        return mFolderRepository;
    }

    @Override
    protected int getResId() {
        return R.id.sortListDialog;
    }
}
