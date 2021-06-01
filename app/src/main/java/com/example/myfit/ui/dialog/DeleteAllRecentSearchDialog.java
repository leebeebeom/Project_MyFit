package com.example.myfit.ui.dialog;


import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.R;
import com.example.myfit.data.repository.RecentSearchRepository;
import com.example.myfit.util.constant.RecentSearchType;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DeleteAllRecentSearchDialog extends BaseDialog {
    @Inject
    RecentSearchRepository mRecentSearchRepository;
    private int type;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO
//        this.type = DeleteAllRecentSearchDialogArgs.fromBundle(getArguments()).getType;
    }

    @Override
    protected AlertDialog getAlertDialog() {
        return mDialogBuilder.makeConfirmDialog(getString(R.string.dialog_message_recent_search_delete_all))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @NotNull
    @Contract(pure = true)
    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            mRecentSearchRepository.deleteAll(RecentSearchType.values()[type]);
            dismiss();
        };
    }

    @Override
    protected int getResId() {
        return R.id.deleteAllRecentSearchDialog;
    }
}
