package com.example.myfit.ui.dialog;


import android.view.View;

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
    RecentSearchRepository recentSearchRepository;

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

    @Override
    protected int getResId() {
        return R.id.deleteAllRecentSearchDialog;
    }
}
