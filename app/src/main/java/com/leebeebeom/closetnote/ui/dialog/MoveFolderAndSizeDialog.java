package com.leebeebeom.closetnote.ui.dialog;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.leebeebeom.closetnote.R;

public class MoveFolderAndSizeDialog extends BaseDialog {
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
    protected AlertDialog getAlertDialog() {
        String selectedItemsSize = String.valueOf(mSelectedFolderIds.length + mSelectedSizeIds.length);
        return mDialogBuilder.makeConfirmDialog(selectedItemsSize + getString(R.string.dialog_message_item_move))
                .setPositiveClickListener(getPositiveClickListener())
                .create();
    }

    @Override
    protected View.OnClickListener getPositiveClickListener() {
        return v -> {
            if (mSelectedFolderIds.length != 0)
                getMainGraphViewModel().folderMove(mTargetId, mSelectedFolderIds);
            if (mSelectedSizeIds.length != 0)
                getMainGraphViewModel().sizeMove(mTargetId, mSelectedSizeIds);
            getBackStack().getSavedStateHandle().set(ACTION_MODE_OFF, null);
            mNavController.popBackStack(R.id.treeViewDialog, true);
        };
    }

    @Override
    protected int getResId() {
        return R.id.moveFolderAndSizeDialog;
    }
}
