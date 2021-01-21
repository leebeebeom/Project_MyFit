package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.jetbrains.annotations.NotNull;

public class ItemMoveDialog extends DialogFragment {
    public interface ItemMoveConfirmClick {
        void itemMoveConfirmClick(long folderId);
    }

    private ItemMoveConfirmClick mListener;

    public ItemMoveDialog() {
    }

    public static ItemMoveDialog getInstance(int itemAmount, long id) {
        ItemMoveDialog itemMoveDialog = new ItemMoveDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("amount", itemAmount);
        bundle.putLong("id", id);
        itemMoveDialog.setArguments(bundle);
        return itemMoveDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (ItemMoveConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        int amount = 0;
        long id = 0;
        if (getArguments() != null) {
            amount = getArguments().getInt("amount");
            id = getArguments().getLong("id");
        }
        long folderId = id;
        return new MaterialAlertDialogBuilder(requireContext(), R.style.myAlertDialog)
                .setTitle("확인")
                .setMessage(amount + "개의 아이템을 이동하시겠습니까?")
                .setNegativeButton("취소", null)
                .setPositiveButton("확인", (dialog, which) -> mListener.itemMoveConfirmClick(folderId))
                .create();
    }
}
