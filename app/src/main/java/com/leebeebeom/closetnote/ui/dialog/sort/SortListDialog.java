package com.leebeebeom.closetnote.ui.dialog.sort;

import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.data.repository.FolderRepository;

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
