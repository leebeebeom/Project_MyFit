package com.example.myfit.ui.dialog.sort;

import com.example.myfit.R;
import com.example.myfit.data.repository.BaseRepository;
import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.databinding.LayoutDialogSortBinding;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SortListDialog extends BaseSortDialog {
    @Inject
    FolderRepository folderRepository;
    @Inject
    LayoutDialogSortBinding binding;

    @Override
    protected LayoutDialogSortBinding getBinding() {
        return binding;
    }

    @Override
    protected int getCheckedNumber() {
        return folderRepository.getSort().getValue();
    }

    @Override
    protected BaseRepository getRepository() {
        return folderRepository;
    }

    @Override
    protected int getResId() {
        return R.id.sortListDialog;
    }
}
