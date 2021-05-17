package com.example.myfit.ui.dialog;


import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.R;
import com.example.myfit.data.repository.RecentSearchRepository;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeleteAllRecentSearchDialog extends BaseDialog {
    @Inject
    DialogBindingBuilder dialogBindingBuilder;
    @Inject
    RecentSearchRepository recentSearchRepository;

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return getAlertDialog();
    }

    @Override
    protected AlertDialog getAlertDialog() {
        return dialogBuilder.makeConfirmDialog(getString(R.string.dialog_message_recent_search_delete_all))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @NotNull
    @Contract(pure = true)
    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            recentSearchRepository.deleteAll();
            dismiss();
        };
    }
}
