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

public class DeleteConFirmDialog extends DialogFragment {
    private DeleteConfirmClick mListener;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        mListener = (DeleteConfirmClick) getTargetFragment();
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        DialogInterface.OnClickListener listener = (dialog, which) -> mListener.deleteConfirmClick();
        return DialogUtils.getDialog(requireContext(), getString(R.string.delete_check), listener);
    }

    public interface DeleteConfirmClick {
        void deleteConfirmClick();
    }
}
