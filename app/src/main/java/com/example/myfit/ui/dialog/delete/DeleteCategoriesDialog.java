package com.example.myfit.ui.dialog.delete;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.R;
import com.example.myfit.data.repository.CategoryRepository;
import com.example.myfit.ui.dialog.BaseDialog;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeleteCategoriesDialog extends BaseDialog {
    @Inject
    CategoryRepository categoryRepository;
    private long[] selectedItemIds;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        selectedItemIds = DeleteCategoriesDialogArgs.fromBundle(getArguments()).getSelectedItemIds();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return getAlertDialog();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        String selectedItemsSize = String.valueOf(selectedItemIds.length);
        return dialogBuilder.makeConfirmDialog(selectedItemsSize + getString(R.string.dialog_message_selected_item_delete))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @NotNull
    @Contract(pure = true)
    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            categoryRepository.deleteOrRestore(selectedItemIds, true);
            dismiss();
        };
    }
}
