package com.example.myfit.ui.dialog;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.myfit.R;
import com.example.myfit.data.repository.FolderRepository;
import com.example.myfit.data.repository.SizeRepository;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MoveFolderAndSizeDialog extends BaseDialog {
    @Inject
    FolderRepository mFolderRepository;
    @Inject
    SizeRepository mSizeRepository;
    private long[] mSelectedFolderIds, mSelectedSizeIds;
    private long mTargetId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSelectedFolderIds = MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getSelectedFolderIds();
        mSelectedSizeIds = MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getSelectedSizeIds();
        mTargetId = MoveFolderAndSizeDialogArgs.fromBundle(getArguments()).getTargetId();
        setBackStackLive();
    }

    @Override
    protected AlertDialog getAlertDialog(DialogBuilder dialogBuilder) {
        String selectedItemsSize = String.valueOf(mSelectedFolderIds.length + mSelectedSizeIds.length);
        return dialogBuilder.makeConfirmDialog(selectedItemsSize + getString(R.string.dialog_message_item_move))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            if (mSelectedFolderIds.length != 0)
                mFolderRepository.move(mTargetId, mSelectedFolderIds);
            if (mSelectedSizeIds.length != 0)
                mSizeRepository.move(mTargetId, mSelectedSizeIds);
            getBackStack().getSavedStateHandle().set(ACTION_MODE_OFF, null);
            getNavController().popBackStack(R.id.treeViewDialog, true);
        };
    }

    @Override
    protected int getResId() {
        return R.id.moveFolderAndSizeDialog;
    }
}
