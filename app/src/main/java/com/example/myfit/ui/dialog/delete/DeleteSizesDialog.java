package com.example.myfit.ui.dialog.delete;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.R;
import com.example.myfit.data.repository.SizeRepository;
import com.example.myfit.ui.dialog.BaseDialog;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeleteSizesDialog extends BaseDialog {
    @Inject
    SizeRepository sizeRepository;
    private long[] selectedItemIds;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedItemIds = DeleteSizesDialogArgs.fromBundle(getArguments()).getSelectedItemIds();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        String selectedItemsSize = String.valueOf(selectedItemIds.length);
        return dialogBuilder.makeConfirmDialog(selectedItemsSize + getString(R.string.dialog_message_selected_item_delete))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            sizeRepository.deleteOrRestore(selectedItemIds, true);
            dismiss();
        };
    }
}
