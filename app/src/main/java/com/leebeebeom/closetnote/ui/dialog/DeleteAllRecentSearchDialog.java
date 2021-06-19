package com.leebeebeom.closetnote.ui.dialog;


import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.leebeebeom.closetnote.R;
import com.leebeebeom.closetnote.util.constant.RecentSearchType;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class DeleteAllRecentSearchDialog extends BaseDialog {
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
            getMainGraphViewModel().recentSearchRepositoryDeleteAll(RecentSearchType.values()[type]);
            dismiss();
        };
    }

    @Override
    protected int getResId() {
        return R.id.deleteAllRecentSearchDialog;
    }
}
