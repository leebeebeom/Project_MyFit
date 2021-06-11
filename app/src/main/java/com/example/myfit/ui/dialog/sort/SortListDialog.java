package com.example.myfit.ui.dialog.sort;

import com.example.myfit.R;
import com.example.myfit.data.repository.FolderRepository;

public class SortListDialog extends BaseSortDialog {

    @Override
    protected FolderRepository getRepository() {
        return getMainGraphViewModel().getFolderRepository();
    }

    @Override
    protected int getResId() {
        return R.id.sortListDialog;
    }
}
