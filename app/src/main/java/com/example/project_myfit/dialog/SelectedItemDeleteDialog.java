package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.AMOUNT;

public class SelectedItemDeleteDialog extends DialogFragment {
    private SelectedItemDeleteConfirmClick mListener;

    public SelectedItemDeleteDialog() {
    }

    @NotNull
    public static SelectedItemDeleteDialog getInstance(int selectedItemAmount) {
        SelectedItemDeleteDialog selectedItemDeleteDialog = new SelectedItemDeleteDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(AMOUNT, selectedItemAmount);
        selectedItemDeleteDialog.setArguments(bundle);
        return selectedItemDeleteDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (SelectedItemDeleteConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        int amount = 0;
        if (getArguments() != null) amount = getArguments().getInt(AMOUNT);
        String message = amount + getString(R.string.selected_item_delete_check);
        return DialogUtils.getDialog(requireContext(), message,
                (dialog, which) -> mListener.selectedItemDeleteConfirmClick());
    }

    public interface SelectedItemDeleteConfirmClick {
        void selectedItemDeleteConfirmClick();
    }
}
