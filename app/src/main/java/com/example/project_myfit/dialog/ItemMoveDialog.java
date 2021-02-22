package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.AMOUNT;
import static com.example.project_myfit.MyFitConstant.ID;

public class ItemMoveDialog extends DialogFragment {
    private ItemMoveConfirmClick mListener;

    public ItemMoveDialog() {
    }

    @NotNull
    public static ItemMoveDialog getInstance(int selectedItemAmount, long id) {
        ItemMoveDialog itemMoveDialog = new ItemMoveDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(AMOUNT, selectedItemAmount);
        bundle.putLong(ID, id);
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
            amount = getArguments().getInt(AMOUNT);
            id = getArguments().getLong(ID);
        }
        long finalId = id;
        String message = amount + getString(R.string.item_move_check);
        DialogInterface.OnClickListener listener = (dialog, which) -> mListener.itemMoveConfirmClick(finalId);
        return DialogUtils.getDialog(requireContext(), message, listener);
    }

    public interface ItemMoveConfirmClick {
        void itemMoveConfirmClick(long folderId);
    }
}
