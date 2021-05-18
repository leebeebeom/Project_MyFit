package com.example.myfit.ui.dialog.delete;

import com.example.myfit.R;
import com.example.myfit.data.repository.BaseRepository;
import com.example.myfit.data.repository.SizeRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeleteSizesDialog extends BaseDeleteDialog {
    @Inject
    SizeRepository sizeRepository;

    @Override
    protected String getMessage() {
        return getString(R.string.dialog_message_selected_item_delete);
    }

    @Override
    protected long[] getSelectedItemIds() {
        return DeleteSizesDialogArgs.fromBundle(getArguments()).getSelectedItemIds();
    }

    @Override
    protected BaseRepository getRepository() {
        return sizeRepository;
    }
}
