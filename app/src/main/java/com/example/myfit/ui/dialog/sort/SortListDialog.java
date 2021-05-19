package com.example.myfit.ui.dialog.sort;

import com.example.myfit.data.repository.BaseRepository;
import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.databinding.LayoutDialogSortBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SortListDialog extends BaseSortDialog {
    @Inject
    FolderRepository folderRepository;

    @Override
    protected LayoutDialogSortBinding getBinding() {
        return LayoutDialogSortBinding.inflate(getLayoutInflater());
    }

    @Override
    protected int getCheckedNumber() {
        return folderRepository.getSort();
    }

    @Override
    protected BaseRepository getRepository() {
        return folderRepository;
    }
}
