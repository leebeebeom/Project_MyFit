package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;

import org.jetbrains.annotations.NotNull;

public class DeleteConfirmDialog extends DialogFragment {

    private long mSizeId;
    private int mNavGraphId;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSizeId = DeleteConfirmDialogArgs.fromBundle(getArguments()).getSizeId();
        mNavGraphId = DeleteConfirmDialogArgs.fromBundle(getArguments()).getNavGraphId();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogUtil dialogUtil = new DialogUtil(requireContext(), this, mNavGraphId)
                .setValueBackStackLive(R.id.deleteConfirmDialog);

        AlertDialog alertDialog = dialogUtil.getConfirmDialog(getString(R.string.dialog_message_delete));

        Button positiveButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            dialogUtil.getDialogViewModel().deleteSize(mSizeId);
        /*
        TODO
        서치뷰에서 삭제 시 액티비티 종료 되는지 and 백스택 쌓였을때 뒤로가기 되는지
        사이즈 프래그먼트에서 프래그먼트 종료 되는지
        정상 종료 안될시

        sizeFragment -> dialogLive -> delete 복구
         */
//        mNavBackStackEntry.getSavedStateHandle().set(SIZE_DELETE_CONFIRM, null);
            dialogUtil.getNavController().popBackStack(R.id.sizeFragment, true);
        });
        return alertDialog;
    }
}
