package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.FOLDER_ID;
import static com.example.project_myfit.MyFitConstant.SELECTED_ITEM_SIZE;

public class ItemMoveDialog extends DialogFragment {
    private ItemMoveConfirmListener mListener;

    @NotNull
    public static ItemMoveDialog getInstance(int selectedItemSize, long folderId) {
        ItemMoveDialog itemMoveDialog = new ItemMoveDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(SELECTED_ITEM_SIZE, selectedItemSize);
        bundle.putLong(FOLDER_ID, folderId);
        itemMoveDialog.setArguments(bundle);
        return itemMoveDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (ItemMoveConfirmListener) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        int amount = getArguments() != null ? getArguments().getInt(SELECTED_ITEM_SIZE) : 0;
        long folderId = getArguments() != null ? getArguments().getLong(FOLDER_ID) : 0;

        String message = amount + getString(R.string.item_move_check);
        return DialogUtils.getConfirmDialog(requireContext(), message,
                (dialog, which) -> mListener.itemMoveConfirmClick(folderId));
    }

    public interface ItemMoveConfirmListener {
        void itemMoveConfirmClick(long folderId);
    }
}
