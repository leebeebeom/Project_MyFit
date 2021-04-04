package com.example.project_myfit.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.project_myfit.R;

import org.jetbrains.annotations.NotNull;

import static com.example.project_myfit.MyFitConstant.SELECTED_ITEM_SIZE;

public class SelectedItemDeleteDialog extends DialogFragment {
    private SelectedItemDeleteConfirmListener mListener;

    public SelectedItemDeleteDialog() {
    }

    @NotNull
    public static SelectedItemDeleteDialog getInstance(int selectedItemSize) {
        //checked
        SelectedItemDeleteDialog selectedItemDeleteDialog = new SelectedItemDeleteDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(SELECTED_ITEM_SIZE, selectedItemSize);
        selectedItemDeleteDialog.setArguments(bundle);
        return selectedItemDeleteDialog;
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        //checked
        super.onAttach(context);
        mListener = (SelectedItemDeleteConfirmListener) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        //checked
        int selectedItemSize = 0;
        if (getArguments() != null) selectedItemSize = getArguments().getInt(SELECTED_ITEM_SIZE);
        String message = selectedItemSize + getString(R.string.selected_item_delete_check);
        return DialogUtils.getConfirmDialog(requireContext(), message,
                (dialog, which) -> mListener.selectedItemDeleteConfirmClick());
    }

    public interface SelectedItemDeleteConfirmListener {
        void selectedItemDeleteConfirmClick();
    }
}
