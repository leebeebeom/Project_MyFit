package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;

import org.jetbrains.annotations.NotNull;

public class SelectedItemDeleteDialog extends DialogFragment {
    private SelectedItemDeleteConfirmListener mListener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (SelectedItemDeleteConfirmListener) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        int selectedItemSize = SelectedItemDeleteDialogArgs.fromBundle(getArguments()).getSelectedItemSize();
        String message = selectedItemSize + getString(R.string.selected_item_delete_check);
        return DialogUtils.getConfirmDialog(requireContext(), message,
                (dialog, which) -> mListener.selectedItemDeleteConfirmClick());
    }

    public interface SelectedItemDeleteConfirmListener {
        void selectedItemDeleteConfirmClick();
    }
}
